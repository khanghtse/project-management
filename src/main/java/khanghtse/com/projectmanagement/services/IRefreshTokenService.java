package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.entities.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface IRefreshTokenService {
    RefreshToken createRefreshToken(UUID userId);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByUserId(UUID userId);

    Optional<RefreshToken> findByToken(String token);
}
