package khanghtse.com.projectmanagement.controllers;

import khanghtse.com.projectmanagement.dtos.TaskColumnDto;
import khanghtse.com.projectmanagement.services.ITaskColumnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TaskColumnController {

    private final ITaskColumnService taskColumnService;

    // GET: Lấy danh sách cột (Để lấy columnId test tạo task)
    @GetMapping("/projects/{projectId}/columns")
    public ResponseEntity<List<TaskColumnDto.Response>> getColumns(@PathVariable UUID projectId) {
        return ResponseEntity.ok(taskColumnService.getColumns(projectId));
    }

    // POST: Tạo cột mới
    @PostMapping("/projects/{projectId}/columns")
    public ResponseEntity<TaskColumnDto.Response> createColumn(
            @PathVariable UUID projectId,
            @RequestBody TaskColumnDto.CreateColumnRequest request) {
        return new ResponseEntity<>(taskColumnService.createColumn(projectId, request), HttpStatus.CREATED);
    }

    // PATCH: Đổi tên cột
    @PatchMapping("/columns/{columnId}")
    public ResponseEntity<TaskColumnDto.Response> updateColumn(
            @PathVariable UUID columnId,
            @RequestBody TaskColumnDto.UpdateColumnRequest request) {
        return ResponseEntity.ok(taskColumnService.updateColumn(columnId, request));
    }

    // PUT: Di chuyển cột
    @PutMapping("/columns/{columnId}/move")
    public ResponseEntity<TaskColumnDto.Response> moveColumn(
            @PathVariable UUID columnId,
            @RequestBody TaskColumnDto.MoveColumnRequest request) {
        return ResponseEntity.ok(taskColumnService.moveColumn(columnId, request));
    }

    // DELETE: Xóa cột
    @DeleteMapping("/columns/{columnId}")
    public ResponseEntity<Void> deleteColumn(@PathVariable UUID columnId) {
        taskColumnService.deleteColumn(columnId);
        return ResponseEntity.noContent().build();
    }
}
