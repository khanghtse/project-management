package khanghtse.com.projectmanagement.controllers;


import khanghtse.com.projectmanagement.services.IAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AttachmentController {

    private final IAttachmentService attachmentService;

    // GET Attachments
    @GetMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<?> getAttachments(@PathVariable UUID taskId) {
        return ResponseEntity.ok(attachmentService.getAttachments(taskId));
    }

    // UPLOAD
    @PostMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<?> uploadAttachment(
            @PathVariable UUID taskId,
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        return ResponseEntity.ok(attachmentService.uploadAttachment(taskId, file, principal.getName()));
    }

    // DELETE
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable UUID attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}
