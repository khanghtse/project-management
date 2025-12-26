package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.*;
import khanghtse.com.projectmanagement.entities.*;
import khanghtse.com.projectmanagement.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {

    private final TaskRepository taskRepository;
    private final TaskColumnRepository taskColumnRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ILexorankService lexorankService;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    // Lấy dữ liệu Board (Cột + Tasks)
    @Override
    public BoardResponse getBoard(UUID projectId) {
        // 1. Tìm Project để lấy Workspace ID
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        UUID workspaceId = project.getWorkspace().getId();

        // 2. Lấy danh sách cột và tasks
        List<TaskColumn> columns = taskColumnRepository.findByProjectIdOrderByPositionAsc(projectId);

        List<ColumnResponse> columnResponses = columns.stream().map(col -> {
            List<Task> tasks = taskRepository.findByColumnIdAndParentTaskIsNullOrderByPositionAsc(col.getId());
            List<TaskResponse> taskResponses = tasks.stream().map(this::mapToTaskDto).collect(Collectors.toList());
            return new ColumnResponse(col.getId(), col.getName(), taskResponses);
        }).collect(Collectors.toList());

        // 3. Trả về BoardResponse kèm workspaceId
        return new BoardResponse(projectId, workspaceId, columnResponses);
    }

    @Override
    public BoardResponse getBoard(UUID projectId, String keyword, String priority, Boolean isMyTask, String currentUserEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        UUID workspaceId = project.getWorkspace().getId();

        // 1. Xử lý bộ lọc User
        UUID assigneeId = null;
        if (Boolean.TRUE.equals(isMyTask)) {
            User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);
            if (currentUser != null) assigneeId = currentUser.getId();
        }

        // 2. Xử lý bộ lọc Keyword (Search) - QUAN TRỌNG: Xử lý chuỗi tại đây
        String searchPattern = null;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Chuyển về chữ thường và thêm % bao quanh để tìm kiếm tương đối (LIKE)
            searchPattern = "%" + keyword.trim().toLowerCase() + "%";
        }

        // 3. Lấy danh sách cột
        List<TaskColumn> columns = taskColumnRepository.findByProjectIdOrderByPositionAsc(projectId);

        // 4. Gọi Repository với searchPattern đã xử lý
        List<Task> allTasks = taskRepository.findTasksByProjectAndFilters(projectId, searchPattern, priority, assigneeId);

        // 5. Group tasks theo Column ID
        Map<UUID, List<Task>> tasksByColumn = allTasks.stream()
                .collect(Collectors.groupingBy(t -> t.getColumn().getId()));

        // 6. Map dữ liệu trả về
        List<ColumnResponse> columnResponses = columns.stream().map(col -> {
            List<Task> tasksInCol = tasksByColumn.getOrDefault(col.getId(), new ArrayList<>());
            List<TaskResponse> taskResponses = tasksInCol.stream()
                    .map(this::mapToTaskDto)
                    .collect(Collectors.toList());
            return new ColumnResponse(col.getId(), col.getName(), taskResponses);
        }).collect(Collectors.toList());

        return new BoardResponse(projectId, workspaceId, columnResponses);
    }

    @Override
    @Transactional
    public TaskResponse createTask(UUID projectId, CreateTaskRequest request, String userEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        TaskColumn column = taskColumnRepository.findById(request.getColumnId())
                .orElseThrow(() -> new RuntimeException("Column not found"));

        // Validate: Cột phải thuộc dự án này
        if (!column.getProject().getId().equals(projectId)) {
            throw new RuntimeException("Column does not belong to this project");
        }

        // 1. Tăng số thứ tự task (Atomic update nên làm ở DB level trong thực tế)
        project.setCurrentTaskNumber(project.getCurrentTaskNumber() + 1);
        projectRepository.save(project);

        // 2. Tính rank: Mặc định chèn vào cuối cột
        // Logic đơn giản: lấy rank cuối cùng + 'z' hoặc dùng Lexorank
        String newRank = lexorankService.getRankBetween(null, null); // Demo: ở giữa 0 và z -> n
        // Thực tế: Cần query task cuối cùng trong cột để lấy rank, sau đó tính rank tiếp theo.
        // Để đơn giản cho demo, ta tạm dùng cơ chế sinh ngẫu nhiên hoặc logic đơn giản.
        // Nếu muốn chèn cuối: getRankBetween(lastTask.position, null)

        List<Task> tasksInCol = taskRepository.findByColumnIdAndParentTaskIsNullOrderByPositionAsc(column.getId());
        if (!tasksInCol.isEmpty()) {
            String lastRank = tasksInCol.get(tasksInCol.size() - 1).getPosition();
            newRank = lexorankService.getRankBetween(lastRank, null);
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setProject(project);
        task.setColumn(column);
        task.setTaskNumber(project.getCurrentTaskNumber());
        task.setPosition(newRank);
        task.setDueDate(request.getDueDate());

        // --- XỬ LÝ NHIỀU ASSIGNEES ---
        if (request.getAssigneeIds() != null && !request.getAssigneeIds().isEmpty()) {
            Set<User> assignees = validateAndGetAssignees(project.getWorkspace().getId(), request.getAssigneeIds());
            task.setAssignees(assignees);
        }

//        if (request.getAssigneeId() != null) {
//            User assignee = userRepository.findById(request.getAssigneeId()).orElse(null);
//            task.setAssignee(assignee);
//        }

        Task savedTask = taskRepository.save(task);
        return mapToTaskDto(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse moveTask(UUID taskId, MoveTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // 1. Cập nhật cột mới (nếu có thay đổi)
        if (!task.getColumn().getId().equals(request.getTargetColumnId())) {
            TaskColumn targetColumn = taskColumnRepository.findById(request.getTargetColumnId())
                    .orElseThrow(() -> new RuntimeException("Target column not found"));
            task.setColumn(targetColumn);
        }

        // 2. Tính toán Rank mới dựa trên vị trí thả
        String newRank = lexorankService.getRankBetween(request.getPrevTaskRank(), request.getNextTaskRank());
        task.setPosition(newRank);

        Task savedTask = taskRepository.save(task);
        return mapToTaskDto(savedTask);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(UUID taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // 1. Cập nhật thông tin cơ bản (chỉ update nếu field có giá trị)
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        // Cho phép set null nếu muốn xóa deadline
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        // Cập nhật danh sách người làm (Ghi đè)
        if (request.getAssigneeIds() != null) {
            Set<User> assignees = validateAndGetAssignees(task.getProject().getWorkspace().getId(), request.getAssigneeIds());
            task.setAssignees(assignees);
        }

        Task savedTask = taskRepository.save(task);
        return mapToTaskDto(savedTask);
    }

    @Override
    @Transactional
    public void deleteTask(UUID taskId) {
        // Kiểm tra tồn tại
        if (!taskRepository.existsById(taskId)) {
            throw new RuntimeException("Task not found");
        }
        // Xóa task (Database sẽ tự động xóa các liên kết con nếu cấu hình Cascade,
        // nhưng với Task thì thường ít liên kết phức tạp ngoài subtask)
        taskRepository.deleteById(taskId);
    }

    @Override
    @Transactional
    public SubTaskResponse createSubTask(UUID parentTaskId, String title) {
        Task parentTask = taskRepository.findById(parentTaskId)
                .orElseThrow(() -> new RuntimeException("Parent task not found"));

        // --- FIX LỖI DUPLICATE KEY: Tăng task_number cho Subtask ---
        Project project = parentTask.getProject();
        project.setCurrentTaskNumber(project.getCurrentTaskNumber() + 1);
        projectRepository.save(project);

        Task subTask = new Task();
        subTask.setTitle(title);
        subTask.setParentTask(parentTask);

        // Vẫn gán project/column để quản lý data, nhưng sẽ bị ẩn khỏi board nhờ query trên
        subTask.setProject(project);
        subTask.setColumn(parentTask.getColumn());
        subTask.setPriority(parentTask.getPriority());
        subTask.setPosition(lexorankService.getRankBetween(null, null));
        subTask.setTaskNumber(project.getCurrentTaskNumber()); // Dùng số mới sinh thay vì 0
        subTask.setIsDone(false);

        Task saved = taskRepository.save(subTask);
        return new SubTaskResponse(saved.getId(), saved.getTitle(), saved.getIsDone());
    }

    @Override
    @Transactional
    public SubTaskResponse toggleSubTask(UUID subTaskId) {
        Task subTask = taskRepository.findById(subTaskId)
                .orElseThrow(() -> new RuntimeException("Subtask not found"));

        subTask.setIsDone(!Boolean.TRUE.equals(subTask.getIsDone()));
        Task saved = taskRepository.save(subTask);
        return new SubTaskResponse(saved.getId(), saved.getTitle(), saved.getIsDone());
    }

    @Override
    @Transactional
    public void deleteSubTask(UUID subTaskId) {
        taskRepository.deleteById(subTaskId);
    }

    @Override
    public TaskResponse getTask(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToTaskDto(task);
    }

    // Mapper
    private TaskResponse mapToTaskDto(Task t) {
        // Map assignees...
        List<UserDto> assigneeDtos = t.getAssignees().stream()
                .map(u -> new UserDto(u.getId(), u.getName(), u.getEmail(), u.getPhoneNumber(), u.getAvatarUrl()))
                .collect(Collectors.toList());

        // Map subtasks...
        List<SubTaskResponse> subTaskDtos = t.getSubTasks() != null ?
                t.getSubTasks().stream()
                        .map(st -> new SubTaskResponse(st.getId(), st.getTitle(), st.getIsDone()))
                        .collect(Collectors.toList())
                : Collections.emptyList();

        String displayId = t.getProject().getKey() + "-" + t.getTaskNumber();

        return new TaskResponse(
                t.getId(), t.getTitle(), t.getDescription(), displayId,
                t.getPriority(), t.getPosition(), t.getColumn().getId(),
                assigneeDtos, t.getCreatedAt(),
                t.getDueDate(),
                subTaskDtos, t.getIsDone() // <--- Thêm subtasks và isDone
        );
    }

    // Helper: Validate list users
    private Set<User> validateAndGetAssignees(UUID workspaceId, Set<UUID> userIds) {
        Set<User> users = new HashSet<>();
        for (UUID userId : userIds) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            boolean isMember = workspaceMemberRepository.findById(
                    new WorkspaceMember.WorkspaceMemberId(workspaceId, userId)
            ).isPresent();

            if (!isMember) {
                throw new RuntimeException("User " + user.getEmail() + " is not in this workspace");
            }
            users.add(user);
        }
        return users;
    }
}
