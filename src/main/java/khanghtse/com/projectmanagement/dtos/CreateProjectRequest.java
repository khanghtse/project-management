package khanghtse.com.projectmanagement.dtos;

import lombok.Data;

@Data
public class CreateProjectRequest {
    private String name;
    private String key; // VD: "PRJ" (để tạo task ID: PRJ-1)
    private String description;
}
