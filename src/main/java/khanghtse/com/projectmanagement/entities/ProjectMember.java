package khanghtse.com.projectmanagement.entities;

import jakarta.persistence.*;
import khanghtse.com.projectmanagement.enums.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "project_members")
@Data
@NoArgsConstructor
public class ProjectMember {
    @EmbeddedId
    private ProjectMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ProjectRole role;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProjectMemberId implements Serializable {
        private UUID projectId;
        private UUID userId;
    }
}
