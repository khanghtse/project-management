package khanghtse.com.projectmanagement.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AttachmentResponse {

    private UUID id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private LocalDateTime uploadedAt;
}
