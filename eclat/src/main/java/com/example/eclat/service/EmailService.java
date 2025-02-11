package com.example.eclat.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;



    public void sendVerificationEmail(String to, String verificationUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Xác thực tài khoản của bạn");

            String emailContent = "<div style=\"font-family: Arial, sans-serif; padding: 20px; background-color: #f4f4f4;\">"
                    + "<div style=\"max-width: 500px; margin: auto; background-color: white; padding: 20px; border-radius: 10px; box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);\">"
                    + "<h2 style=\"color: #333; text-align: center;\">Xác nhận tài khoản của bạn</h2>"
                    + "<p style=\"font-size: 16px; color: #666;\">Cảm ơn bạn đã đăng ký! Vui lòng nhấp vào nút bên dưới để xác thực tài khoản của bạn:</p>"
                    + "<div style=\"text-align: center; margin: 20px 0;\">"
                    + "<a href=\"" + verificationUrl + "\" style=\"background-color: #28a745; color: white; padding: 12px 20px; text-decoration: none; border-radius: 5px; font-size: 16px; display: inline-block;\">Xác thực tài khoản</a>"
                    + "</div>"
                    + "<p style=\"font-size: 14px; color: #999; text-align: center;\">Nếu bạn không đăng ký tài khoản này, vui lòng bỏ qua email này.</p>"
                    + "</div>"
                    + "</div>";

            helper.setText(emailContent, true);

            mailSender.send(message);
            log.info("Email xác thực đã gửi đến: {}", to);
        } catch (MessagingException e) {
            log.error("Lỗi khi gửi email", e);
            throw new RuntimeException("Không thể gửi email xác thực.");
        }
    }

    public void sendPasswordChangeNotification(String to) {
        String subject = "🔐 Mật khẩu của bạn đã được thay đổi thành công!";

        // Tạo dữ liệu Thymeleaf
        Context context = new Context();
        Map<String, Object> variables = new HashMap<>();
        variables.put("email", to);
        context.setVariables(variables);

        // Load HTML từ file Thymeleaf
        String htmlContent = templateEngine.process("/password-change", context);

        sendHtmlEmail(to, subject, htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = gửi email với HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage());
        }
    }

    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // `true` để nội dung hỗ trợ HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage());
        }
    }




}
