package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.Event.GameMode;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;
import ch.uzh.ifi.sopra22.entity.*;
import ch.uzh.ifi.sopra22.repository.EventRepository;
import java.util.Optional;

import ch.uzh.ifi.sopra22.repository.EventTaskRepository;
import ch.uzh.ifi.sopra22.rest.dto.EventTaskPostDTO;
import ch.uzh.ifi.sopra22.rest.dto.EventUserPostDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserEventGetDTO;
import ch.uzh.ifi.sopra22.rest.mapper.EventDTOMapper;
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
import static org.mockito.Mockito.doNothing;

class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventTaskRepository eventTaskRepository;

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
        testEvent.setEventDate(new Date(new Date().getTime() +(1000*60*60*24)));
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        testEvent.setEventLocation(eventLocation);
        testEvent.setGameMode(GameMode.OFF);

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
    public void linkImageToEvent_success() {
        // given

        // when
        Mockito.when(eventRepository.save(Mockito.any())).thenReturn(testEvent);
        doNothing().when(eventRepository).flush();

        // then
        eventService.linkImageToEvent(testEvent, "randomName");
    }

    @Test
    public void isUserAloudToUpdate_success() {
        // given
        EventUser eventUser = new EventUser();
        eventUser.setEvent(testEvent);
        eventUser.setUser(testUser);
        eventUser.setEventUserId(5L);
        eventUser.setStatus(EventUserStatus.CONFIRMED);
        eventUser.setRole(EventUserRole.ADMIN);

        List<EventUser> eventUserList = new ArrayList<>();
        eventUserList.add(eventUser);

        // when
        testEvent.setEventUsers(eventUserList);

        // then
        eventService.isUserAloudToUpdate(testEvent, testUser);
    }

    @Test
    public void getEventUsers_success() {
        // given
        EventUser eventUser = new EventUser();
        eventUser.setEvent(testEvent);
        eventUser.setUser(testUser);
        eventUser.setEventUserId(5L);
        eventUser.setStatus(EventUserStatus.CONFIRMED);
        eventUser.setRole(EventUserRole.ADMIN);

        List<EventUser> eventUserList = new ArrayList<>();
        eventUserList.add(eventUser);

        // when
        testEvent.setEventUsers(eventUserList);

        // then
        List<EventUser> results = eventService.getEventUsers(testEvent);
        assertEquals(eventUser.getRole(), results.get(0).getRole());
        assertEquals(eventUser.getStatus(), results.get(0).getStatus());
        assertEquals(eventUser.getEvent().getId(), results.get(0).getEvent().getId());
        assertEquals(eventUser.getUser().getId(), results.get(0).getUser().getId());
        assertEquals(eventUser.getEventUserId(), results.get(0).getEventUserId());
    }

    @Test
    public void generateUserEvents_success() {
        // given
        UserEventGetDTO userEventGetDTO = EventDTOMapper.INSTANCE.convertEntityToUserEventGetDTO(testEvent);
        userEventGetDTO.setEventUserRole(EventUserRole.GUEST);
        userEventGetDTO.setEventUserStatus(EventUserStatus.CONFIRMED);

        EventUser eventUser = new EventUser();
        eventUser.setEventUserId(5L);
        eventUser.setEvent(testEvent);
        eventUser.setUser(testUser);
        eventUser.setRole(EventUserRole.GUEST);
        eventUser.setStatus(EventUserStatus.CONFIRMED);
        List<EventUser> eventUsers = new ArrayList<>();
        eventUsers.add(eventUser);

        // when
        Mockito.when(eventUserService.getEventUsers(Mockito.any())).thenReturn(eventUsers);

        // then
        List<UserEventGetDTO> results = eventService.generateUserEvents(testUser);
        assertEquals(userEventGetDTO.getEventUserRole(), results.get(0).getEventUserRole());
        assertEquals(userEventGetDTO.getEventUserStatus(), results.get(0).getEventUserStatus());
        assertEquals(userEventGetDTO.getTitle(), results.get(0).getTitle());
        assertEquals(userEventGetDTO.getType(), results.get(0).getType());
    }

    @Test
    public void getTasksByEventID_success() {
        // given
        EventTask task = new EventTask();
        task.setEvent(testEvent);
        task.setUser(testUser);
        task.setDescription("task1");
        List<EventTask> taskList = new ArrayList<>();
        taskList.add(task);

        // when
        Mockito.when(eventTaskRepository.findAllByEvent_id(Mockito.any())).thenReturn(taskList);

        // then
        List<EventTask> results = eventService.getTasksByEventID(testEvent.getId());
        assertEquals(task.getEvent().getId(), results.get(0).getEvent().getId());
        assertEquals(task.getUser().getId(), results.get(0).getUser().getId());
        assertEquals(task.getDescription(), results.get(0).getDescription());
    }

    @Test
    public void addTask_success() {
        // given
        EventTask task = new EventTask();
        task.setEvent(testEvent);
        task.setUser(testUser);
        task.setDescription("task1");
        List<EventTask> taskList = new ArrayList<>();
        taskList.add(task);

        // when
        Mockito.when(eventRepository.existsById(Mockito.any())).thenReturn(true);
        Optional<Event> optionalEvent = Optional.ofNullable(testEvent);
        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(optionalEvent);
        Mockito.when(eventTaskRepository.save(Mockito.any())).thenReturn(task);

        // then
        eventService.addTask(testEvent.getId(), task);
    }

    @Test
    public void updateTask_success() {
        // given
        EventTask task = new EventTask();
        task.setId(5L);
        task.setEvent(testEvent);
        task.setUser(testUser);
        task.setDescription("task1");
        List<EventTask> taskList = new ArrayList<>();
        taskList.add(task);

        EventTaskPostDTO eventTaskPostDTO = new EventTaskPostDTO();
        eventTaskPostDTO.setUserID(testUser.getId());
        eventTaskPostDTO.setDescription(task.getDescription());

        // when
        Mockito.when(eventTaskRepository.getOne(task.getId())).thenReturn(task);
        Mockito.when(userService.getUserByIDNum(Mockito.any())).thenReturn(testUser);

        // then
        eventService.updateTask(task.getId(), eventTaskPostDTO);
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
        assertEquals(createdEvent.getEventLocation().getName(),testEvent.getEventLocation().getName());
        assertEquals(createdEvent.getEventLocation().getLatitude(),testEvent.getEventLocation().getLatitude());
        assertEquals(createdEvent.getEventLocation().getLongitude(),testEvent.getEventLocation().getLongitude());
        assertEquals(createdEvent.getEventDate(),testEvent.getEventDate());
        assertEquals(createdEvent.getGameMode(),testEvent.getGameMode());
    }

    @Test
    public void getAvailableEvents_validUser() {
        Event createdEvent = eventService.createEvent(testEvent);

        Event secondEvent = new Event();
        secondEvent.setId(2L);
        secondEvent.setTitle("We Events");
        secondEvent.setType(EventType.PRIVATE);
        secondEvent.setStatus(EventStatus.IN_PLANNING);
        secondEvent.setEventDate(new Date(new Date().getTime() +(1000*60*60*24)));
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        secondEvent.setEventLocation(eventLocation);

        List<Event> eventList = new ArrayList<>();
        eventList.add(createdEvent);
        eventList.add(secondEvent);
        List<Long> eventIds = new ArrayList<>();
        eventIds.add(createdEvent.getId());
        eventIds.add(secondEvent.getId());

        //given
        Mockito.when(eventRepository.findByType(EventType.PUBLIC)).thenReturn(eventList);
        Mockito.when(eventUserService.getEventIdsFromToken(Mockito.any())).thenReturn(eventIds);
        //Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.of(secondEvent));

        List<Event> testEventList = eventService.getAvailableEvents(testUser.getToken());

        assertEquals(2, testEventList.size());
        assertEquals(testEvent.getId(), testEventList.get(0).getId());
        assertEquals(testEvent.getTitle(), testEventList.get(0).getTitle());
        assertEquals(testEvent.getStatus(), testEventList.get(0).getStatus());
        assertEquals(testEvent.getType(), testEventList.get(0).getType());
        assertEquals(testEvent.getEventDate(),testEventList.get(0).getEventDate());
        assertEquals(testEvent.getEventLocation().getName(),testEventList.get(0).getEventLocation().getName());
        assertEquals(testEvent.getEventLocation().getLongitude(), testEventList.get(0).getEventLocation().getLongitude());
        assertEquals(testEvent.getEventLocation().getLatitude(),testEventList.get(0).getEventLocation().getLatitude());
        assertEquals(testEvent.getGameMode(),testEventList.get(0).getGameMode());
    }

    @Test
    public void getAvailableEventsBySearch_validUser() {
        Event createdEvent = eventService.createEvent(testEvent);

        List<Event> eventList = new ArrayList<>();
        eventList.add(createdEvent);
        String search = "We+Even";

        //given
        Mockito.when(userService.parseString(Mockito.any())).thenReturn("we even");
        List<String> words = new ArrayList<>();
        words.add("we");
        words.add("even");
        Mockito.when(userService.getWordsFromString(Mockito.any())).thenReturn(words);

        List<Event> testEventList = eventService.sortEventsBySearch(eventList, search);

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
        assertEquals(actualEventUser.getStatus(),eventUser.getStatus());
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
        event.setDescription("This is a test event");
        event.setEventDate(new Date(new Date().getTime() + (1000*60*60*24)));
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
    @Test
    public void testupdateEvent_InvalidDate(){ //do I have to do this, since I don't check anything
        User user = new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setPassword("password");
        user.setId(2L);
        user.setToken("1");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("We Events");
        event.setDescription("This is a test event");
        event.setEventDate(new Date(new Date().getTime() - (1000*60*60*24)));
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

        assertThrows(ResponseStatusException.class, () -> eventService.updateEvent(event,user,event));
    }
    @Test
    public void getEventByIDNum_success(){
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

        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(Optional.of(event));

        Event testEvent = eventService.getEventByIDNum(event.getId());

        assertEquals(testEvent.getId(),event.getId());
        assertEquals(testEvent.getEventLocation().getName(),event.getEventLocation().getName());
        assertEquals(testEvent.getEventLocation().getLongitude(),event.getEventLocation().getLongitude());
        assertEquals(testEvent.getEventLocation().getLatitude(),event.getEventLocation().getLatitude());
        assertEquals(testEvent.getEventDate(),event.getEventDate());
        assertEquals(testEvent.getTitle(),event.getTitle());
        assertEquals(testEvent.getType(),event.getType());
        assertEquals(testEvent.getStatus(),event.getStatus());
        assertEquals(testEvent.getDescription(),event.getDescription());
    }
    @Test
    public void test_getEventByIDNum_invalidInput(){
        Mockito.when(eventRepository.findById(Mockito.any())).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> eventService.getEventByIDNum(1L));
    }
    @Test
    public void test_getWordsFromString_success(){
        List<Event> events = new ArrayList<>();
        List<Event> expectedEvents = new ArrayList<>();

        Event event = new Event();
        event.setId(1L);
        event.setTitle("We Events");
        event.setType(EventType.PUBLIC);
        event.setStatus(EventStatus.IN_PLANNING);
        event.setDescription("Wevents");
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        event.setEventLocation(eventLocation);
        events.add(event);
        expectedEvents.add(event);

        Event event2 = new Event();
        event2.setId(1L);
        event2.setTitle("We Events");
        event2.setType(EventType.PUBLIC);
        event2.setStatus(EventStatus.IN_PLANNING);
        event2.setDescription("wevents");
        EventLocation eventLocation2 = new EventLocation();
        eventLocation2.setName("Frankfurt");
        eventLocation2.setLatitude(1.02F);
        eventLocation2.setLongitude(1.02F);
        event2.setEventLocation(eventLocation2);
        events.add(event2);

        String searchTerm = "Zurich";

        Mockito.when(userService.parseString(Mockito.any())).thenReturn("zurich");

        //test
        List<Event> actual = eventService.sortEventsBySearch(events,searchTerm);

        assertEquals(expectedEvents,actual);
    }
    @Test
    public void test_validateToken_validInput(){
        User testUser = new User();
        testUser.setId(1L);
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setToken("1");

        Mockito.when(userService.getUserByToken(Mockito.any())).thenReturn(testUser);

        User actualUser = eventService.validateToken(testUser.getToken());

        assertEquals(actualUser.getId(),testUser.getId());
        assertEquals(actualUser.getToken(),testUser.getToken());
        assertEquals(actualUser.getName(),testUser.getName());
        assertEquals(actualUser.getUsername(), testUser.getUsername());
    }
    @Test
    public void test_validateTokenForEventGET_validInput(){
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

        Mockito.when(userService.getUserByToken(Mockito.any())).thenReturn(user);

        EventUser actualEventUser = eventService.validateTokenForEventGET(event,user.getToken());

        assertEquals(actualEventUser.getStatus(),eventUser.getStatus());
        assertEquals(actualEventUser.getEventUserId(),eventUser.getEventUserId());
        assertEquals(actualEventUser.getEvent().getId(),eventUser.getEvent().getId());
        assertEquals(actualEventUser.getUser().getId(),eventUser.getUser().getId());
        assertEquals(actualEventUser.getRole(),eventUser.getRole());
    }
    @Test
    public void addUserToEventPOST_validInput() {
        //when
        EventUserPostDTO eventUserPostDTO = new EventUserPostDTO();
        eventUserPostDTO.setId(testUser.getId());
        eventUserPostDTO.setEventUserRole(EventUserRole.GUEST);
        eventUserPostDTO.setEventUserStatus(EventUserStatus.CONFIRMED);

        EventUser createdEventUser = new EventUser();
        createdEventUser.setEventUserId(8L);
        createdEventUser.setEvent(testEvent);
        createdEventUser.setUser(testUser);
        createdEventUser.setRole(EventUserRole.GUEST);
        createdEventUser.setStatus(EventUserStatus.CONFIRMED);

        //given
        Mockito.when(userService.getUserByToken(Mockito.any())).thenReturn(testUser);
        Mockito.when(eventUserService.createEventUser(Mockito.any())).thenReturn(createdEventUser);

        EventUser eventUser = eventService.validEventUserPOST(testUser, testEvent, eventUserPostDTO, testUser.getToken());

        //then
        assertEquals(eventUser.getStatus(), EventUserStatus.CONFIRMED);
        assertEquals(eventUser.getEvent().getId(), testEvent.getId());
        assertEquals(eventUser.getUser().getId(), testUser.getId());
        assertEquals(eventUser.getRole(), EventUserRole.GUEST);

    }

    @Test
    public void addUserToEventPOSTDifferentUser_validInput() {
        //when
        EventUserPostDTO eventUserPostDTO = new EventUserPostDTO();
        eventUserPostDTO.setId(testUser.getId());
        eventUserPostDTO.setEventUserStatus(EventUserStatus.CONFIRMED);

        EventUser createdEventUser = new EventUser();
        createdEventUser.setEventUserId(8L);
        createdEventUser.setEvent(testEvent);
        createdEventUser.setUser(testUser);
        createdEventUser.setRole(EventUserRole.GUEST);
        createdEventUser.setStatus(EventUserStatus.CONFIRMED);

        User adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("admin");
        adminUser.setPassword("password");
        adminUser.setName("admin");
        adminUser.setToken("123345");

        EventUser admin = new EventUser();
        admin.setEvent(testEvent);
        admin.setUser(adminUser);
        admin.setEventUserId(7L);
        admin.setRole(EventUserRole.ADMIN);
        admin.setStatus(EventUserStatus.CONFIRMED);

        testEvent.addEventUsers(createdEventUser);
        testEvent.addEventUsers(admin);

        //given
        Mockito.when(userService.getUserByToken(Mockito.any())).thenReturn(adminUser);
        Mockito.when(userService.getUserByIDNum(Mockito.any())).thenReturn(testUser);
        Mockito.when(eventUserService.createEventUser(Mockito.any())).thenReturn(createdEventUser);

        EventUser eventUser = eventService.validEventUserPOST(testUser, testEvent, eventUserPostDTO, adminUser.getToken());

        //then
        assertEquals(eventUser.getStatus(), EventUserStatus.CONFIRMED);
        assertEquals(eventUser.getEvent().getId(), testEvent.getId());
        assertEquals(eventUser.getUser().getId(), testUser.getId());
        assertEquals(eventUser.getRole(), EventUserRole.GUEST);

    }
    @Test
    public void addUserToEventPUT_validInput() {
        //when
        EventUserPostDTO eventUserPostDTO = new EventUserPostDTO();
        eventUserPostDTO.setId(testUser.getId());
        eventUserPostDTO.setEventUserRole(EventUserRole.GUEST);
        eventUserPostDTO.setEventUserStatus(EventUserStatus.CANCELLED);

        EventUser createdEventUser = new EventUser();
        createdEventUser.setEventUserId(8L);
        createdEventUser.setEvent(testEvent);
        createdEventUser.setUser(testUser);
        createdEventUser.setRole(EventUserRole.GUEST);
        createdEventUser.setStatus(EventUserStatus.CONFIRMED);

        EventUser admin = new EventUser();
        admin.setEvent(testEvent);
        admin.setUser(testUser);
        admin.setEventUserId(7L);
        admin.setRole(EventUserRole.ADMIN);
        createdEventUser.setStatus(EventUserStatus.CONFIRMED);

        testEvent.addEventUsers(admin);
        testEvent.addEventUsers(createdEventUser);

        //given
        Mockito.when(userService.getUserByToken(Mockito.any())).thenReturn(testUser);
        Mockito.when(eventUserService.ensureEventUserExists(Mockito.any(), Mockito.any())).thenReturn(createdEventUser);

        EventUser eventUser = eventService.validEventUserPUT(testUser, testEvent, eventUserPostDTO, testUser.getToken());

        //then
        assertEquals(eventUser.getStatus(), EventUserStatus.CANCELLED);
        assertEquals(eventUser.getEvent().getId(), testEvent.getId());
        assertEquals(eventUser.getUser().getId(), testUser.getId());
        assertEquals(eventUser.getRole(), EventUserRole.GUEST);

    }
    @Test
    public void addUserToEventPUTNotSelfChange_validInput() {
        //when
        User adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("admin");
        adminUser.setPassword("password");
        adminUser.setName("admin");
        adminUser.setToken("123345");

        EventUserPostDTO eventUserPostDTO = new EventUserPostDTO();
        eventUserPostDTO.setId(testUser.getId());
        eventUserPostDTO.setEventUserRole(EventUserRole.GUEST);
        eventUserPostDTO.setEventUserStatus(EventUserStatus.CANCELLED);

        EventUser createdEventUser = new EventUser();
        createdEventUser.setEventUserId(8L);
        createdEventUser.setEvent(testEvent);
        createdEventUser.setUser(testUser);
        createdEventUser.setRole(EventUserRole.ADMIN);
        createdEventUser.setStatus(EventUserStatus.CONFIRMED);

        EventUser admin = new EventUser();
        admin.setEvent(testEvent);
        admin.setUser(adminUser);
        admin.setEventUserId(7L);
        admin.setRole(EventUserRole.ADMIN);
        createdEventUser.setStatus(EventUserStatus.CONFIRMED);

        testEvent.addEventUsers(admin);
        testEvent.addEventUsers(createdEventUser);

        //given
        Mockito.when(userService.getUserByToken(Mockito.any())).thenReturn(adminUser);
        Mockito.when(eventUserService.ensureEventUserExists(Mockito.any(), Mockito.any())).thenReturn(admin);

        EventUser eventUser = eventService.validEventUserPUT(testUser, testEvent, eventUserPostDTO, testUser.getToken());

        //then
        assertEquals(eventUser.getStatus(), EventUserStatus.CANCELLED);
        assertEquals(eventUser.getEvent().getId(), testEvent.getId());
        assertEquals(eventUser.getUser().getId(), adminUser.getId());
        assertEquals(eventUser.getRole(), EventUserRole.GUEST);

    }
}