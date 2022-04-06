package ch.uzh.ifi.sopra22.controller;

import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventLocation;
import ch.uzh.ifi.sopra22.service.EventService;
import ch.uzh.ifi.sopra22.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.List;


import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
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

}