package khanghtse.com.projectmanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

public class ActivityLogDto {

    @Data
    @AllArgsConstructor
    public static class Response {
        private UUID id;
        private String content;
        private String userName;
        private String userAvatar;
        private LocalDateTime createdAt;
    }
}
