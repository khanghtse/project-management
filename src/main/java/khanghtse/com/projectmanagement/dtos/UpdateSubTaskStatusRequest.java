package khanghtse.com.projectmanagement.dtos;

import lombok.Data;

@Data
public class UpdateSubTaskStatusRequest {
    private Boolean isDone; // Dùng để check/uncheck
}
