package khanghtse.com.projectmanagement.controllers;

import khanghtse.com.projectmanagement.dtos.CreateWorkspaceRequest;
import khanghtse.com.projectmanagement.dtos.WorkspaceResponse;
import khanghtse.com.projectmanagement.services.IWorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final IWorkspaceService workspaceService;

    // API: Lấy danh sách Workspace của tôi
    // GET /api/v1/workspaces
    @GetMapping
    public ResponseEntity<List<WorkspaceResponse>> getMyWorkspaces(Principal principal) {
        // Principal.getName() sẽ trả về email từ JWT Token
        return ResponseEntity.ok(workspaceService.getMyWorkspaces(principal.getName()));
    }

    // API: Tạo Workspace mới
    // POST /api/v1/workspaces
    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(
            @RequestBody CreateWorkspaceRequest request,
            Principal principal) {

        WorkspaceResponse response = workspaceService.createWorkspace(request, principal.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
