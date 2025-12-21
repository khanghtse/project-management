package khanghtse.com.projectmanagement.repositories;

import khanghtse.com.projectmanagement.entities.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, WorkspaceMember.WorkspaceMemberId> {
    List<WorkspaceMember> findByWorkspaceId(UUID workspaceId);
}
