package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.entities.Invitation;
import khanghtse.com.projectmanagement.entities.User;
import khanghtse.com.projectmanagement.entities.Workspace;
import khanghtse.com.projectmanagement.entities.WorkspaceMember;
import khanghtse.com.projectmanagement.enums.WorkspaceRole;
import khanghtse.com.projectmanagement.repositories.InvitationRepository;
import khanghtse.com.projectmanagement.repositories.UserRepository;
import khanghtse.com.projectmanagement.repositories.WorkspaceMemberRepository;
import khanghtse.com.projectmanagement.repositories.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationService implements IInvitationService {

    private final InvitationRepository invitationRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final IEmailService emailService;

    // Gửi lời mời
    @Override
    @Transactional
    public void sendInvitation(UUID workspaceId, String email, String inviterEmail) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        User inviter = userRepository.findByEmail(inviterEmail)
                .orElseThrow(() -> new RuntimeException("Inviter not found"));

        // 1. Check xem user đã ở trong workspace chưa
        userRepository.findByEmail(email).ifPresent(user -> {
            boolean isMember = workspaceMemberRepository.findById(new WorkspaceMember.WorkspaceMemberId(workspaceId, user.getId())).isPresent();
            if (isMember) {
                throw new RuntimeException("User is already a member of this workspace");
            }
        });

        // 2. Check xem đã mời chưa (tránh spam)
        if (invitationRepository.existsByWorkspaceIdAndEmail(workspaceId, email)) {
            throw new RuntimeException("Invitation already sent to this email");
        }

        // 3. Tạo token & Lưu Invitation
        String token = UUID.randomUUID().toString();
        Invitation invitation = new Invitation(email, token, workspace, inviter);
        invitationRepository.save(invitation);

        // 4. Gửi email
        // Giả sử Frontend chạy ở localhost:5173
        String inviteLink = "http://localhost:5173/accept-invite?token=" + token;
        emailService.sendInvitationEmail(email, workspace.getName(), inviteLink);
    }

    // Chấp nhận lời mời
    @Override
    @Transactional
    public void acceptInvitation(String token, String userEmail) {
        // 1. Validate Token
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

        if (invitation.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invitation expired");
        }

        // 2. Validate User (Người chấp nhận phải đúng là người được mời - optional, hoặc user mới)
        // Ở đây ta đơn giản là lấy user đang login hiện tại để add vào.
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // (Optional: Check email user hiện tại có khớp với email trong invitation không?)
        // if (!user.getEmail().equals(invitation.getEmail())) ...

        // 3. Add vào Workspace
        WorkspaceMember member = new WorkspaceMember();
        member.setId(new WorkspaceMember.WorkspaceMemberId(invitation.getWorkspace().getId(), user.getId()));
        member.setWorkspace(invitation.getWorkspace());
        member.setUser(user);
        member.setRole(WorkspaceRole.MEMBER); // Default role

        workspaceMemberRepository.save(member);

        // 4. Xóa Invitation sau khi dùng xong
        invitationRepository.delete(invitation);
    }
}
