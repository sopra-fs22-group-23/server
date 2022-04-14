package ch.uzh.ifi.sopra22.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class MailService {

    //private static final Logger logger = (Logger) LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender mailSender;

    @Autowired
    public MailService(@Qualifier("gmail")JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String from, String subject, String toAddresses, String ccAddresses, String bccAddresses, String body) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setTo(toAddresses.split("[,;]"));
            message.setFrom(from, "Wevent");
            message.setSubject(subject);
            if (StringUtils.isNotBlank(ccAddresses))
                message.setCc(ccAddresses.split("[;,]"));
            if (StringUtils.isNotBlank(bccAddresses))
                message.setBcc(bccAddresses.split("[;,]"));
            message.setText(body, false);
        };
        mailSender.send(preparator);
        //logger.info("Email sent successfully To " + toAddresses +", " + ccAddresses +" with Subject " + subject);
    }
}
