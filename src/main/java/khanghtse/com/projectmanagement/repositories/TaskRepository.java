package khanghtse.com.projectmanagement.repositories;

import khanghtse.com.projectmanagement.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    // Lấy task theo cột, sắp xếp theo Lexorank (quan trọng để hiển thị đúng thứ tự)
    // Chỉ lấy các Task không có cha (Task cấp 1) để hiển thị lên Board
    List<Task> findByColumnIdAndParentTaskIsNullOrderByPositionAsc(UUID columnId);
}
