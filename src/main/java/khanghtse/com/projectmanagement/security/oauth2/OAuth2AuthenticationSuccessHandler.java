package khanghtse.com.projectmanagement.security.oauth2;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import khanghtse.com.projectmanagement.entities.RefreshToken;
import khanghtse.com.projectmanagement.entities.User;
import khanghtse.com.projectmanagement.repositories.UserRepository;
import khanghtse.com.projectmanagement.security.JwtTokenProvider;
import khanghtse.com.projectmanagement.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Value("${app.oauth2.authorized-redirect-uris}")
    private String redirectUri;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // 1. Lấy User từ DB để có UUID (Quan trọng vì bảng RefreshToken cần UUID)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found logic error"));

        // 2. Tạo Access Token (JWT)
        String accessToken = jwtTokenProvider.generateToken(email);

        // 3. Tạo Refresh Token và Lưu vào DB
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        // 4. Tạo Cookie chứa Refresh Token
        Cookie refreshCookie = new Cookie("refresh_token", refreshToken.getToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // Đặt true nếu chạy HTTPS
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
        response.addCookie(refreshCookie);

        // 5. Redirect kèm Access Token
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", accessToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
