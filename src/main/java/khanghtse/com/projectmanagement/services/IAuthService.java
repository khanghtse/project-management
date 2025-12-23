package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.AuthDto;

public interface IAuthService {
    String login(AuthDto.LoginRequest loginRequest);
    String register(AuthDto.RegisterRequest registerRequest);
    String generateTokenFromEmail(String email);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
}
