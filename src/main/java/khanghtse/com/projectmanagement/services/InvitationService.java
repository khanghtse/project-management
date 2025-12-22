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

        // --- 1. LOGIC MỚI: BẮT BUỘC NGƯỜI ĐƯỢC MỜI PHẢI CÓ TÀI KHOẢN ---
        // Nếu bạn muốn cho phép mời người chưa đăng ký, hãy bỏ đoạn check này đi.
//        User invitee = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Email " + email + " chưa đăng ký tài khoản trong hệ thống. Vui lòng bảo họ đăng ký trước."));

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
                .orElseThrow(() -> new RuntimeException("Lời mời không hợp lệ hoặc đường dẫn bị sai."));

        if (invitation.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Lời mời này đã hết hạn.");
        }

        // 2. Lấy user đang login
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng."));

        // --- 3. LOGIC QUAN TRỌNG: CHECK KHỚP EMAIL ---
        // Ngăn chặn trường hợp Login nick A nhưng bấm vào link mời của nick B
        if (!user.getEmail().equalsIgnoreCase(invitation.getEmail())) {
            throw new RuntimeException("Lỗi bảo mật: Bạn đang đăng nhập là " + user.getEmail() +
                    ", nhưng lời mời này được gửi tới " + invitation.getEmail() + ". Vui lòng đăng xuất và đăng nhập đúng tài khoản.");
        }

        // 4. Add vào Workspace
        WorkspaceMember member = new WorkspaceMember();
        member.setId(new WorkspaceMember.WorkspaceMemberId(invitation.getWorkspace().getId(), user.getId()));
        member.setWorkspace(invitation.getWorkspace());
        member.setUser(user);
        member.setRole(WorkspaceRole.MEMBER); // Mặc định là Member

        workspaceMemberRepository.save(member);

        // 5. Xóa Invitation sau khi dùng xong (để token không dùng lại được)
        invitationRepository.delete(invitation);
    }
}
