package khanghtse.com.projectmanagement.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class UpdateTaskRequest {
    private String title;       // Có thể null nếu không sửa
    private String description; // Có thể null
    private String priority;    // Có thể null
    private Set<UUID> assigneeIds; // Danh sách ID mới (ghi đè danh sách cũ)

    // Cờ đặc biệt để xóa người được giao (vì nếu assigneeId = null thì ta hiểu là không đổi)
    private Boolean unassign;   // True -> Set assignee = null
    private LocalDateTime dueDate;
}
