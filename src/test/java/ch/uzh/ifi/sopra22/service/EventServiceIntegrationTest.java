package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class EventServiceIntegrationTest {

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

    /**private User testUser;

    private Event testEvent;**/

    @BeforeEach
    public void setup() {
        eventRepository.deleteAll();
        userRepository.deleteAll();
        eventUserRepository.deleteAll();

    }
/**
    @AfterEach
    public void cleanup() {
        eventRepository.deleteAll();
        userRepository.deleteAll();
        eventUserRepository.deleteAll();
    }*/

    @Test
    public void getEvents_validInput_success(){
        Event testEvent = new Event();
        testEvent.setTitle("We Events");
        testEvent.setType(EventType.PUBLIC);
        testEvent.setStatus(EventStatus.IN_PLANNING);
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        testEvent.setEventLocation(eventLocation);

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        Event createdEvent = eventService.createEvent(testEvent);

        // when
        List<Event> eventList = eventRepository.findAll();

        // then
        assertEquals(createdEvent.getTitle(), eventList.get(0).getTitle());
        assertEquals(createdEvent.getType(), eventList.get(0).getType());
        assertEquals(createdEvent.getStatus(), eventList.get(0).getStatus());
    }

    @Test
    public void getEventById_validInput_success() {
        Event testEvent = new Event();
        testEvent.setTitle("We Events");
        testEvent.setType(EventType.PUBLIC);
        testEvent.setStatus(EventStatus.IN_PLANNING);
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        testEvent.setEventLocation(eventLocation);

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        Event createdEvent = eventService.createEvent(testEvent);

        //when
        Event foundEvent = eventService.getEventByIDNum(createdEvent.getId());

        //then
        assertNotNull(foundEvent);
        assertEquals(createdEvent.getTitle(), foundEvent.getTitle());
        assertEquals(createdEvent.getType(), foundEvent.getType());
        assertEquals(createdEvent.getStatus(), foundEvent.getStatus());
    }
/**
    @Test
    public void updateEvent_validInput_success() {
        Event testEvent = new Event();
        testEvent.setTitle("We Events");
        testEvent.setType(EventType.PUBLIC);
        testEvent.setStatus(EventStatus.IN_PLANNING);
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        testEvent.setEventLocation(eventLocation);

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");

        User createdUser = userService.createUser(testUser);
        Event createdEvent = eventService.createEvent(testEvent);

        EventUser eventUser = eventService.createEventUser(createdUser,createdEvent,EventUserRole.ADMIN);
        eventService.linkEventUsertoEvent(createdEvent,eventUser);
        userService.linkEventUsertoUser(createdUser,eventUser);



        Event updateEvent = new Event();
        updateEvent.setTitle("We Events");
        //updateEvent.setType(EventType.PUBLIC);
        //updateEvent.setStatus(EventStatus.IN_PLANNING);
        EventLocation ueventLocation = new EventLocation();
        ueventLocation.setName("Zurich");
        ueventLocation.setLatitude(1.02F);
        ueventLocation.setLongitude(1.02F);
        updateEvent.setEventLocation(ueventLocation);

        //when
        eventService.updateEvent(createdEvent,createdUser,updateEvent);

        Event event = eventService.getEventByIDNum(testEvent.getId());

        assertEquals(event.getTitle(),updateEvent.getTitle());
        assertEquals(event.getEventLocation().getName(), updateEvent.getEventLocation().getName());
        assertEquals(event.getEventLocation().getLatitude(),updateEvent.getEventLocation().getLatitude());
        assertEquals(event.getEventLocation().getLongitude(), updateEvent.getEventLocation().getLongitude());


    }*/
}
