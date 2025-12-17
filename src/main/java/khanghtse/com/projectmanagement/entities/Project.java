package khanghtse.com.projectmanagement.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Data
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String key; // Ví dụ: "WEB", "APP"

    @Column(name = "current_task_number")
    private Integer currentTaskNumber = 0; // Để sinh ID: WEB-1, WEB-2

    // --- BỔ SUNG QUAN HỆ WORKSPACE ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<TaskColumn> columns;

    // --- BỔ SUNG PROJECT MEMBERS ---
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> members;
}
