package online.contactraphael.readabook.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.contactraphael.readabook.exception.EmailNotSentException;
import online.contactraphael.readabook.model.FileAttachment;
import online.contactraphael.readabook.service.service.NotificationService;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender javaMailSender;


    @Override
    public void sendEmailNotification(List<String> emails, String sender, String message, String subject, FileAttachment attachment) {
        sendEmail(emails, sender, message, subject, attachment);

    }

    @Override
    public void sendSMSNotification(List<String> phoneNumber, String sender, String message, String subject) {

    }

    private void sendEmail(List<String> emails, String sender, String message, String subject, FileAttachment attachment) {
        String[] recipients = {emails.get(0)};

        try {

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            mimeMessageHelper.setText(message, true);
            mimeMessageHelper.setTo(recipients);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setFrom(sender);

            if(attachment != null)
                mimeMessageHelper.addAttachment(attachment.getFileName(), attachment.getFile());


            javaMailSender.send(mimeMessage);

        }catch (MailSendException | MessagingException exception) {

            log.info("failed to send email to {} : {}", Arrays.toString(recipients), Instant.now());
            throw new EmailNotSentException("Failed to send email " + Arrays.toString(recipients));

        }
    }

    private void sendSms(List<String> phoneNumber, String sender, String message, String subject) {

    }
}
