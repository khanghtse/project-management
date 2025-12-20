package khanghtse.com.projectmanagement.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "invitations")
@Data
@NoArgsConstructor
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String email; // Email người được mời

    @Column(nullable = false, unique = true)
    private String token; // Token để verify

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    private User inviter; // Người gửi lời mời

    private LocalDateTime expiryDate;

    public Invitation(String email, String token, Workspace workspace, User inviter) {
        this.email = email;
        this.token = token;
        this.workspace = workspace;
        this.inviter = inviter;
        this.expiryDate = LocalDateTime.now().plusDays(3); // Link hết hạn sau 3 ngày
    }
}
