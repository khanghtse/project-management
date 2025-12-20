package khanghtse.com.projectmanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponse {
    private UUID projectId;
    private List<ColumnResponse> columns;
}
