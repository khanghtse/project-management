package khanghtse.com.projectmanagement.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import khanghtse.com.projectmanagement.dtos.AuthDto;
import khanghtse.com.projectmanagement.entities.RefreshToken;
import khanghtse.com.projectmanagement.entities.User;
import khanghtse.com.projectmanagement.repositories.UserRepository;
import khanghtse.com.projectmanagement.services.IAuthService;
import khanghtse.com.projectmanagement.services.IRefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService   authService;
    private final IRefreshTokenService refreshTokenService;
    private final UserRepository userRepository; // Dùng tạm để lấy ID nhanh

    // --- 1. REGISTER API (Đã bổ sung) ---
    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<String> register(@RequestBody AuthDto.RegisterRequest registerRequest) {
        String response = authService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<AuthDto.AuthResponse> login(@RequestBody AuthDto.LoginRequest loginRequest, HttpServletResponse response) {
        // 1. Lấy Access Token từ Service cũ
        String accessToken = authService.login(loginRequest);

        // 2. Lấy User ID
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();

        // 3. Tạo Refresh Token & Lưu DB
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        // 4. Set Cookie
        Cookie refreshCookie = new Cookie("refresh_token", refreshToken.getToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshCookie);

        // 5. Trả về Access Token
        AuthDto.AuthResponse jwtAuthResponse = new AuthDto.AuthResponse();
        jwtAuthResponse.setAccessToken(accessToken);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    // API quan trọng: Lấy Access Token mới từ Refresh Token
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token", required = false) String requestRefreshToken) {
        if (requestRefreshToken == null) {
            return ResponseEntity.status(401).body("Refresh Token is empty!");
        }

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    // Tạo Access Token mới
                    String newAccessToken = authService.generateTokenFromEmail(user.getEmail());
                    return ResponseEntity.ok(new AuthDto.AuthResponse(newAccessToken));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    // --- 4. LOGOUT API (Bổ sung thêm cho đầy đủ) ---
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, @CookieValue(name = "refresh_token", required = false) String requestRefreshToken) {
        // 1. Xóa Token trong DB (Optional - tùy nghiệp vụ)
        if (requestRefreshToken != null) {
             refreshTokenService.findByToken(requestRefreshToken).ifPresent(token -> {
                 refreshTokenService.deleteByUserId(token.getUser().getId());
             });
         }


        // 2. Xóa Cookie ở phía Client
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Hết hạn ngay lập tức
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    // --- MỚI: API QUÊN MẬT KHẨU ---
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody AuthDto.ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("Link đặt lại mật khẩu đã được gửi vào email của bạn.");
    }

    // --- MỚI: API ĐẶT LẠI MẬT KHẨU ---
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody AuthDto.ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Mật khẩu đã được thay đổi thành công. Vui lòng đăng nhập lại.");
    }
}
