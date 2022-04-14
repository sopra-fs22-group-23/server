package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.mail.EmailParameters;
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

    public void sendMail(EmailParameters emailParameters) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setTo(emailParameters.getToAddresses().split("[,;]"));
            message.setFrom(emailParameters.getFrom(), "Wevent");
            message.setSubject(emailParameters.getSubject());
            if (StringUtils.isNotBlank(emailParameters.getCcAddresses()))
                message.setCc(emailParameters.getCcAddresses().split("[;,]"));
            if (StringUtils.isNotBlank(emailParameters.getBccAddresses()))
                message.setBcc(emailParameters.getBccAddresses().split("[;,]"));
            message.setText(emailParameters.getBody(), false);
        };
        mailSender.send(preparator);
        //logger.info("Email sent successfully To " + toAddresses +", " + ccAddresses +" with Subject " + subject);
    }

    public void sendInvitationMail(EventUser newSignup) {
        EmailParameters emailParameters = new EmailParameters();
        emailParameters.setFrom("wevent21@gmail.com");
        emailParameters.setSubject("You got invited to an new event!!!");
        emailParameters.setToAddresses(newSignup.getUser().getEmail());
        emailParameters.setBody("Welcome to the new Event " + newSignup.getEvent().getTitle() + ",\n The link is the folowing ………" + newSignup.getEvent().getId());
        sendMail(emailParameters);
    }
}
