package khanghtse.com.projectmanagement.repositories;

import khanghtse.com.projectmanagement.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    // Lấy comment theo task, sắp xếp cũ trước mới sau (giống chat)
    List<Comment> findByTaskIdOrderByCreatedAtAsc(UUID taskId);
}
