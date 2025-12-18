package khanghtse.com.projectmanagement.repositories;

import khanghtse.com.projectmanagement.entities.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
    // Tìm tất cả workspace mà userId là chủ sở hữu HOẶC là thành viên
    // (Giả sử bạn đã có quan hệ members trong Entity Workspace như thiết kế trước)
    @Query("SELECT w FROM Workspace w LEFT JOIN w.members m WHERE w.owner.id = :userId OR m.user.id = :userId")
    List<Workspace> findAllByUserId(UUID userId);
}
