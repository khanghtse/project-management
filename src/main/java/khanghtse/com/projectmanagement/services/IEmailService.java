package khanghtse.com.projectmanagement.services;

public interface IEmailService {
    void sendInvitationEmail(String toEmail, String workspaceName, String inviteLink);
    void sendSimpleEmail(String to, String subject, String text);
}
