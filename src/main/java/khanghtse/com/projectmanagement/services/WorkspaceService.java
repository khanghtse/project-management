package khanghtse.com.projectmanagement.services;


import khanghtse.com.projectmanagement.dtos.CreateWorkspaceRequest;
import khanghtse.com.projectmanagement.dtos.UserDto;
import khanghtse.com.projectmanagement.dtos.WorkspaceResponse;
import khanghtse.com.projectmanagement.entities.User;
import khanghtse.com.projectmanagement.entities.Workspace;
import khanghtse.com.projectmanagement.entities.WorkspaceMember;
import khanghtse.com.projectmanagement.enums.WorkspaceRole;
import khanghtse.com.projectmanagement.repositories.UserRepository;
import khanghtse.com.projectmanagement.repositories.WorkspaceMemberRepository;
import khanghtse.com.projectmanagement.repositories.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceService implements IWorkspaceService{
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;

    @Override
    public WorkspaceResponse createWorkspace(CreateWorkspaceRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. create workspace
        Workspace workspace = new Workspace();
        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());
        workspace.setOwner(user);

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        // 2. Thêm người tạo vào danh sách thành viên với quyền ADMIN
        WorkspaceMember member = new WorkspaceMember();
        member.setId(new WorkspaceMember.WorkspaceMemberId(savedWorkspace.getId(), user.getId()));
        member.setWorkspace(savedWorkspace);
        member.setUser(user);
        member.setRole(WorkspaceRole.ADMIN);

        workspaceMemberRepository.save(member);
        return mapToDto(savedWorkspace);
    }

    @Override
    @Transactional
    public List<WorkspaceResponse> getMyWorkspaces(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return workspaceRepository.findAllByUserId(user.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getWorkspaceMembers(UUID workspaceId) {
        // Kiểm tra workspace tồn tại (Optional)
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new RuntimeException("Workspace not found");
        }

        return workspaceMemberRepository.findByWorkspaceId(workspaceId).stream()
                .map(member -> {
                    User u = member.getUser();
                    return new UserDto(u.getId(), u.getName(), u.getEmail(), u.getPhoneNumber(), u.getAvatarUrl());
                })
                .collect(Collectors.toList());
    }

    // Helper mapper
    private WorkspaceResponse mapToDto(Workspace w) {
        UserDto ownerDto = new UserDto(w.getOwner().getId(), w.getOwner().getName(), w.getOwner().getEmail(), w.getOwner().getPhoneNumber(), w.getOwner().getAvatarUrl());
        return new WorkspaceResponse(w.getId(), w.getName(), w.getDescription(), ownerDto);
    }
}
