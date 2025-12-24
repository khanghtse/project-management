package khanghtse.com.projectmanagement.controllers;

import khanghtse.com.projectmanagement.dtos.*;
import khanghtse.com.projectmanagement.services.ITaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TaskController {

    private final ITaskService taskService;

    // GET Board data (Columns + Tasks)
    @GetMapping("/projects/{projectId}/board")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable UUID projectId) {
        return ResponseEntity.ok(taskService.getBoard(projectId));
    }

    // POST Create Task
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @RequestBody CreateTaskRequest request,
            Principal principal) {
        return new ResponseEntity<>(taskService.createTask(projectId, request, principal.getName()), HttpStatus.CREATED);
    }

    // PUT Move Task (Drag & Drop)
    @PutMapping("/tasks/{taskId}/move")
    public ResponseEntity<TaskResponse> moveTask(
            @PathVariable UUID taskId,
            @RequestBody MoveTaskRequest request) {
        return ResponseEntity.ok(taskService.moveTask(taskId, request));
    }

    // PATCH Update Task (Sửa tiêu đề, mô tả, assignee...)
    @PatchMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID taskId,
            @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    // DELETE Task
    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build(); // Trả về 204 No Content
    }

    // --- SUBTASK API ---

    // POST /api/v1/tasks/{id}/subtasks
    @PostMapping("/tasks/{taskId}/subtasks")
    public ResponseEntity<SubTaskResponse> createSubTask(
            @PathVariable UUID taskId,
            @RequestBody CreateSubTaskRequest request) {
        return ResponseEntity.ok(taskService.createSubTask(taskId, request.getTitle()));
    }

    // PATCH /api/v1/subtasks/{id}/toggle
    @PatchMapping("/subtasks/{subTaskId}/toggle")
    public ResponseEntity<SubTaskResponse> toggleSubTask(@PathVariable UUID subTaskId) {
        return ResponseEntity.ok(taskService.toggleSubTask(subTaskId));
    }

    // DELETE /api/v1/subtasks/{id}
    @DeleteMapping("/subtasks/{subTaskId}")
    public ResponseEntity<Void> deleteSubTask(@PathVariable UUID subTaskId) {
        taskService.deleteSubTask(subTaskId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/tasks/{taskId}
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable UUID taskId) {
        return ResponseEntity.ok(taskService.getTask(taskId));
    }
}
