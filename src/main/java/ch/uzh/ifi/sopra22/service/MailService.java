package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.User;
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
        emailParameters.setBody("Hi "+ newSignup.getUser().getName() + ",\n \n" +
                "Welcome to the new event: " + newSignup.getEvent().getTitle() + "!!\n \n The link to this event is the folowing https://sopra-fs22-group23-client.herokuapp.com/" +newSignup.getEvent().getId() + "(……… e.g. http://localhost:8080/events/" + newSignup.getEvent().getId()+")") ;
        sendMail(emailParameters);
    }

    public void sendUpdateEventMail(EventUser eventUser, User userUpdate) {
        EmailParameters emailParameters = new EmailParameters();
        emailParameters.setFrom("wevent21@gmail.com");
        emailParameters.setSubject("The event '" + eventUser.getEvent().getTitle() + "' received an updated");
        emailParameters.setToAddresses(eventUser.getUser().getEmail());
        emailParameters.setBody("Hi "+ eventUser.getUser().getName() + ",\n \n" +
                "The event '" + eventUser.getEvent().getTitle() + "' recently received an update to its parameters. This update was conducted by "+ userUpdate.getName()+
                ".\n \n The link to the updated event is the folowing https://sopra-fs22-group23-client.herokuapp.com/" + eventUser.getEvent().getId() + "(……… e.g. http://localhost:8080/events/" + eventUser.getEvent().getId()+")");
        sendMail(emailParameters);
    }

    public void sendUnregisterdUserNotification(User unregisteredUser, Event event) {
        EmailParameters emailParameters = new EmailParameters();
        emailParameters.setFrom("wevent21@gmail.com");
        emailParameters.setSubject("The event '" + event.getTitle() + "' received an updated");
        emailParameters.setToAddresses(unregisteredUser.getEmail());
        emailParameters.setBody("Hi Wevent Site visitor" + ",\n \n" +
                "You expressed intrest in the event '" + event.getTitle() + "'. The link to the event is the folowing https://sopra-fs22-group23-client.herokuapp.com/" + event.getId() + "(……… e.g. http://localhost:8080/events/" + event.getId()+")");
        sendMail(emailParameters);
    }
}
