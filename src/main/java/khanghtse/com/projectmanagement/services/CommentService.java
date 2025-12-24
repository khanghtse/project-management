package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.CommentDto;
import khanghtse.com.projectmanagement.dtos.UserDto;
import khanghtse.com.projectmanagement.entities.Comment;
import khanghtse.com.projectmanagement.entities.Task;
import khanghtse.com.projectmanagement.entities.User;
import khanghtse.com.projectmanagement.repositories.CommentRepository;
import khanghtse.com.projectmanagement.repositories.TaskRepository;
import khanghtse.com.projectmanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService{

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public List<CommentDto.Response> getComments(UUID taskId) {
        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto.Response addComment(UUID taskId, String content, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // (Có thể thêm bước check user có thuộc workspace chứa task này không để bảo mật hơn)

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setAuthor(user);
        comment.setContent(content);

        Comment savedComment = commentRepository.save(comment);
        return mapToDto(savedComment);
    }

    private CommentDto.Response mapToDto(Comment c) {
        UserDto authorDto = new UserDto(
                c.getAuthor().getId(),
                c.getAuthor().getName(),
                c.getAuthor().getEmail(),
                c.getAuthor().getAvatarUrl()
        );
        return new CommentDto.Response(c.getId(), c.getContent(), authorDto, c.getCreatedAt());
    }
}
