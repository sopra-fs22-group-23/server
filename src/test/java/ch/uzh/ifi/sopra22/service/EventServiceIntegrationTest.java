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
import ch.uzh.ifi.sopra22.rest.dto.EventUserPostDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

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

    @Autowired
    private MailService mailService;

    /**private User testUser;

    private Event testEvent;**/

    @BeforeEach
    public void setup() {
        eventUserRepository.deleteAll();
        eventRepository.deleteAll();
        userRepository.deleteAll();

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

    @Test
    public void updateEvent_validInput_success() {
        //Setup
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
        updateEvent.setTitle("Test Events");
        EventLocation ueventLocation = new EventLocation();
        ueventLocation.setName("Frankfurt");
        ueventLocation.setLatitude(1F);
        ueventLocation.setLongitude(1F);
        updateEvent.setEventLocation(ueventLocation);

        //when
        eventService.updateEvent(createdEvent,createdUser,updateEvent);

        //then
        Event event = eventService.getEventByIDNum(testEvent.getId());

        assertEquals(event.getTitle(),updateEvent.getTitle());
        assertEquals(event.getEventLocation().getName(), updateEvent.getEventLocation().getName());
        assertEquals(event.getEventLocation().getLatitude(),updateEvent.getEventLocation().getLatitude());
        assertEquals(event.getEventLocation().getLongitude(), updateEvent.getEventLocation().getLongitude());
    }
    @Test
    public void test_createEventUser_validInput(){
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

        assertEquals(eventUser.getUser().getId(),testUser.getId());
        assertEquals(eventUser.getEvent().getId(),testEvent.getId());
        assertEquals("CONFIRMED",eventUser.getStatus().toString());
        assertEquals("ADMIN",eventUser.getRole().toString());
    }
    @Test
    public void test_createEvent_success(){
        Event testEvent = new Event();
        testEvent.setTitle("We Events");
        testEvent.setType(EventType.PUBLIC);
        testEvent.setStatus(EventStatus.IN_PLANNING);
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        testEvent.setEventLocation(eventLocation);

        Event actualEvent = eventService.createEvent(testEvent);

        assertEquals(testEvent.getTitle(),actualEvent.getTitle());
        assertEquals(testEvent.getEventDate(),actualEvent.getEventDate());
        assertEquals(testEvent.getDescription(),actualEvent.getDescription());
        assertEquals(testEvent.getStatus(),actualEvent.getStatus());
        assertEquals(testEvent.getEventLocation(),actualEvent.getEventLocation());
        assertEquals(testEvent.getType(),actualEvent.getType());
    }
    @Test
    public void test_EventUserDelete_success(){
        //Setup
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

        //create second user
        User guestUser = new User();
        guestUser.setName("guestName");
        guestUser.setUsername("guestUsername");
        guestUser.setPassword("password10");
        User createguestUser = userService.createUser(guestUser);

        EventUser guestEventUser = eventService.validEventUserPOST(createguestUser,createdEvent,new EventUserPostDTO(),"Bearer "+ createguestUser.getToken());
        eventService.linkEventUsertoEvent(createdEvent,guestEventUser);
        userService.linkEventUsertoUser(createguestUser,guestEventUser);

        //when
        eventService.validEventUserDELETE(createdEvent,createguestUser.getId(),"Bearer "+ createdUser.getToken());

        //then
        assertEquals(createdEvent.getEventUsers().size(),1);
        assertEquals(createdUser.getEventUsers().size(),1);
        assertEquals(createdEvent.getEventUsers().get(0).getEventUserId(),eventUser.getEventUserId());

        //assertThrows(ResponseStatusException.class, () -> eventService.validEventUserDELETE(createdEvent,createdUser.getId(), "Bearer "+createdUser.getToken()));
    }
}
