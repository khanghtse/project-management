package khanghtse.com.projectmanagement.repositories;

import khanghtse.com.projectmanagement.entities.TaskColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskColumnRepository extends JpaRepository<TaskColumn, UUID> {
    List<TaskColumn> findByProjectIdOrderByPositionAsc(UUID projectId);
}
