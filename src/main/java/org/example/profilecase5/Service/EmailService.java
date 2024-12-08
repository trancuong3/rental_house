package org.example.profilecase5.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {
            System.out.println("Attempting to send email to: " + to);  // Log thông tin gửi email
            mailSender.send(message);
            System.out.println("Email sent successfully.");
        } catch (MailException e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();  // Log chi tiết lỗi
        }
    }
}
