package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.CreateProjectRequest;
import khanghtse.com.projectmanagement.dtos.ProjectResponse;
import khanghtse.com.projectmanagement.dtos.ProjectStatsDto;
import khanghtse.com.projectmanagement.dtos.TaskResponse;
import khanghtse.com.projectmanagement.entities.*;
import khanghtse.com.projectmanagement.enums.ProjectRole;
import khanghtse.com.projectmanagement.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService implements IProjectService {

    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;
    private final TaskColumnRepository taskColumnRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public ProjectResponse createProject(UUID workspaceId, CreateProjectRequest request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        // 1. Validate Key trùng lặp trong workspace
        if (projectRepository.existsByWorkspaceIdAndKey(workspaceId, request.getKey())) {
            throw new RuntimeException("Project key " + request.getKey() + " already exists");
        }

        // 2. Tạo Project
        Project project = new Project();
        project.setName(request.getName());
        project.setKey(request.getKey().toUpperCase());
        project.setDescription(request.getDescription());
        project.setWorkspace(workspace);
        project.setOwner(user);
        project.setCurrentTaskNumber(0); // Khởi đầu chưa có task nào

        Project savedProject = projectRepository.save(project);

        // 3. Tạo các cột Kanban mặc định (Default Workflow)
        createDefaultColumns(savedProject);

        // 4. Thêm người tạo làm MANAGER của dự án
        ProjectMember member = new ProjectMember();
        member.setId(new ProjectMember.ProjectMemberId(savedProject.getId(), user.getId()));
        member.setProject(savedProject);
        member.setUser(user);
        member.setRole(ProjectRole.MANAGER);

        projectMemberRepository.save(member);

        // --- Cập nhật constructor với createdAt, updatedAt ---
        return new ProjectResponse(
                savedProject.getId(),
                savedProject.getName(),
                savedProject.getKey(),
                savedProject.getDescription(),
                0,
                savedProject.getCreatedAt(),
                savedProject.getUpdatedAt()
        );
    }

    @Override
    public List<ProjectResponse> getProjectsInWorkspace(UUID workspaceId) {
        return projectRepository.findByWorkspaceId(workspaceId).stream()
                .map(p -> new ProjectResponse(
                        p.getId(),
                        p.getName(),
                        p.getKey(),
                        p.getDescription(),
                        0,
                        p.getCreatedAt(),
                        p.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ProjectStatsDto getProjectStats(UUID projectId) {
        // 1. Lấy tất cả task
        List<Task> tasks = taskRepository.findByProjectIdAndParentTaskIsNull(projectId);

        // 2. Tính tổng
        long totalTasks = tasks.size();

        // 3. Tính hoàn thành (Dựa trên isDone hoặc cột cuối cùng - ở đây dùng isDone cho chuẩn)
        long completedTasks = tasks.stream().filter(t -> Boolean.TRUE.equals(t.getIsDone())).count();

        // 4. Tính quá hạn (Chưa xong AND Có deadline AND Deadline < Hiện tại)
        LocalDateTime now = LocalDateTime.now();
        List<Task> overdueList = tasks.stream()
                .filter(t -> !Boolean.TRUE.equals(t.getIsDone())
                        && t.getDueDate() != null
                        && t.getDueDate().isBefore(now))
                .collect(Collectors.toList());
        long overdueTasks = overdueList.size();

        // 5. Group by Status (Column Name)
        Map<String, Long> tasksByStatus = tasks.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getColumn().getName(),
                        Collectors.counting()
                ));

        // 6. Group by Assignee
        // Vì Task có nhiều assignee, ta cần flatMap
        // Logic: Task A có User 1, User 2 -> User 1 +1 task, User 2 +1 task
        Map<String, Long> tasksByAssignee = new HashMap<>();
        for (Task task : tasks) {
            if (task.getAssignees().isEmpty()) {
                tasksByAssignee.merge("Chưa gán", 1L, Long::sum);
            } else {
                for (var user : task.getAssignees()) {
                    tasksByAssignee.merge(user.getName(), 1L, Long::sum);
                }
            }
        }

        // 7. Map overdue list sang DTO (để hiển thị chi tiết)
        // Bạn cần inject TaskService để dùng lại hàm mapToTaskDto hoặc copy lại logic map đơn giản ở đây
        // Để đơn giản tôi map thủ công các field cần thiết
        List<TaskResponse> overdueDtos = overdueList.stream().map(t -> {
            String displayId = t.getProject().getKey() + "-" + t.getTaskNumber();
            return new TaskResponse(
                    t.getId(), t.getTitle(), null, displayId, t.getPriority(),
                    null, null, null, null, t.getDueDate(), null, false
            );
        }).collect(Collectors.toList());

        return new ProjectStatsDto(
                totalTasks,
                completedTasks,
                overdueTasks,
                tasksByStatus,
                tasksByAssignee,
                overdueDtos
        );
    }

    // Tạo cột mặc định: To Do -> In Progress -> Done
    private void createDefaultColumns(Project project) {
        // Sử dụng Lexorank đơn giản: "0|aaaaa", "0|aaaab", "0|aaaac"
        // Trong thực tế nên dùng library Lexorank để sinh string
        createColumn(project, "To Do", "0|i0000");       // Vị trí 1
        createColumn(project, "In Progress", "0|i0001"); // Vị trí 2
        createColumn(project, "Done", "0|i0002");        // Vị trí 3
    }

    private void createColumn(Project project, String name, String position) {
        TaskColumn col = new TaskColumn();
        col.setProject(project);
        col.setName(name);
        col.setPosition(position);
        taskColumnRepository.save(col);
    }
}
