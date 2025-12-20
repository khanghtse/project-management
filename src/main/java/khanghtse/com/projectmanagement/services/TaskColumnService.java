package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.TaskColumnDto;
import khanghtse.com.projectmanagement.entities.Project;
import khanghtse.com.projectmanagement.entities.TaskColumn;
import khanghtse.com.projectmanagement.repositories.ProjectRepository;
import khanghtse.com.projectmanagement.repositories.TaskColumnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskColumnService implements ITaskColumnService{

    private final TaskColumnRepository taskColumnRepository;
    private final ProjectRepository projectRepository;
    private final LexorankService lexorankService;

    // 1. Lấy danh sách cột của dự án
    @Override
    public List<TaskColumnDto.Response> getColumns(UUID projectId) {
        return taskColumnRepository.findByProjectIdOrderByPositionAsc(projectId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    // 2. Tạo cột mới
    public TaskColumnDto.Response createColumn(UUID projectId, TaskColumnDto.CreateColumnRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Tính rank để đưa xuống cuối cùng
        List<TaskColumn> existingCols = taskColumnRepository.findByProjectIdOrderByPositionAsc(projectId);
        String newRank = lexorankService.getRankBetween(null, null); // Default mid

        if (!existingCols.isEmpty()) {
            String lastRank = existingCols.get(existingCols.size() - 1).getPosition();
            newRank = lexorankService.getRankBetween(lastRank, null);
        }

        TaskColumn column = new TaskColumn();
        column.setName(request.getName());
        column.setProject(project);
        column.setPosition(newRank);

        return mapToDto(taskColumnRepository.save(column));
    }

    @Override
    @Transactional
    // 3. Đổi tên cột
    public TaskColumnDto.Response updateColumn(UUID columnId, TaskColumnDto.UpdateColumnRequest request) {
        TaskColumn column = taskColumnRepository.findById(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));

        column.setName(request.getName());
        return mapToDto(taskColumnRepository.save(column));
    }

    @Override
    @Transactional
    // 4. Di chuyển cột (Thay đổi thứ tự trái/phải)
    public TaskColumnDto.Response moveColumn(UUID columnId, TaskColumnDto.MoveColumnRequest request) {
        TaskColumn column = taskColumnRepository.findById(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));

        String newRank = lexorankService.getRankBetween(request.getPrevRank(), request.getNextRank());
        column.setPosition(newRank);

        return mapToDto(taskColumnRepository.save(column));
    }

    @Override
    @Transactional
    public void deleteColumn(UUID columnId) {
        // Lưu ý: Cần xử lý các Task trong cột này trước khi xóa (Move sang cột khác hoặc xóa luôn)
        // Ở đây demo xóa luôn (Cascade sẽ xóa tasks nếu config DB, hoặc phải xóa tay)
        taskColumnRepository.deleteById(columnId);
    }

    private TaskColumnDto.Response mapToDto(TaskColumn c) {
        return new TaskColumnDto.Response(c.getId(), c.getName(), c.getPosition(), c.getProject().getId());
    }
}
