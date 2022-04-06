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

    @InjectMocks
    private EventService eventService;

    private Event testEvent;

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
        // testUser
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(testEvent);
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

}