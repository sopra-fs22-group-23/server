package ch.uzh.ifi.sopra22.mail;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailParametersTest {
    @Test
    public void passEmailParameters_success(){
        EmailParameters emailParameters = new EmailParameters();
        emailParameters.setBody("Test Body");
        emailParameters.setSubject("Test Subject");
        emailParameters.setToAddresses("test@gmail.com");
        emailParameters.setFrom("test@gmail.com");
        emailParameters.setCcAddresses("test@gmail.com");
        emailParameters.setBccAddresses("test@gmail.com");

        assertEquals("Test Body",emailParameters.getBody());
        assertEquals("Test Subject",emailParameters.getSubject());
        assertEquals("test@gmail.com",emailParameters.getToAddresses());
        assertEquals("test@gmail.com",emailParameters.getFrom());
        assertEquals("test@gmail.com",emailParameters.getCcAddresses());
        assertEquals("test@gmail.com",emailParameters.getBccAddresses());
    }

}