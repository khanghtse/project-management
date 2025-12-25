package khanghtse.com.projectmanagement.services;


import khanghtse.com.projectmanagement.dtos.ChangePasswordRequest;
import khanghtse.com.projectmanagement.dtos.UpdateProfileRequest;
import khanghtse.com.projectmanagement.dtos.UserDto;
import khanghtse.com.projectmanagement.entities.User;
import khanghtse.com.projectmanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    private UserDto mapToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber(), user.getAvatarUrl());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserDto getCurrentUser(String email) {
        User user = getUser(email);
        return mapToDto(user);
    }

    @Override
    @Transactional
    public UserDto updateProfile(String email, UpdateProfileRequest request) {
        User user = getUser(email);

        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        return mapToDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public String updateAvatar(String email, MultipartFile file) {
        User user = getUser(email);

        // Upload lên Cloudinary
        Map result = cloudinaryService.uploadFile(file);
        String avatarUrl = (String) result.get("url");

        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);

        return avatarUrl;
    }

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = getUser(email);

        if (user.getPassword() == null) {
            throw new RuntimeException("Tài khoản này đăng nhập bằng Google, không thể đổi mật khẩu.");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
