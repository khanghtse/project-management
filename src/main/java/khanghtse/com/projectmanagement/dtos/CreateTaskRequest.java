package khanghtse.com.projectmanagement.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateTaskRequest {
    private String title;
    private String description;
    private String priority; // LOW, MEDIUM, HIGH
    private UUID columnId;   // Task thuộc cột nào
    private UUID assigneeId; // Người được giao (Optional)
}
