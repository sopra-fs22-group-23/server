package ch.uzh.ifi.sopra22.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class GmailConfig {
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
        mailSender.setPort(587);

        /**mailSender.setUsername("wevent21@gmail.com");
        mailSender.setPassword("Wevent$Group$23");*/
        //takes credentials locally from my config file application.properties
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
}
