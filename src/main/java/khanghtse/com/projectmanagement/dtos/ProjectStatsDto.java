package khanghtse.com.projectmanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectStatsDto {

    private long totalTasks;
    private long completedTasks;
    private long overdueTasks;

    // Dữ liệu cho biểu đồ tròn: "Tên cột" -> Số lượng
    private Map<String, Long> tasksByStatus;

    // Dữ liệu cho biểu đồ cột: "Tên thành viên" -> Số lượng task
    private Map<String, Long> tasksByAssignee;

    // Danh sách các task đang bị trễ hạn (để hiện cảnh báo)
    private List<TaskResponse> overdueTaskList;
}
