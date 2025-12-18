package khanghtse.com.projectmanagement.controllers;


import khanghtse.com.projectmanagement.dtos.CreateProjectRequest;
import khanghtse.com.projectmanagement.dtos.ProjectResponse;
import khanghtse.com.projectmanagement.services.IProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1") // Base path chung
@RequiredArgsConstructor
public class ProjectController {

    private final IProjectService projectService;

    // API: Lấy danh sách Project trong một Workspace
    // GET /api/v1/workspaces/{workspaceId}/projects
    @GetMapping("/workspaces/{workspaceId}/projects")
    public ResponseEntity<List<ProjectResponse>> getProjects(
            @PathVariable UUID workspaceId) {
        return ResponseEntity.ok(projectService.getProjectsInWorkspace(workspaceId));
    }

    // API: Tạo Project mới trong một Workspace
    // POST /api/v1/workspaces/{workspaceId}/projects
    @PostMapping("/workspaces/{workspaceId}/projects")
    public ResponseEntity<ProjectResponse> createProject(
            @PathVariable UUID workspaceId,
            @RequestBody CreateProjectRequest request,
            Principal principal) {

        ProjectResponse response = projectService.createProject(workspaceId, request, principal.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // API: Lấy chi tiết 1 Project (Sẽ dùng cho trang Board sau này)
    // GET /api/v1/projects/{id}
    // (Bạn có thể bổ sung method này vào Service sau)
    /* @GetMapping("/projects/{projectId}")
    public ResponseEntity<PmsDto.ProjectResponse> getProjectDetails(@PathVariable UUID projectId) {
        // return ResponseEntity.ok(projectService.getProjectDetails(projectId));
        return ResponseEntity.ok().build(); // Placeholder
    }
    */
}
