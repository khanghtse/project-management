package khanghtse.com.projectmanagement.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService implements ICloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public Map uploadFile(MultipartFile file) {
        try {
            // Upload lên Cloudinary, tự động nhận diện resource_type (ảnh, video, raw)
            return cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));
        } catch (IOException e) {
            throw new RuntimeException("Image upload fail: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Delete file fail: " + e.getMessage());
        }
    }
}
