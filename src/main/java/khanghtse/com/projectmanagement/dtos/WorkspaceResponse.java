package khanghtse.com.projectmanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceResponse {
    private UUID id;
    private String name;
    private String description;
    private UserDto owner; // Chỉ trả về thông tin cơ bản của owner
}
