package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventLocation;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.repository.EventRepository;
import ch.uzh.ifi.sopra22.repository.EventUserRepository;
import ch.uzh.ifi.sopra22.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebAppConfiguration
@SpringBootTest
public class EventUserServiceIntegrationTest {

    @Qualifier("eventRepository")
    @Autowired
    private EventRepository eventRepository;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Qualifier("eventUserRepository")
    @Autowired
    private EventUserRepository eventUserRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventUserService eventUserService;

    private User testUser;

    private Event testEvent;

    private EventUser testEventUser;

    @BeforeEach
    public void setup() {
        eventRepository.deleteAll();
        userRepository.deleteAll();
        eventUserRepository.deleteAll();


        testEvent = new Event();
        testEvent.setTitle("We Events");
        testEvent.setType(EventType.PUBLIC);
        testEvent.setStatus(EventStatus.IN_PLANNING);
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        testEvent.setEventLocation(eventLocation);

        testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");

        testEventUser = new EventUser();
        testEventUser.setEvent(testEvent);
        testEventUser.setUser(testUser);
        testEventUser.setRole(EventUserRole.ADMIN);
        testEventUser.setStatus(EventUserStatus.CONFIRMED);
        testEventUser.setCreationDate(new Date(System.currentTimeMillis()));
        testEventUser.setEventUserId(5L);

    }

    @AfterEach
    public void cleanup() {
        eventRepository.deleteAll();
        userRepository.deleteAll();
        eventUserRepository.deleteAll();
    }


    @Test
    public void getEventUsers_validInput_success() {
        ;
        /*
        //given
        EventUser createdEventUser = eventUserService.createEventUser(testEventUser);

        //when
        List<EventUser> eventUsers = eventUserRepository.findAll();

        //then
        assertEquals(createdEventUser.getEventUserId(), eventUsers.get(0).getEventUserId());
        assertEquals(createdEventUser.getEvent(), eventUsers.get(0).getEvent());
        assertEquals(createdEventUser.getUser(), eventUsers.get(0).getUser());
        assertEquals(createdEventUser.getRole(), eventUsers.get(0).getRole());
        assertEquals(createdEventUser.getStatus(), eventUsers.get(0).getStatus());
        */
    }



}
