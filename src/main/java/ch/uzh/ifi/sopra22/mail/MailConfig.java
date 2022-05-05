package ch.uzh.ifi.sopra22.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Configuration
public class MailConfig {
    @Autowired
    private Environment environment;

    /**
    @Value("${mail.support.username}")
    private String userName;
    @Value("${mail.support.password}")
    private String password;*/
    @Bean("gmail")
    public JavaMailSender gmailMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        //mailSender.setHost("smtp.mail.yahoo.com");
        mailSender.setPort(587);

        /**mailSender.setUsername("wevent21@gmail.com");
        mailSender.setPassword("Wevent$Group$23");*/
        //takes credentials locally from my config file application.properties, have to change between GMAIL and YAHOO credentials
        mailSender.setUsername(environment.getProperty("spring.mail.username"));
        System.out.println("Username: "+environment.getProperty("spring.mail.username"));
        mailSender.setPassword(environment.getProperty("spring.mail.password"));
        System.out.println("Password: "+environment.getProperty("spring.mail.password"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.debug", "false");

        return mailSender;
    }
    @Bean("yahoo")
    public Session yahooMailSender(){
        final String to = "kai.zinnhardt@gmail.com";
        final String from = "wevent23@yahoo.com";

        String host = "smtp.mail.yahoo.com";
        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(environment.getProperty("spring.mail.username"), environment.getProperty("spring.mail.password"));
            }
        });

        session.setDebug(true);
        return session;
    }
}
