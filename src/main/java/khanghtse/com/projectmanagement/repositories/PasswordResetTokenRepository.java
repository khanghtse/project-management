package khanghtse.com.projectmanagement.repositories;

import khanghtse.com.projectmanagement.entities.PasswordResetToken;
import khanghtse.com.projectmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user); // Để xóa token cũ nếu user yêu cầu lại
}
