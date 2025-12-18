package khanghtse.com.projectmanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int status;       // Mã lỗi (ví dụ: 401, 400)
    private String message;   // Nội dung lỗi
    private long timestamp;   // Thời gian xảy ra lỗi
}
