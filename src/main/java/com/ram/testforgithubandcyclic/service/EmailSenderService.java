package com.ram.testforgithubandcyclic.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailSenderService {


    @Autowired
    private JavaMailSender javaMailSender;

    //-----------sending html as string inside emailbody------------------
    public void sendRegistrationEmail(String to, String htmlContent) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setFrom("ramsender123@gmail.com");
        helper.setSubject("Thank You for Joining Us!");
        helper.setText(htmlContent, true); // Set the second parameter to true for HTML content

        javaMailSender.send(message);



    }

    //--------------------------------------------------------------------------------

    //---------------normal email with string only-----------------------------------------------------------------
    public void sendSuccesfulAccountDelete(String to, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("ramsender123@gmail.com");
        message.setSubject("Account Deletion Confirmation");
        message.setText(body);

        try {
            javaMailSender.send(message);
        }catch (MailException ex){
            log.error("********* :"+ ex.getMessage());
        }
    }

//------------------------------------------------------------------------------
public void sendPasswordResetTokenUrlEmail(String to, String htmlContent) throws MessagingException {
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);


    helper.setTo(to);
    helper.setFrom("ramsender123@gmail.com");
    helper.setSubject("Password Reset URL");
    helper.setText(htmlContent, true); // Set the second parameter to true for HTML content

    try {
        javaMailSender.send(message);
    }catch (Exception ex){
        log.error("********* :"+ ex.getMessage());
    }
}



}
