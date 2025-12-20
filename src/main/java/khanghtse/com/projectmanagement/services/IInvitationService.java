package khanghtse.com.projectmanagement.services;

import java.util.UUID;

public interface IInvitationService {

    void sendInvitation(UUID workspaceId, String email, String inviterEmail);
    void acceptInvitation(String token, String userEmail);
}
