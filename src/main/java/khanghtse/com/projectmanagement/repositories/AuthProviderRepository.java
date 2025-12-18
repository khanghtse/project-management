package khanghtse.com.projectmanagement.repositories;

import khanghtse.com.projectmanagement.entities.AuthProvider;
import khanghtse.com.projectmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {
    Optional<AuthProvider> findByProviderAndProviderId(String provider, String providerId);
    // Tìm xem user này đã từng đăng nhập bằng provider này chưa (ví dụ: User A đã link với Google chưa)
    Optional<AuthProvider> findByUserAndProvider(User user, String provider);
}
