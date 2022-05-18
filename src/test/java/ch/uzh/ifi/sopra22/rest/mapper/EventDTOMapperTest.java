package ch.uzh.ifi.sopra22.rest.mapper;

import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.Event.GameMode;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;
import ch.uzh.ifi.sopra22.entity.*;
import ch.uzh.ifi.sopra22.rest.dto.*;
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
        event.setGameMode(GameMode.OFF);

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
        assertEquals(event.getGameMode(),eventGetDTO.getGameMode());
    }

    @Test
    public void convertFromEntity_toUserEventGetDTO_success(){
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
        UserEventGetDTO userEventGetDTO = EventDTOMapper.INSTANCE.convertEntityToUserEventGetDTO(event);
        userEventGetDTO.setEventUserStatus(EventUserStatus.CONFIRMED);
        userEventGetDTO.setEventUserRole(EventUserRole.ADMIN);

        //check
        assertEquals(event.getId().intValue(),userEventGetDTO.getId().intValue());
        assertEquals(event.getTitle(),userEventGetDTO.getTitle());
        assertEquals(event.getType(),userEventGetDTO.getType());
        assertEquals(event.getDescription(),userEventGetDTO.getDescription());
        assertEquals(event.getEventDate(),userEventGetDTO.getEventDate());
        assertEquals(event.getStatus(),userEventGetDTO.getStatus());
        assertEquals(event.getEventLocation().getName(),userEventGetDTO.getLocationName());
        assertEquals(event.getEventLocation().getLongitude(), userEventGetDTO.getLongitude());
        assertEquals(event.getEventLocation().getLatitude(),userEventGetDTO.getLatitude());
        assertEquals(EventUserStatus.CONFIRMED,userEventGetDTO.getEventUserStatus());
        assertEquals(EventUserRole.ADMIN,userEventGetDTO.getEventUserRole());
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
        eventPostDTO.setGameMode(GameMode.OFF);

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
        assertEquals(eventPostDTO.getGameMode(), event.getGameMode());
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

    @Test
    public void testConvertMessage_ToMessageGETDTO_success(){
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Event");

        User user = new User();
        user.setId(2L);
        user.setUsername("testUser");
        user.setToken("1");

        EventChatMessage testMessage = new EventChatMessage();
        testMessage.setEvent(event);
        testMessage.setId(3L);
        testMessage.setUser(user);
        testMessage.setText("My description");

        EventChatMessageGetDTO messageGetDTO = EventDTOMapper.INSTANCE.convertMessageToDTO(testMessage);

        assertEquals(messageGetDTO.getId(),testMessage.getId());
        assertEquals(messageGetDTO.getEventID(),testMessage.getEvent().getId());
        assertEquals(messageGetDTO.getUserID(),testMessage.getUser().getId());
        assertEquals(messageGetDTO.getText(),testMessage.getText());
    }

    @Test
    public void testConvertMessagePostDTO_toEntity(){
        EventChatMessagePostDTO messagePostDTO = new EventChatMessagePostDTO();
        messagePostDTO.setText("Hi");


        EventChatMessage message  = EventDTOMapper.INSTANCE.convertDTOtoMessageEntity(messagePostDTO);

        assertEquals(message.getText(),messagePostDTO.getText());
    }
}