package khanghtse.com.projectmanagement.repositories;

import khanghtse.com.projectmanagement.entities.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
    // Lấy log mới nhất lên đầu
    List<ActivityLog> findByTaskIdOrderByCreatedAtDesc(UUID taskId);
}
