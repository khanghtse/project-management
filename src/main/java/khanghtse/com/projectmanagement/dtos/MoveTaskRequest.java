package khanghtse.com.projectmanagement.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class MoveTaskRequest {
    private UUID targetColumnId; // Cột đích đến
    private String prevTaskRank; // Rank của task đứng NGAY TRƯỚC vị trí mới (null nếu thả lên đầu)
    private String nextTaskRank; // Rank của task đứng NGAY SAU vị trí mới (null nếu thả xuống cuối)
}
