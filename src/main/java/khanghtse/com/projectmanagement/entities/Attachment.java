package khanghtse.com.projectmanagement.entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attachments")
@Data
@NoArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fileName; // Tên file gốc (vd: design.png)

    @Column(nullable = false)
    private String fileUrl;  // Link ảnh trên Cloudinary

    @Column(nullable = false)
    private String fileType; // image/png, application/pdf...

    private String publicId; // ID trên Cloudinary để xóa sau này

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }
}
