package khanghtse.com.projectmanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnResponse {
    private UUID id;
    private String name;
    private List<TaskResponse> tasks;
}
