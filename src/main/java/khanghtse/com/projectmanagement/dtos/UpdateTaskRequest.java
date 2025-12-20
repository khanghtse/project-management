package khanghtse.com.projectmanagement.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateTaskRequest {
    private String title;       // Có thể null nếu không sửa
    private String description; // Có thể null
    private String priority;    // Có thể null
    private UUID assigneeId;    // UUID của user mới (Gửi null nếu không đổi)

    // Cờ đặc biệt để xóa người được giao (vì nếu assigneeId = null thì ta hiểu là không đổi)
    private Boolean unassign;   // True -> Set assignee = null
}
