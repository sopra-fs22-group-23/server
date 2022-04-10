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
import ch.uzh.ifi.sopra22.service.EventService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
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
                .andExpect(status().isNoContent());
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
        event.setType(EventType.PUBLIC);
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
        MockHttpServletRequestBuilder getRequest = get("/events/1/users")
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