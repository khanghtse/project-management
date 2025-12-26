package khanghtse.com.projectmanagement.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class CreateTaskRequest {
    private String title;
    private String description;
    private String priority; // LOW, MEDIUM, HIGH
    private UUID columnId;   // Task thuộc cột nào
    private Set<UUID> assigneeIds; // Đổi thành danh sách ID
    private LocalDateTime dueDate;
}
