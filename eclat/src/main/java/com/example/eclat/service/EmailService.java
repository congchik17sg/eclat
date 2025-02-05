package com.example.eclat.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String verificationUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true , "UTF-8");

            helper.setTo(to);
            helper.setSubject("Xác thực tài khoản của bạn");
            helper.setText("<p>Nhấp vào liên kết dưới đây để xác thực tài khoản của bạn:</p>" +
                    "<p><a href=\"" + verificationUrl + "\">Xác thực tài khoản</a></p>", true);

            mailSender.send(message);
            log.info("Email xác thực đã gửi đến: {}", to);
        } catch (MessagingException e) {
            log.error("Lỗi khi gửi email", e);
            throw new RuntimeException("Không thể gửi email xác thực.");
        }
    }

}
