package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.ChangePasswordRequest;
import khanghtse.com.projectmanagement.dtos.UpdateProfileRequest;
import khanghtse.com.projectmanagement.dtos.UserDto;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {

    UserDto getCurrentUser(String email);
    UserDto updateProfile(String email, UpdateProfileRequest request);
    String updateAvatar(String email, MultipartFile file);
    void changePassword(String email, ChangePasswordRequest request);
}
