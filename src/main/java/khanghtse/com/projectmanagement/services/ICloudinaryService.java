package khanghtse.com.projectmanagement.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ICloudinaryService {

    Map uploadFile(MultipartFile file);

    void deleteFile(String publicId);
}
