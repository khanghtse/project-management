package khanghtse.com.projectmanagement.controllers;

import khanghtse.com.projectmanagement.dtos.CommentDto;
import khanghtse.com.projectmanagement.services.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class CommentController {

    private final ICommentService commentService;

    // GET /api/v1/tasks/{taskId}/comments
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<CommentDto.Response>> getComments(@PathVariable UUID taskId) {
        return ResponseEntity.ok(commentService.getComments(taskId));
    }

    // POST /api/v1/tasks/{taskId}/comments
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<CommentDto.Response> addComment(
            @PathVariable UUID taskId,
            @RequestBody CommentDto.CreateCommentRequest request,
            Principal principal) {
        return ResponseEntity.ok(commentService.addComment(taskId, request.getContent(), principal.getName()));
    }
}
