package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;
import ch.uzh.ifi.sopra22.constants.UserStatus;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventLocation;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.repository.EventRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserService userService;

    @Mock
    private EventUserService eventUserService;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("We Events");
        testEvent.setType(EventType.PUBLIC);
        testEvent.setStatus(EventStatus.IN_PLANNING);
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        testEvent.setEventLocation(eventLocation);

        // when -> any object is being save in the userRepository -> return the dummy

        //given
        testUser = new User();
        testUser.setId(2L);
        testUser.setUsername("username");
        testUser.setPassword("password");
        testUser.setName("name");
        testUser.setToken("12345");



        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(testEvent);
    }

    @Test
    public void createEvent_validInput() {

        Event createdEvent = eventService.createEvent(testEvent);

        // then
        Mockito.verify(eventRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(createdEvent.getId(), testEvent.getId());
        assertEquals(createdEvent.getTitle(), testEvent.getTitle());
        assertEquals(createdEvent.getDescription(), testEvent.getDescription());
        assertEquals(createdEvent.getType(), testEvent.getType());
        assertEquals(createdEvent.getStatus(), testEvent.getStatus());
    }

    @Test
    public void getAvailableEvents_validUser() {
        Event createdEvent = eventService.createEvent(testEvent);

        List<Event> eventList = new ArrayList<>();
        eventList.add(createdEvent);

        //given
        Mockito.when(eventRepository.findByType(EventType.PUBLIC)).thenReturn(eventList);

        List<Event> testEventList = eventService.getAvailableEvents(testUser.getToken());

        assertEquals(1, testEventList.size());
        assertEquals(testEvent.getId(), testEventList.get(0).getId());
        assertEquals(testEvent.getTitle(), testEventList.get(0).getTitle());
        assertEquals(testEvent.getStatus(), testEventList.get(0).getStatus());
        assertEquals(testEvent.getType(), testEventList.get(0).getType());
    }

    @Test
    public void createEventUser_validInput(){
        User user = new User();
        user.setId(2L);
        user.setUsername("testUser");
        user.setToken("1");

        EventUser eventUser = new EventUser();
        eventUser.setEvent(testEvent);
        eventUser.setUser(user);
        eventUser.setEventUserId(3L);
        eventUser.setRole(EventUserRole.ADMIN);
        eventUser.setStatus(EventUserStatus.CONFIRMED);

        //given
        Mockito.when(eventUserService.createEventUser(Mockito.any())).thenReturn(eventUser);

        //when
        EventUser actualEventUser = eventService.createEventUser(user,testEvent,EventUserRole.ADMIN);
        assertEquals(actualEventUser.getEvent(), eventUser.getEvent());
        assertEquals(actualEventUser.getUser(), eventUser.getUser());
        assertEquals(actualEventUser.getRole(), eventUser.getRole());
    }

    @Test
    public void stringToDate_validInput(){
        Date actualDate = eventService.stringToDate("2020-02-02");
        Date expectedDate = new Date(120, Calendar.FEBRUARY,2);

        assertEquals(actualDate,expectedDate);
    }

    @Test
    public void stringToDate_invalidInput(){
        assertThrows(ResponseStatusException.class, () -> eventService.stringToDate("2020/02/02"));
    }

    @Test
    public void getQueryEventsUserRole_validInputs(){
        User user = new User();
        user.setId(2L);
        user.setUsername("testUser");
        user.setToken("1");

        EventUser eventUser = new EventUser();
        eventUser.setEvent(testEvent);
        eventUser.setUser(user);
        eventUser.setEventUserId(3L);
        eventUser.setRole(EventUserRole.ADMIN);
        eventUser.setStatus(EventUserStatus.CONFIRMED);

        testEvent.addEventUsers(eventUser);
        Mockito.when(userService.parseBearerToken("Bearer 1")).thenReturn(user.getToken());
        Mockito.when(userService.getUserByToken(user.getToken())).thenReturn(user);

        List<Event> eventList = new ArrayList<>();
        eventList.add(testEvent);

        List<Event> queryEventsUserRoleList = eventService.getQueryEventsUserRole(eventList,"Bearer 1", EventUserRole.ADMIN);
        assertEquals(queryEventsUserRoleList,eventList);

    }

    @Test
    public void getUsers_validInput(){
        User user = new User();
        user.setId(2L);
        user.setName("testUser");
        user.setUsername("testUser");
        user.setToken("1");

        EventUser eventUser = new EventUser();
        eventUser.setEvent(testEvent);
        eventUser.setUser(user);
        eventUser.setEventUserId(3L);
        eventUser.setRole(EventUserRole.ADMIN);
        eventUser.setStatus(EventUserStatus.CONFIRMED);

        testEvent.addEventUsers(eventUser);

        List<User> testList = eventService.getUsers(testEvent);

        assertEquals(testList.size(), 1);
        assertEquals(testList.get(0).getName(),user.getName());
        assertEquals(testList.get(0).getUsername(), user.getUsername());
        assertEquals(testList.get(0).getToken(), user.getToken());
    }

    @Test
    public void testupdateEvent_validInput(){ //do I have to do this, since I don't check anything
        User user = new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setPassword("password");
        user.setId(2L);
        user.setToken("1");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("We Events");
        event.setType(EventType.PUBLIC);
        event.setStatus(EventStatus.IN_PLANNING);
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        event.setEventLocation(eventLocation);

        EventUser eventUser = new EventUser();
        eventUser.setEventUserId(3L);
        eventUser.setEvent(event);
        eventUser.setUser(user);
        eventUser.setStatus(EventUserStatus.CONFIRMED);
        eventUser.setRole(EventUserRole.ADMIN);
        event.addEventUsers(eventUser);

        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(event);

        eventService.updateEvent(event,user,event);
    }

}