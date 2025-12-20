package khanghtse.com.projectmanagement.controllers;

import khanghtse.com.projectmanagement.dtos.InviteRequest;
import khanghtse.com.projectmanagement.services.IInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InvitationController {

    private final IInvitationService invitationService;

    // API: Gửi lời mời (Chỉ Admin workspace mới được gọi - cần check quyền sau)
    // POST /api/v1/workspaces/{id}/invite
    @PostMapping("/workspaces/{workspaceId}/invite")
    public ResponseEntity<String> inviteMember(
            @PathVariable UUID workspaceId,
            @RequestBody InviteRequest request,
            Principal principal) {

        invitationService.sendInvitation(workspaceId, request.getEmail(), principal.getName());
        return ResponseEntity.ok("Invitation sent successfully");
    }

    // API: Chấp nhận lời mời (User click link trong mail -> Frontend -> API này)
    // POST /api/v1/invitations/accept?token=...
    @PostMapping("/invitations/accept")
    public ResponseEntity<String> acceptInvitation(
            @RequestParam String token,
            Principal principal) {

        invitationService.acceptInvitation(token, principal.getName());
        return ResponseEntity.ok("Joined workspace successfully");
    }
}
