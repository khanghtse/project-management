package khanghtse.com.projectmanagement.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
@Data
@NoArgsConstructor
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL)
    private List<WorkspaceMember> members;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL)
    private List<Project> projects;
}
