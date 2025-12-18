package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.AuthDto;
import khanghtse.com.projectmanagement.entities.User;
import khanghtse.com.projectmanagement.repositories.UserRepository;
import khanghtse.com.projectmanagement.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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
}
