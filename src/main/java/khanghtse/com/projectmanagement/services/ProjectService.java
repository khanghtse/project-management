package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.CreateProjectRequest;
import khanghtse.com.projectmanagement.dtos.ProjectResponse;
import khanghtse.com.projectmanagement.entities.*;
import khanghtse.com.projectmanagement.enums.ProjectRole;
import khanghtse.com.projectmanagement.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService implements IProjectService {

    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;
    private final TaskColumnRepository taskColumnRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProjectResponse createProject(UUID workspaceId, CreateProjectRequest request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        // 1. Validate Key trùng lặp trong workspace
        if (projectRepository.existsByWorkspaceIdAndKey(workspaceId, request.getKey())) {
            throw new RuntimeException("Project key " + request.getKey() + " already exists");
        }

        // 2. Tạo Project
        Project project = new Project();
        project.setName(request.getName());
        project.setKey(request.getKey().toUpperCase());
        //project.setDescription(request.getDescription());
        project.setWorkspace(workspace);
        project.setOwner(user);
        project.setCurrentTaskNumber(0); // Khởi đầu chưa có task nào

        Project savedProject = projectRepository.save(project);

        // 3. Tạo các cột Kanban mặc định (Default Workflow)
        createDefaultColumns(savedProject);

        // 4. Thêm người tạo làm MANAGER của dự án
        ProjectMember member = new ProjectMember();
        member.setId(new ProjectMember.ProjectMemberId(savedProject.getId(), user.getId()));
        member.setProject(savedProject);
        member.setUser(user);
        member.setRole(ProjectRole.MANAGER);

        projectMemberRepository.save(member);

        // --- Cập nhật constructor với createdAt, updatedAt ---
        return new ProjectResponse(
                savedProject.getId(),
                savedProject.getName(),
                savedProject.getKey(),
                savedProject.getDescription(),
                0,
                savedProject.getCreatedAt(),
                savedProject.getUpdatedAt()
        );
    }

    @Override
    public List<ProjectResponse> getProjectsInWorkspace(UUID workspaceId) {
        return projectRepository.findByWorkspaceId(workspaceId).stream()
                .map(p -> new ProjectResponse(
                        p.getId(),
                        p.getName(),
                        p.getKey(),
                        p.getDescription(),
                        0,
                        p.getCreatedAt(),
                        p.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    // Tạo cột mặc định: To Do -> In Progress -> Done
    private void createDefaultColumns(Project project) {
        // Sử dụng Lexorank đơn giản: "0|aaaaa", "0|aaaab", "0|aaaac"
        // Trong thực tế nên dùng library Lexorank để sinh string
        createColumn(project, "To Do", "0|i0000");       // Vị trí 1
        createColumn(project, "In Progress", "0|i0001"); // Vị trí 2
        createColumn(project, "Done", "0|i0002");        // Vị trí 3
    }

    private void createColumn(Project project, String name, String position) {
        TaskColumn col = new TaskColumn();
        col.setProject(project);
        col.setName(name);
        col.setPosition(position);
        taskColumnRepository.save(col);
    }
}
