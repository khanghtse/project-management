package khanghtse.com.projectmanagement.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tasks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "task_number"})
})
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String description;

    // Friendly ID part. Ví dụ: taskNumber=101 + projectKey="WEB" -> "WEB-101"
    @Column(name = "task_number", nullable = false)
    private Integer taskNumber;

    // Quan trọng cho Kanban: Lexorank Position
    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String priority; // LOW, MEDIUM, HIGH

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_id", nullable = false)
    private TaskColumn column;

    // --- THAY ĐỔI: TỪ @ManyToOne SANG @ManyToMany ---
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "task_assignees",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignees = new HashSet<>();

    // Sub-tasks (Recursive)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Task parentTask;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
