package com.example.userregistration.services.impl;

import com.example.userregistration.services.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
@Slf4j
public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.from}")
    String mailFrom;

    @Override
    public boolean sendMail(String mailTo) {
        MimeMessage message=mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailFrom);
            helper.setTo(mailTo);
            helper.setSubject("Registration mail");
            helper.setText("You have already successfully finish registration.", true);
            //FileSystemResource file = new FileSystemResource(new File("test.txt"));
            //String fileName = file.getFilename();
            //helper.addAttachment(fileName, file);
            mailSender.send(message);
            log.info("邮件已经发送");
            return true;
        } catch (Exception e) {
            log.error("{}",e);
        }
        return false;
    }
}
