package khanghtse.com.projectmanagement.repositories;

import khanghtse.com.projectmanagement.entities.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {
    Optional<AuthProvider> findByProviderAndProviderId(String provider, String providerId);
}
