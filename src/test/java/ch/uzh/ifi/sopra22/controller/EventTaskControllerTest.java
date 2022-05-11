package ch.uzh.ifi.sopra22.controller;

import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;
import ch.uzh.ifi.sopra22.entity.*;
import ch.uzh.ifi.sopra22.rest.dto.EventPostDTO;
import ch.uzh.ifi.sopra22.rest.dto.EventTaskGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.EventTaskPostDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserPostDTO;
import ch.uzh.ifi.sopra22.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventTaskController.class)
class EventTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private EventService eventService;

    @MockBean
    private EventUserService eventUserService;

    @MockBean
    private FileService fileService;

    @MockBean
    private MailService mailService;

    private Event testEvent;
    private User testUser;
    private List<EventTask> testTasks;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setName("Firstname Lastname");
        testUser.setUsername("firstname@lastname");
        testUser.setPassword("password");
        testUser.setId(2L);
        testUser.setToken("1");

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

        testTasks = new ArrayList<>();
        EventTask task = new EventTask();
        task.setEvent(testEvent);
        task.setId(5L);
        task.setUser(testUser);
        task.setDescription("task1");
        testTasks.add(task);
        task = new EventTask();
        task.setEvent(testEvent);
        task.setId(6L);
        task.setUser(testUser);
        task.setDescription("task2");
        testTasks.add(task);
    }

    @Test
    void getEventTasks_success() throws Exception {
        given(eventService.validateToken(Mockito.any())).willReturn(testUser);
        given(eventService.getTasksByEventID(Mockito.any())).willReturn(testTasks);
        given(eventUserService.canUserAccessEvent(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        MockHttpServletRequestBuilder getRequest = get("/events/1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization","1");

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(testTasks.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].userID", is(testTasks.get(0).getUser().getId().intValue())))
                .andExpect(jsonPath("$[0].eventID", is(testTasks.get(0).getEvent().getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(testTasks.get(0).getDescription())));
    }
    @Test
    void postEventTask_success() throws Exception {
        given(eventService.validateToken(Mockito.any())).willReturn(testUser);
        given(eventService.getTasksByEventID(Mockito.any())).willReturn(testTasks);
        doNothing().when(eventService).addTask(Mockito.any(), Mockito.any());
        given(eventUserService.canUserAccessEvent(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        EventTaskPostDTO eventTaskPostDTO = new EventTaskPostDTO();
        eventTaskPostDTO.setUserID(testUser.getId());
        eventTaskPostDTO.setDescription("task2");

        MockHttpServletRequestBuilder postRequest = post("/events/1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(eventTaskPostDTO))
                .header("Authorization","1");

        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(testTasks.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].userID", is(testTasks.get(0).getUser().getId().intValue())))
                .andExpect(jsonPath("$[0].eventID", is(testTasks.get(0).getEvent().getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(testTasks.get(0).getDescription())));
    }
    @Test
    void putEventTask_success() throws Exception {
        given(eventService.validateToken(Mockito.any())).willReturn(testUser);
        given(eventService.getTasksByEventID(Mockito.any())).willReturn(testTasks);
        doNothing().when(eventService).updateTask(Mockito.any(), Mockito.any());
        given(eventUserService.canUserAccessEvent(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(true);

        EventTaskPostDTO eventTaskPostDTO = new EventTaskPostDTO();
        eventTaskPostDTO.setUserID(testUser.getId());
        eventTaskPostDTO.setDescription("task2");

        MockHttpServletRequestBuilder putRequest = put("/events/1/tasks/6")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(eventTaskPostDTO))
                .header("Authorization","1");

        mockMvc.perform(putRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(testTasks.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].userID", is(testTasks.get(0).getUser().getId().intValue())))
                .andExpect(jsonPath("$[0].eventID", is(testTasks.get(0).getEvent().getId().intValue())))
                .andExpect(jsonPath("$[0].description", is(testTasks.get(0).getDescription())));
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