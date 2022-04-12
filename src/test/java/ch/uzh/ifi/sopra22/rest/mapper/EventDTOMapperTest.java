package ch.uzh.ifi.sopra22.rest.mapper;

import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventLocation;
import ch.uzh.ifi.sopra22.entity.EventTask;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.rest.dto.EventGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.EventPostDTO;
import ch.uzh.ifi.sopra22.rest.dto.EventTaskGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.EventTaskPostDTO;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

class EventDTOMapperTest {
    @Test
    public void convertFromEntityToEventGetDTO_success(){
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Event");
        event.setType(EventType.PUBLIC);
        event.setDescription("This is an event");
        event.setEventDate(Calendar.getInstance().getTime());
        event.setStatus(EventStatus.READY);
        EventLocation eventLocation = new EventLocation();
        eventLocation.setLongitude(1F);
        eventLocation.setLongitude(1F);
        eventLocation.setName("Zurich");
        event.setEventLocation(eventLocation);

        //Map
        EventGetDTO eventGetDTO = EventDTOMapper.INSTANCE.convertEntityToEventGetDTO(event);

        //check
        assertEquals(event.getId(), eventGetDTO.getId());
        assertEquals(event.getTitle(), eventGetDTO.getTitle());
        assertEquals(event.getType(), eventGetDTO.getType());
        assertEquals(event.getDescription(), eventGetDTO.getDescription());
        assertEquals(event.getEventDate(), eventGetDTO.getEventDate());
        assertEquals(event.getEventLocation().getName(), eventGetDTO.getLocationName());
        assertEquals(event.getEventLocation().getLatitude(), eventGetDTO.getLatitude());
        assertEquals(event.getEventLocation().getLongitude(), eventGetDTO.getLongitude());
        assertEquals(event.getStatus(), eventGetDTO.getStatus());
    }

    @Test
    public void convertFromPostDTOToEntity_success(){
        EventPostDTO eventPostDTO = new EventPostDTO();
        eventPostDTO.setTitle("Events");
        eventPostDTO.setType(EventType.PUBLIC);
        eventPostDTO.setDescription("This is an Event");
        eventPostDTO.setEventDate(Calendar.getInstance().getTime());
        eventPostDTO.setStatus(EventStatus.READY);
        eventPostDTO.setLocationName("Zurich");
        eventPostDTO.setLatitude(2F);
        eventPostDTO.setLongitude(2F);

        //Map
        Event event = EventDTOMapper.INSTANCE.convertEventPostDTOtoEntity(eventPostDTO);

        //Check
        assertEquals(eventPostDTO.getTitle(), event.getTitle());
        assertEquals(eventPostDTO.getType(), event.getType());
        assertEquals(eventPostDTO.getDescription(), event.getDescription());
        assertEquals(eventPostDTO.getEventDate(), event.getEventDate());
        assertEquals(eventPostDTO.getLocationName(), event.getEventLocation().getName());
        assertEquals(eventPostDTO.getLatitude(), event.getEventLocation().getLatitude());
        assertEquals(eventPostDTO.getLongitude(), event.getEventLocation().getLongitude());
        assertEquals(eventPostDTO.getStatus(), event.getStatus());
    }
    @Test
    public void testConvertEventTask_ToEventaskGetDTO_success(){
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Event");

        User user = new User();
        user.setId(2L);
        user.setUsername("testUser");
        user.setToken("1");

        EventTask eventTask = new EventTask();
        eventTask.setEvent(event);
        eventTask.setId(3L);
        eventTask.setUser(user);
        eventTask.setDescription("My description");

        EventTaskGetDTO eventTaskGetDTO = EventDTOMapper.INSTANCE.convertEventTaskToEventTaskGetDTO(eventTask);

        assertEquals(eventTaskGetDTO.getId(),eventTask.getId());
        assertEquals(eventTaskGetDTO.getEventID(),eventTask.getEvent().getId());
        assertEquals(eventTaskGetDTO.getUserID(),eventTask.getUser().getId());
        assertEquals(eventTaskGetDTO.getDescription(),eventTask.getDescription());
    }

    @Test
    public void testConvertEventTaskPostDTO_toEntity(){
        EventTaskPostDTO eventTaskPostDTO = new EventTaskPostDTO();
        eventTaskPostDTO.setDescription("This is my task");

        EventTask eventTask = EventDTOMapper.INSTANCE.convertEventTaskPostDTOtoEntity(eventTaskPostDTO);

        assertEquals(eventTask.getDescription(),eventTaskPostDTO.getDescription());
    }
}