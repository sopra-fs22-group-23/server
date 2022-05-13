package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.mail.EmailParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.web.PagedResourcesAssemblerArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {

    //private static final Logger logger = (Logger) LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender mailSender;
    private final Session yahooSession;

    @Autowired
    public MailService(@Qualifier("gmail")JavaMailSender mailSender, @Qualifier("yahoo")Session yahooSession) {
        this.mailSender = mailSender;
        this.yahooSession = yahooSession;
    }
/** Send a mail via GMAIL
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
    }*/
    public void yahooSendMail(EmailParameters emailParameters){
        try {
            MimeMessage message = new MimeMessage(yahooSession);

            message.setFrom(new InternetAddress(emailParameters.getFrom()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailParameters.getToAddresses()));
            message.setSubject(emailParameters.getSubject());
            message.setText(emailParameters.getBody());

            Transport.send(message);
        } catch (MessagingException e) {
            //throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Wrong email or credentials");
            //e.printStackTrace();
            System.out.println("Wrong email or credentials used");
        }
    }

    public void sendInvitationMail(EventUser newSignup) {
        EmailParameters emailParameters = new EmailParameters();
        emailParameters.setFrom("wevent23@yahoo.com");
        emailParameters.setSubject("You got invited to an new event!!!");
        emailParameters.setToAddresses(newSignup.getUser().getEmail());
        emailParameters.setBody("Hi "+ newSignup.getUser().getName() + ",\n \n" +
                "Welcome to the new event: " + newSignup.getEvent().getTitle() + "!!\n \n The link to this event is the folowing https://sopra-fs22-group23-client.herokuapp.com/event/" +newSignup.getEvent().getId());
        yahooSendMail(emailParameters);
    }

    public void sendUpdateEventMail(EventUser eventUser, User userUpdate) {
        EmailParameters emailParameters = new EmailParameters();
        emailParameters.setFrom("wevent23@yahoo.com");
        emailParameters.setSubject("The event '" + eventUser.getEvent().getTitle() + "' received an updated");
        emailParameters.setToAddresses(eventUser.getUser().getEmail());
        emailParameters.setBody("Hi "+ eventUser.getUser().getName() + ",\n \n" +
                "The event '" + eventUser.getEvent().getTitle() + "' recently received an update to its parameters. This update was conducted by "+ userUpdate.getName()+
                ".\n \n The link to the updated event is the folowing https://sopra-fs22-group23-client.herokuapp.com/event/" + eventUser.getEvent().getId());
        yahooSendMail(emailParameters);
    }

    public void sendUnregisterdUserNotification(User unregisteredUser, Event event) {
        EmailParameters emailParameters = new EmailParameters();
        emailParameters.setFrom("wevent23@yahoo.com");
        emailParameters.setSubject("The event '" + event.getTitle() + "' received your interest");
        emailParameters.setToAddresses(unregisteredUser.getEmail());
        emailParameters.setBody("Hi Wevent Site visitor" + ",\n \n" +
                "You expressed intrest in the event '" + event.getTitle() + "'. The link to the event is the folowing https://sopra-fs22-group23-client.herokuapp.com/event/" + event.getId());
        yahooSendMail(emailParameters);
    }
}
