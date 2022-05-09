package ch.uzh.ifi.sopra22.controller;

import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventLocation;
import ch.uzh.ifi.sopra22.entity.EventTask;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.rest.dto.EventTaskPostDTO;
import ch.uzh.ifi.sopra22.service.EventService;
import ch.uzh.ifi.sopra22.service.EventUserService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventTaskController.class)
class EventTaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

   @MockBean
   private EventUserService eventUserService;

   @MockBean
    private EventService eventService;

   @MockBean
   private UserService userService;

   @Test
    public void getAllTasksForAnEvent_validAccess() throws Exception {
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

       User user = new User();
       user.setName("Firstname Lastname");
       user.setUsername("firstname@lastname");
       user.setPassword("password");
       user.setId(2L);
       user.setToken("1");

       EventTask task = new EventTask();
       task.setEvent(event);
       task.setUser(user);
       task.setDescription("Test Task");
       task.setId(3L);

       List<EventTask> allTasks = Collections.singletonList(task);

       given(eventService.validateToken(Mockito.any())).willReturn(user);
       given(eventUserService.canUserAccessEvent(Mockito.any(),Mockito.any(),Mockito.any())).willReturn(true);
       given(eventService.getTasksByEventID(Mockito.any())).willReturn(allTasks);

       // when
       MockHttpServletRequestBuilder getRequest = get("/events/1/tasks")
               .contentType(MediaType.APPLICATION_JSON)
               .header("Authorization",user.getToken());

       mockMvc.perform(getRequest).andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].id", is(task.getId().intValue())))
               .andExpect(jsonPath("$[0].userID", is(user.getId().intValue())))
               .andExpect(jsonPath("$[0].eventID", is(event.getId().intValue())))
               .andExpect(jsonPath("$[0].description", is(task.getDescription())));
   }

   @Test
   public void createTask_validInput() throws Exception {
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

      User user = new User();
      user.setName("Firstname Lastname");
      user.setUsername("firstname@lastname");
      user.setPassword("password");
      user.setId(2L);
      user.setToken("1");

      EventTask task = new EventTask();
      task.setEvent(event);
      task.setUser(user);
      task.setDescription("Test Task");
      task.setId(3L);

      EventTaskPostDTO eventTaskPostDTO = new EventTaskPostDTO();
      eventTaskPostDTO.setUserID(2L);
      eventTaskPostDTO.setDescription("Test Task");

      List<EventTask> allTasks = Collections.singletonList(task);

      given(eventService.validateToken(Mockito.any())).willReturn(user);
      given(eventUserService.canUserAccessEvent(Mockito.any(),Mockito.any(),Mockito.any())).willReturn(true);
      given(eventService.getTasksByEventID(Mockito.any())).willReturn(allTasks);

      //when
      MockHttpServletRequestBuilder postRequest = post("/events/1/tasks")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Authorization",user.getToken())
              .content(asJsonString(eventTaskPostDTO));

      //then
      mockMvc.perform(postRequest).andExpect(status().isCreated())
              .andExpect(jsonPath("$", hasSize(1)))
              .andExpect(jsonPath("$[0].id", is(task.getId().intValue())))
              .andExpect(jsonPath("$[0].userID", is(user.getId().intValue())))
              .andExpect(jsonPath("$[0].eventID", is(event.getId().intValue())))
              .andExpect(jsonPath("$[0].description", is(task.getDescription())));
   }

   @Test
   public void updateSpecificTask_validInput() throws Exception {
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

      User user = new User();
      user.setName("Firstname Lastname");
      user.setUsername("firstname@lastname");
      user.setPassword("password");
      user.setId(2L);
      user.setToken("1");

      EventTask task = new EventTask();
      task.setEvent(event);
      task.setUser(user);
      task.setDescription("Test Task");
      task.setId(3L);

      EventTaskPostDTO eventTaskPostDTO = new EventTaskPostDTO();
      eventTaskPostDTO.setUserID(2L);
      eventTaskPostDTO.setDescription("Test Task");

      List<EventTask> allTasks = Collections.singletonList(task);

      given(eventService.validateToken(Mockito.any())).willReturn(user);
      given(eventUserService.canUserAccessEvent(Mockito.any(),Mockito.any(),Mockito.any())).willReturn(true);
      given(eventService.getTasksByEventID(Mockito.any())).willReturn(allTasks);

      //when
      MockHttpServletRequestBuilder putRequest = MockMvcRequestBuilders.put("/events/1/tasks/3")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Authorization",user.getToken())
              .content(asJsonString(eventTaskPostDTO));

      //then
      mockMvc.perform(putRequest).andExpect(status().isCreated())
              .andExpect(jsonPath("$", hasSize(1)))
              .andExpect(jsonPath("$[0].id", is(task.getId().intValue())))
              .andExpect(jsonPath("$[0].userID", is(user.getId().intValue())))
              .andExpect(jsonPath("$[0].eventID", is(event.getId().intValue())))
              .andExpect(jsonPath("$[0].description", is(task.getDescription())));
   }

   @Test
   public void getAllTasksForAnEventFromAUser_validAccess() throws Exception {
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

      User user = new User();
      user.setName("Firstname Lastname");
      user.setUsername("firstname@lastname");
      user.setPassword("password");
      user.setId(2L);
      user.setToken("1");

      EventTask task = new EventTask();
      task.setEvent(event);
      task.setUser(user);
      task.setDescription("Test Task");
      task.setId(3L);

      List<EventTask> allTasks = Collections.singletonList(task);

      given(eventService.validateToken(Mockito.any())).willReturn(user);
      given(eventService.getTasksByEventID(Mockito.any())).willReturn(allTasks);
      given(eventUserService.getUserTasks(Mockito.any(),Mockito.any())).willReturn(allTasks);

      // when
      MockHttpServletRequestBuilder getRequest = get("/events/1/users/2/tasks")
              .contentType(MediaType.APPLICATION_JSON)
              .header("Authorization",user.getToken());

      mockMvc.perform(getRequest).andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(1)))
              .andExpect(jsonPath("$[0].id", is(task.getId().intValue())))
              .andExpect(jsonPath("$[0].userID", is(user.getId().intValue())))
              .andExpect(jsonPath("$[0].eventID", is(event.getId().intValue())))
              .andExpect(jsonPath("$[0].description", is(task.getDescription())));
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