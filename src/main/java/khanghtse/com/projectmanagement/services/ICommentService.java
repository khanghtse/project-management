package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.CommentDto;

import java.util.List;
import java.util.UUID;

public interface ICommentService {

    List<CommentDto.Response> getComments(UUID taskId);
    CommentDto.Response addComment(UUID taskId, String content, String userEmail);
}
