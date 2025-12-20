package khanghtse.com.projectmanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
    private UUID id;
    private String title;
    private String description;
    private String displayId; // VD: WEB-101
    private String priority;
    private String position;  // Lexorank
    private UUID columnId;
    private UserDto assignee;
    private LocalDateTime createdAt;
}
