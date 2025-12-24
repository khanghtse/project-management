package khanghtse.com.projectmanagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentDto {
    @Data
    public static class CreateCommentRequest {
        private String content;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private UUID id;
        private String content;
        private UserDto author; // Tái sử dụng UserDto cũ
        private LocalDateTime createdAt;
    }
}
