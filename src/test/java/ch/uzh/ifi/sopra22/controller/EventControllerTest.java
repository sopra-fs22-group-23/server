package ch.uzh.ifi.sopra22.controller;

import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventLocation;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.rest.dto.EventPostDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserEventGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserPostDTO;
import ch.uzh.ifi.sopra22.service.EventService;
import ch.uzh.ifi.sopra22.service.FileService;
import ch.uzh.ifi.sopra22.service.MailService;
import ch.uzh.ifi.sopra22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;


import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private EventService eventService;

    @MockBean
    private FileService fileService;

    @MockBean
    private MailService mailService;

    @Test
    void givenEvent_getAvailableEvents() throws Exception {
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

        List<Event> allEvents = Collections.singletonList(event);

        given(eventService.sortEventsBySearch(Mockito.any(), Mockito.any())).willReturn(allEvents);
        given(eventService.getAvailableEvents("1")).willReturn(allEvents);
        given(eventService.getQueryEventsUserRole(allEvents,"1", EventUserRole.ADMIN)).willReturn(allEvents);
        given(eventService.stringToDate("1999-01-01")).willReturn(null);

        MockHttpServletRequestBuilder getRequest = get("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","1");

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(event.getId().intValue())))
                .andExpect(jsonPath("$[0].title", is(event.getTitle())))
                .andExpect(jsonPath("$[0].type", is(event.getType().toString())))
                .andExpect(jsonPath("$[0].status", is(event.getStatus().toString())))
                .andExpect(jsonPath("$[0].locationName", is(event.getEventLocation().getName())));
    }
    @Test
    void givenEvent_getAvailableEvents_withLocationParam() throws Exception {
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

        List<Event> allEvents = Collections.singletonList(event);

        given(eventService.sortEventsBySearch(Mockito.any(), Mockito.any())).willReturn(allEvents);
        given(eventService.getAvailableEvents("1")).willReturn(allEvents);
        given(eventService.getQueryEventsUserRole(allEvents,"1", EventUserRole.ADMIN)).willReturn(allEvents);
        given(eventService.stringToDate("1999-01-01")).willReturn(null);

        MockHttpServletRequestBuilder getRequest = get("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","1")
                .param("location",eventLocation.getName());

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(event.getId().intValue())))
                .andExpect(jsonPath("$[0].title", is(event.getTitle())))
                .andExpect(jsonPath("$[0].type", is(event.getType().toString())))
                .andExpect(jsonPath("$[0].status", is(event.getStatus().toString())))
                .andExpect(jsonPath("$[0].locationName", is(event.getEventLocation().getName())));
    }
    @Test
    public void createEvent_validInput() throws Exception {
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

        EventPostDTO eventPostDTO = new EventPostDTO();
        eventPostDTO.setTitle("We Events");
        eventPostDTO.setType(EventType.PUBLIC);
        eventPostDTO.setLocationName("Zurich");

        User user = new User();
        user.setId(2L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setToken("1");

        EventUser eventUser = new EventUser();
        eventUser.setEventUserId(3L);
        eventUser.setEvent(event);
        eventUser.setUser(user);
        eventUser.setRole(EventUserRole.ADMIN);

        given(eventService.validateToken(Mockito.any())).willReturn(user);
        given(eventService.createEvent(Mockito.any())).willReturn(event);
        given(eventService.createEventUser(Mockito.any(),Mockito.any(),Mockito.any())).willReturn(eventUser);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(eventPostDTO))
                .header("Authorization",user.getToken());

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(event.getId().intValue())))
                .andExpect(jsonPath("$.title", is(event.getTitle())))
                .andExpect(jsonPath("$.type", is(event.getType().toString())))
                .andExpect(jsonPath("$.locationName", is(event.getEventLocation().getName())));

    }

    @Test
    public void getSpecificEvent_validInput() throws Exception {
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

        given(eventService.getEventByIDNum(Mockito.any())).willReturn(event);

        MockHttpServletRequestBuilder getRequest = get("/events/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","1");

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(event.getId().intValue())))
                .andExpect(jsonPath("$.title", is(event.getTitle())))
                .andExpect(jsonPath("$.type", is(event.getType().toString())))
                .andExpect(jsonPath("$.status", is(event.getStatus().toString())))
                .andExpect(jsonPath("$.locationName", is(event.getEventLocation().getName())));
    }

    @Test
    public void updateSpecificEvent_valid() throws Exception {
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

        EventPostDTO eventPostDTO = new EventPostDTO();
        eventPostDTO.setTitle("We Events");
        eventPostDTO.setLocationName("Zurich");

        given(eventService.validateToken(Mockito.any())).willReturn(user);
        given(eventService.getEventByIDNum(Mockito.any())).willReturn(event);

        MockHttpServletRequestBuilder putRequest = put("/events/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(eventPostDTO))
                .header("Authorization",user.getToken());

        mockMvc.perform(putRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(event.getId().intValue())))
                .andExpect(jsonPath("$.title", is(event.getTitle())))
                .andExpect(jsonPath("$.type", is(event.getType().toString())))
                .andExpect(jsonPath("$.status", is(event.getStatus().toString())))
                .andExpect(jsonPath("$.locationName", is(event.getEventLocation().getName())));
    }

    @Test
    public void getAllUsers_validInput() throws Exception {
        User user = new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setPassword("password");
        user.setId(2L);
        user.setToken("1");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("We Events");
        event.setType(EventType.PRIVATE);
        event.setStatus(EventStatus.IN_PLANNING);
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        event.setEventLocation(eventLocation);

        EventUser eventUser = new EventUser();
        eventUser.setEvent(event);
        eventUser.setUser(user);
        eventUser.setEventUserId(3L);
        eventUser.setRole(EventUserRole.GUEST);
        eventUser.setStatus(EventUserStatus.CONFIRMED);

        List<EventUser> allEventUsers = Collections.singletonList(eventUser);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(eventService.getEventByIDNum(Mockito.any())).willReturn(event);
        given(eventService.getEventUsers(Mockito.any())).willReturn(allEventUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/events/1/users?eventUserStatus=CONFIRMED")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization",user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].birthday", is(user.getBirthday())));
    }

    @Test
    public void addUserToEvent_validInput_UserExistent() throws Exception {
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
        eventUser.setRole(EventUserRole.COLLABORATOR);

        given(eventService.getEventByIDNum(Mockito.any())).willReturn(event);
        given(userService.getUserByPartialUser(Mockito.any())).willReturn(user);
        given(eventService.validEventUserPOST(Mockito.any(),Mockito.any(),Mockito.any(), Mockito.any())).willReturn(eventUser);

        // when
        MockHttpServletRequestBuilder postRequest = post("/events/1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization",user.getToken());

        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.birthday", is(user.getBirthday())));

    }

    @Test
    public void updateEventUser_validInput_success() throws Exception {
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
        eventUser.setRole(EventUserRole.GUEST);

        given(eventService.getEventByIDNum(Mockito.any())).willReturn(event);
        given(eventService.validEventUserPUT(Mockito.any(),Mockito.any(),Mockito.any(), Mockito.any())).willReturn(eventUser);

        // when
        MockHttpServletRequestBuilder putRequest = put("/events/1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization",user.getToken());

        // then
        mockMvc.perform(putRequest).andExpect(status().isNoContent());
    }

    @Test
    public void uploadfile_validInput() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setToken("1");

        Event event = new Event();
        event.setId(2L);
        event.setTitle("Test Event");
        event.setType(EventType.PUBLIC);
        event.setStatus(EventStatus.IN_PLANNING);
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        event.setEventLocation(eventLocation);


        given(eventService.validateToken(Mockito.any())).willReturn(user);
        given(eventService.getEventByIDNum(Mockito.any())).willReturn(event);
        given(fileService.createNameWithTimestampAndID(Mockito.any(),Mockito.any())).willReturn("test_1.json");
        //Mock Request
        MockMultipartFile jsonFile = new MockMultipartFile("test_1.json", "test", "application/json", "{\"key1\": \"value1\"}".getBytes());

        MockHttpServletRequestBuilder postRequest = MockMvcRequestBuilders.multipart("/events/2/image")
                .file("file",jsonFile.getBytes())
                .header("Authorization",user.getToken());

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated());

    }
    @Test
    public void downloadEvent_validInput() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setToken("1");

        Event event = new Event();
        event.setId(2L);
        event.setTitle("Test Event");
        event.setType(EventType.PUBLIC);
        event.setStatus(EventStatus.IN_PLANNING);
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        event.setEventLocation(eventLocation);

        MockMultipartFile jsonFile = new MockMultipartFile("test.json", "", "application/json", "{\"key1\": \"value1\"}".getBytes());

        given(eventService.getEventByIDNum(Mockito.any())).willReturn(event);
        given(fileService.load(Mockito.any())).willReturn(null);

        MockHttpServletRequestBuilder getRequest = get("/events/2/image")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization",user.getToken());

        mockMvc.perform(getRequest)
                .andExpect(status().isBadRequest());
    }
    @Test
    public void getAllUserEvents_sameUser_validInput() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setToken("1");

        UserEventGetDTO userEventGetDTO = new UserEventGetDTO();
        userEventGetDTO.setId(2L);
        userEventGetDTO.setTitle("test Event");
        userEventGetDTO.setType(EventType.PUBLIC);
        userEventGetDTO.setDescription("test event");
        userEventGetDTO.setStatus(EventStatus.READY);
        userEventGetDTO.setEventUserStatus(EventUserStatus.CONFIRMED);
        userEventGetDTO.setEventUserRole(EventUserRole.ADMIN);

        List<UserEventGetDTO> userEventGetDTOList = Collections.singletonList(userEventGetDTO);

        given(eventService.validateToken(Mockito.any())).willReturn(user);
        given(userService.getUserByIDNum(Mockito.any())).willReturn(user);
        given(eventService.generateUserEvents(Mockito.any())).willReturn(userEventGetDTOList);

        MockHttpServletRequestBuilder getRequest = get("/users/1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", user.getToken());
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userEventGetDTO.getId().intValue())))
                .andExpect(jsonPath("$[0].title", is(userEventGetDTO.getTitle())))
                .andExpect(jsonPath("$[0].type", is(userEventGetDTO.getType().toString())))
                .andExpect(jsonPath("$[0].description", is(userEventGetDTO.getDescription())))
                .andExpect(jsonPath("$[0].status", is(userEventGetDTO.getStatus().toString())))
                .andExpect(jsonPath("$[0].eventUserStatus", is(userEventGetDTO.getEventUserStatus().toString())))
                .andExpect(jsonPath("$[0].eventUserRole", is(userEventGetDTO.getEventUserRole().toString())));
    }

    @Test
    public void getAllUserEvents_differentUser_validInput() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Test User");
        user1.setUsername("testUsername");
        user1.setPassword("password");
        user1.setToken("1");

        User user2 = new User();
        user2.setId(3L);
        user2.setName("Test User2");
        user2.setUsername("testUsername2");
        user2.setPassword("password2");
        user2.setToken("3");

        UserEventGetDTO userEventGetDTO = new UserEventGetDTO();
        userEventGetDTO.setId(2L);
        userEventGetDTO.setTitle("test Event");
        userEventGetDTO.setType(EventType.PUBLIC);
        userEventGetDTO.setDescription("test event");
        userEventGetDTO.setStatus(EventStatus.READY);
        userEventGetDTO.setEventUserStatus(EventUserStatus.CONFIRMED);
        userEventGetDTO.setEventUserRole(EventUserRole.ADMIN);

        List<UserEventGetDTO> userEventGetDTOList = Collections.singletonList(userEventGetDTO);

        given(eventService.validateToken(Mockito.any())).willReturn(user1);
        given(userService.getUserByIDNum(Mockito.any())).willReturn(user2);
        given(eventService.generateUserEvents(Mockito.any())).willReturn(userEventGetDTOList);

        MockHttpServletRequestBuilder getRequest = get("/users/1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", user1.getToken());
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userEventGetDTO.getId().intValue())))
                .andExpect(jsonPath("$[0].title", is(userEventGetDTO.getTitle())))
                .andExpect(jsonPath("$[0].type", is(userEventGetDTO.getType().toString())))
                .andExpect(jsonPath("$[0].description", is(userEventGetDTO.getDescription())))
                .andExpect(jsonPath("$[0].status", is(userEventGetDTO.getStatus().toString())))
                .andExpect(jsonPath("$[0].eventUserStatus", is(userEventGetDTO.getEventUserStatus().toString())))
                .andExpect(jsonPath("$[0].eventUserRole", is(userEventGetDTO.getEventUserRole().toString())));
    }

    @Test
    public void emailNotificationTest() throws Exception {
        Event event = new Event();
        event.setId(2L);
        event.setTitle("Test Event");
        event.setType(EventType.PUBLIC);
        event.setStatus(EventStatus.IN_PLANNING);
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        event.setEventLocation(eventLocation);

        given(eventService.getEventByIDNum(Mockito.any())).willReturn(event);
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setEmail("test@gmail.com");

        MockHttpServletRequestBuilder postRequest = post("/emailNotification/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated());
    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }

}