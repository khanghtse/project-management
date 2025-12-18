package khanghtse.com.projectmanagement.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import khanghtse.com.projectmanagement.entities.AuthProvider;
import khanghtse.com.projectmanagement.entities.RefreshToken;
import khanghtse.com.projectmanagement.entities.User;
import khanghtse.com.projectmanagement.repositories.AuthProviderRepository; // Mới thêm
import khanghtse.com.projectmanagement.repositories.UserRepository;
import khanghtse.com.projectmanagement.security.JwtTokenProvider;
import khanghtse.com.projectmanagement.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
    private final AuthProviderRepository authProviderRepository; // Inject thêm Repository này

    @Value("${app.oauth2.authorized-redirect-uris}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Lấy ID của Provider (ví dụ: "google", "github") từ Token
        // Chú ý: Ép kiểu sang OAuth2AuthenticationToken để lấy registrationId
        String provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String avatarUrl = oAuth2User.getAttribute("picture");
        String providerId = oAuth2User.getName(); // Đây là ID duy nhất của User bên Google (sub)

        // --- 1. XỬ LÝ USER (Tạo mới hoặc Lấy cũ) ---
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setAvatarUrl(avatarUrl);
            newUser.setPassword(null); // OAuth2 không cần pass
            return userRepository.save(newUser);
        });

        // --- 2. XỬ LÝ AUTH PROVIDER (Logic mới bổ sung) ---
        // Kiểm tra xem User này đã liên kết với Provider này chưa
        authProviderRepository.findByUserAndProvider(user, provider)
                .orElseGet(() -> {
                    // Nếu chưa có thì tạo mới liên kết
                    AuthProvider newProvider = new AuthProvider();
                    newProvider.setUser(user);
                    newProvider.setProvider(provider);     // google
                    newProvider.setProviderId(providerId); // ID số dài ngoằng từ google
                    return authProviderRepository.save(newProvider);
                });

        // --- 3. Tạo JWT & Refresh Token (Logic cũ giữ nguyên) ---
        String accessToken = jwtTokenProvider.generateToken(email);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        Cookie refreshCookie = new Cookie("refresh_token", refreshToken.getToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // Set true nếu chạy HTTPS
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshCookie);

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", accessToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
