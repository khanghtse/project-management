package khanghtse.com.projectmanagement.services;

import jakarta.transaction.Transactional;
import khanghtse.com.projectmanagement.entities.RefreshToken;
import khanghtse.com.projectmanagement.repositories.RefreshTokenRepository;
import khanghtse.com.projectmanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements IRefreshTokenService{

    @Value("${app.jwt-refresh-expiration-milliseconds}")
    private Long refreshTokenDurationMs; // Ví dụ: 604800000 (7 ngày)

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(UUID userId) {
        // Xóa token cũ của user này (nếu muốn mỗi user chỉ có 1 session tại 1 thời điểm)
        // Hoặc giữ lại nếu muốn cho phép đăng nhập nhiều nơi
        // refreshTokenRepository.deleteByUserId(userId);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString()); // Token là chuỗi ngẫu nhiên

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}
