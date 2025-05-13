package com.datsan.caulong.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendEmail(String email, String content) throws MessagingException {
        // tạo đối tượng cho phép gửi email nhiều định dạng -> tạo từ mailSender (theo cấu hình gốc)
        MimeMessage message = mailSender.createMimeMessage();

        // tạo messHelper giúp tạo nội dung của email
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

        messageHelper.setFrom("tienolympia2020@gmail.com");
        messageHelper.setTo(email);
        messageHelper.setSubject("Xác thực tài khoản");
        messageHelper.setText(content,true);


        mailSender.send(message);
    }
}
