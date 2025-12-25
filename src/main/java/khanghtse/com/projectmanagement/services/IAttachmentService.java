package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.AttachmentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IAttachmentService {

    List<AttachmentResponse> getAttachments(UUID taskId);

    AttachmentResponse uploadAttachment(UUID taskId, MultipartFile file, String userEmail);

    void deleteAttachment(UUID attachmentId);
}
