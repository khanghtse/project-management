package khanghtse.com.projectmanagement.repositories;

import khanghtse.com.projectmanagement.entities.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    Optional<Invitation> findByToken(String token);
    boolean existsByWorkspaceIdAndEmail(UUID workspaceId, String email);
}
