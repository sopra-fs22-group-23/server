package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;
import ch.uzh.ifi.sopra22.entity.*;
import ch.uzh.ifi.sopra22.repository.EventRepository;
import ch.uzh.ifi.sopra22.repository.EventUserRepository;
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

class EventUserServiceTest {
    @Mock
    private EventUserRepository eventUserRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private EventUserService eventUserService;

    @Mock
    private EventService eventService;

    private Event testEvent;

    private User testUser;

    private EventUser testEventUser;

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

        //given
        testEventUser = new EventUser();
        testEventUser.setEventUserId(4L);
        testEventUser.setUser(testUser);
        testEventUser.setEvent(testEvent);
        testEventUser.setRole(EventUserRole.ADMIN);
        testEventUser.setStatus(EventUserStatus.CONFIRMED);



        Mockito.when(eventUserRepository.save(Mockito.any())).thenReturn(testEventUser);
    }

    @Test
    public void canUserAccessEvent_success() {
        // given
        List<EventUser> eventUsers = new ArrayList<>();
        eventUsers.add(testEventUser);

        // when
        Mockito.when(eventUserRepository.findByUserId(Mockito.any())).thenReturn(eventUsers);

        // then
        assertTrue(eventUserService.canUserAccessEvent(testUser, testEvent.getId(), testEventUser.getRole()));
    }

    @Test
    public void getEventUsers_success() {
        // given
        List<EventUser> eventUsers = new ArrayList<>();
        eventUsers.add(testEventUser);

        // when
        Mockito.when(eventUserRepository.findByUserId(Mockito.any())).thenReturn(eventUsers);

        // then
        List<EventUser> results = eventUserService.getEventUsers(testUser);
        assertEquals(testEventUser.getEventUserId(), results.get(0).getEventUserId());
        assertEquals(testEventUser.getUser().getId(), results.get(0).getUser().getId());
        assertEquals(testEventUser.getEvent().getId(), results.get(0).getEvent().getId());
        assertEquals(testEventUser.getStatus(), results.get(0).getStatus());
        assertEquals(testEventUser.getRole(), results.get(0).getRole());
    }

    @Test
    public void ensureEventUserExists_success() {
        // given
        List<EventUser> eventUsers = new ArrayList<>();
        eventUsers.add(testEventUser);

        // when
        Mockito.when(eventUserRepository.findByUserId(Mockito.any())).thenReturn(eventUsers);

        // then
        EventUser result = eventUserService.ensureEventUserExists(testEvent.getId(), testUser.getId());
        assertEquals(testEventUser.getEventUserId(), result.getEventUserId());
        assertEquals(testEventUser.getUser().getId(), result.getUser().getId());
        assertEquals(testEventUser.getEvent().getId(), result.getEvent().getId());
        assertEquals(testEventUser.getStatus(), result.getStatus());
        assertEquals(testEventUser.getRole(), result.getRole());
    }

    @Test
    public void createEventUser_validInput_success() {
        //given
        EventUser createdEventUser = eventUserService.createEventUser(testEventUser);

        // then
        Mockito.verify(eventUserRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(createdEventUser.getEventUserId(), testEventUser.getEventUserId());
        assertEquals(createdEventUser.getUser(), testEventUser.getUser());
        assertEquals(createdEventUser.getEvent(), testEventUser.getEvent());
        assertEquals(createdEventUser.getRole(), testEventUser.getRole());
        assertEquals(createdEventUser.getStatus(), testEventUser.getStatus());
    }

    @Test
    public void getEventIdsFromToken_validInput_success() {
        //given
        List<EventUser> eventUserList = new ArrayList<>();
        eventUserList.add(testEventUser);
        Mockito.when(eventUserRepository.findAll()).thenReturn(eventUserList);
        Mockito.when(userService.getUserByToken(Mockito.any())).thenReturn(testUser);

        //when
        List<Long> eventIds = eventUserService.getEventIdsFromToken(testUser.getToken());


        //then
        assertEquals(eventIds.get(0), testEvent.getId());
    }

    @Test
    public void getUserTasks_validInput(){
        List<EventTask> taskList = new ArrayList<>();
        EventTask task1 = new EventTask();
        task1.setDescription("Test Task");
        task1.setUser(testUser);
        task1.setEvent(testEvent);
        task1.setId(5L);
        taskList.add(task1);

        EventTask task2 = new EventTask();
        task2.setDescription("Task 2");
        task2.setEvent(testEvent);
        task2.setId(6L);
        taskList.add(task2);

        List<EventTask> actualTaskList = eventUserService.getUserTasks(taskList,2L);

        assertEquals(1,actualTaskList.size());
        assertEquals(task1.getId(),actualTaskList.get(0).getId());
        assertEquals(task1.getUser().getId(),actualTaskList.get(0).getUser().getId());
        assertEquals(task1.getEvent().getId(),actualTaskList.get(0).getEvent().getId());
        assertEquals(task1.getDescription(),actualTaskList.get(0).getDescription());
    }

}