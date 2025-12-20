package khanghtse.com.projectmanagement.services;

public interface IEmailService {
    void sendInvitationEmail(String toEmail, String workspaceName, String inviteLink);
}
