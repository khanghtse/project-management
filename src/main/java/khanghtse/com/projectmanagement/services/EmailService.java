package khanghtse.com.projectmanagement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {
    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendInvitationEmail(String toEmail, String workspaceName, String inviteLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@pms-system.com");
        message.setTo(toEmail);
        message.setSubject("Lời mời tham gia Workspace: " + workspaceName);
        message.setText("Chào bạn,\n\n" +
                "Bạn đã được mời tham gia Workspace '" + workspaceName + "' trên hệ thống quản lý dự án PMS.\n" +
                "Vui lòng click vào đường dẫn sau để chấp nhận lời mời:\n\n" +
                inviteLink + "\n\n" +
                "Link này sẽ hết hạn sau 3 ngày.\n" +
                "Trân trọng,");

        mailSender.send(message);
    }
}
