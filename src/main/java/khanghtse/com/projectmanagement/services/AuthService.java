package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.AuthDto;
import khanghtse.com.projectmanagement.entities.PasswordResetToken;
import khanghtse.com.projectmanagement.entities.User;
import khanghtse.com.projectmanagement.repositories.PasswordResetTokenRepository;
import khanghtse.com.projectmanagement.repositories.UserRepository;
import khanghtse.com.projectmanagement.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final IEmailService emailService;

    @Override
    public String login(AuthDto.LoginRequest loginRequest) {
        System.out.println("1. Bắt đầu login với email: " + loginRequest.getEmail());

        // Nếu sai pass, dòng này sẽ tự ném BadCredentialsException ra ngoài
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        System.out.println("2. Xác thực thành công!");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(loginRequest.getEmail());
    }

    @Override
    public String register(AuthDto.RegisterRequest registerRequest) {
        // 1. Check tồn tại
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // 2. Tạo User mới
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        userRepository.save(user);

        return "User registered successfully!.";
    }

    // --- Method mới thêm vào cho Refresh Token Flow ---
    @Override
    public String generateTokenFromEmail(String email) {
        return jwtTokenProvider.generateToken(email);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email này chưa được đăng ký trong hệ thống."));

        // Xóa token cũ nếu có
        passwordResetTokenRepository.deleteByUser(user);
        // Lưu ý: Cần thêm method deleteByUser trong Repository hoặc xử lý thủ công
        // Ở đây ta đơn giản là tạo mới, cái cũ kệ nó (job dọn dẹp sau) hoặc tìm và xóa

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(resetToken);

        // Gửi email (Link Frontend)
        String resetLink = "http://localhost:5173/reset-password?token=" + token;

        // Ta tái sử dụng EmailService (cần update EmailService chút để support subject/body tùy chỉnh tốt hơn
        // hoặc dùng hàm sendInvitationEmail tạm cũng được nhưng nên viết hàm mới)
        emailService.sendSimpleEmail(email, "Yêu cầu đặt lại mật khẩu PMS",
                "Chào " + user.getName() + ",\n\n" +
                        "Bạn vừa yêu cầu đặt lại mật khẩu. Vui lòng click vào link bên dưới:\n" +
                        resetLink + "\n\n" +
                        "Link này hết hạn sau 1 giờ.\nNếu bạn không yêu cầu, vui lòng bỏ qua email này.");
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ hoặc đường dẫn sai."));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Link đặt lại mật khẩu đã hết hạn.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xóa token sau khi dùng
        passwordResetTokenRepository.delete(resetToken);
    }
}
