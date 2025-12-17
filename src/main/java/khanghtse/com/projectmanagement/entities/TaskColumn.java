package khanghtse.com.projectmanagement.entities;

import jakarta.persistence.*;
import lombok.Data;


import java.util.UUID;

@Entity
@Table(name = "task_columns")
@Data
public class TaskColumn {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String position; // Lexorank string

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
