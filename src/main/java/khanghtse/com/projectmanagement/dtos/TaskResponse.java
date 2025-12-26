package khanghtse.com.projectmanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
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
    private List<UserDto> assignees; // Trả về danh sách user
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;

    // Bổ sung danh sách Subtasks
    private List<SubTaskResponse> subTasks;

    // Field này dùng cho frontend biết task này đã xong chưa (nếu muốn)
    private Boolean isDone;
}
