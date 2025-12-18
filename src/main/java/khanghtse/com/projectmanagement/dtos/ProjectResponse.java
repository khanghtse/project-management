package khanghtse.com.projectmanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
    private UUID id;
    private String name;
    private String key;
    private String description;
    private Integer taskCount; // Số lượng task (Optional)

    // Bổ sung audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
