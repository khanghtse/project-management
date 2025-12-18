package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.CreateProjectRequest;
import khanghtse.com.projectmanagement.dtos.ProjectResponse;

import java.util.List;
import java.util.UUID;

public interface IProjectService {
    ProjectResponse createProject(UUID workspaceId, CreateProjectRequest request, String userEmail);
    List<ProjectResponse> getProjectsInWorkspace(UUID workspaceId);
}
