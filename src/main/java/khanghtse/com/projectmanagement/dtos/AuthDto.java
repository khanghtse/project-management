package khanghtse.com.projectmanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDto {
    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthResponse {
        private String accessToken;
        private String tokenType = "Bearer";

        public AuthResponse(String accessToken) {
            this.accessToken = accessToken;
        }
    }

    // --- MỚI: FORGOT PASSWORD DTOs ---
    @Data
    public static class ForgotPasswordRequest {
        private String email;
    }

    @Data
    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;
    }
}
