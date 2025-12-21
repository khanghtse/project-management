package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.CreateWorkspaceRequest;
import khanghtse.com.projectmanagement.dtos.UserDto;
import khanghtse.com.projectmanagement.dtos.WorkspaceResponse;

import java.util.List;
import java.util.UUID;

public interface IWorkspaceService {
    WorkspaceResponse createWorkspace(CreateWorkspaceRequest request, String userEmail);
    List<WorkspaceResponse> getMyWorkspaces(String userEmail);
    List<UserDto> getWorkspaceMembers(UUID workspaceId);
}
