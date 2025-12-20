package khanghtse.com.projectmanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class TaskColumnDto {
    @Data
    public static class CreateColumnRequest {
        private String name;
    }

    @Data
    public static class UpdateColumnRequest {
        private String name;
    }

    @Data
    public static class MoveColumnRequest {
        private String prevRank; // Rank của cột đứng trước
        private String nextRank; // Rank của cột đứng sau
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private UUID id;
        private String name;
        private String position;
        private UUID projectId;
    }
}
