package khanghtse.com.projectmanagement.entities;

import jakarta.persistence.*;
import khanghtse.com.projectmanagement.enums.WorkspaceRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

// Bảng trung gian (Many-to-Many) giữa User và Workspace
@Entity
@Table(name = "workspace_members")
@Data
@NoArgsConstructor
public class WorkspaceMember {
    @EmbeddedId
    private WorkspaceMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("workspaceId") // Map với field trong EmbeddedId
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkspaceRole role; // ADMIN, MEMBER

    // Class định nghĩa Composite Key
    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkspaceMemberId implements Serializable {
        private UUID workspaceId;
        private UUID userId;
    }
}
