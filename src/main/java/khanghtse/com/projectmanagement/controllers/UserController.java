package khanghtse.com.projectmanagement.controllers;

import khanghtse.com.projectmanagement.dtos.ChangePasswordRequest;
import khanghtse.com.projectmanagement.dtos.UpdateProfileRequest;
import khanghtse.com.projectmanagement.dtos.UserDto;
import khanghtse.com.projectmanagement.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    // Lấy thông tin bản thân
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        return ResponseEntity.ok(userService.getCurrentUser(principal.getName()));
    }

    // Cập nhật thông tin cơ bản
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateProfile(
            @RequestBody UpdateProfileRequest request,
            Principal principal) {
        return ResponseEntity.ok(userService.updateProfile(principal.getName(), request));
    }

    // Đổi Avatar
    @PostMapping("/me/avatar")
    public ResponseEntity<?> updateAvatar(
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        String avatarUrl = userService.updateAvatar(principal.getName(), file);
        return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
    }

    // Đổi mật khẩu
    @PutMapping("/me/password")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal principal) {
        userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok("Đổi mật khẩu thành công.");
    }
}
