package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.AttachmentResponse;
import khanghtse.com.projectmanagement.entities.Attachment;
import khanghtse.com.projectmanagement.entities.Task;
import khanghtse.com.projectmanagement.entities.User;
import khanghtse.com.projectmanagement.repositories.AttachmentRepository;
import khanghtse.com.projectmanagement.repositories.TaskRepository;
import khanghtse.com.projectmanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService implements IAttachmentService {

    private final AttachmentRepository attachmentRepository; // Tạo interface này nhé (kế thừa JpaRepository)
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ICloudinaryService cloudinaryService;

    @Override
    public List<AttachmentResponse> getAttachments(UUID taskId) {
        return attachmentRepository.findByTaskIdOrderByUploadedAtDesc(taskId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AttachmentResponse uploadAttachment(UUID taskId, MultipartFile file, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Upload lên Cloudinary
        Map uploadResult = cloudinaryService.uploadFile(file);

        // 2. Lưu vào DB
        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileUrl(uploadResult.get("url").toString());
        attachment.setPublicId(uploadResult.get("public_id").toString());
        attachment.setFileType(file.getContentType());
        attachment.setTask(task);
        attachment.setUploader(user);

        Attachment saved = attachmentRepository.save(attachment);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteAttachment(UUID attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        // 1. Xóa trên Cloudinary
        if (attachment.getPublicId() != null) {
            cloudinaryService.deleteFile(attachment.getPublicId());
        }

        // 2. Xóa trong DB
        attachmentRepository.delete(attachment);
    }

    private AttachmentResponse mapToDto(Attachment a) {
        AttachmentResponse res = new AttachmentResponse();
        res.setId(a.getId());
        res.setFileName(a.getFileName());
        res.setFileUrl(a.getFileUrl());
        res.setFileType(a.getFileType());
        res.setUploadedAt(a.getUploadedAt());
        return res;
    }
}
