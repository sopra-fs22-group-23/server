package ch.uzh.ifi.sopra22.rest.mapper;

import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventLocation;
import ch.uzh.ifi.sopra22.rest.dto.EventGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.EventPostDTO;
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

}