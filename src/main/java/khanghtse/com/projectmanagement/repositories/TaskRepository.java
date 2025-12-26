package khanghtse.com.projectmanagement.repositories;

import khanghtse.com.projectmanagement.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    // Lấy task theo cột, sắp xếp theo Lexorank (quan trọng để hiển thị đúng thứ tự)
    // Chỉ lấy các Task không có cha (Task cấp 1) để hiển thị lên Board
    List<Task> findByColumnIdAndParentTaskIsNullOrderByPositionAsc(UUID columnId);

    // --- QUERY ĐÃ ĐƯỢC TỐI ƯU ---
    // Loại bỏ CONCAT trong SQL để tránh lỗi type inference
    // Sử dụng logic: Nếu :keyword là NULL thì bỏ qua điều kiện tìm kiếm
    @Query("SELECT t FROM Task t " +
            "LEFT JOIN t.assignees a " +
            "WHERE t.project.id = :projectId " +
            "AND t.parentTask IS NULL " +
            "AND (:keyword IS NULL OR LOWER(t.title) LIKE :keyword OR LOWER(t.description) LIKE :keyword) " +
            "AND (:priority IS NULL OR t.priority = :priority) " +
            "AND (:assigneeId IS NULL OR a.id = :assigneeId) " +
            "ORDER BY t.position ASC")
    List<Task> findTasksByProjectAndFilters(
            @Param("projectId") UUID projectId,
            @Param("keyword") String keyword,   // Tham số này sẽ chứa chuỗi "%abc%"
            @Param("priority") String priority,
            @Param("assigneeId") UUID assigneeId
    );
}
