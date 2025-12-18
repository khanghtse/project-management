package khanghtse.com.projectmanagement.repositories;

import khanghtse.com.projectmanagement.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByWorkspaceId(UUID workspaceId);

    // Kiểm tra Key đã tồn tại trong workspace chưa (tránh trùng lặp key dự án)
    boolean existsByWorkspaceIdAndKey(UUID workspaceId, String key);
}
