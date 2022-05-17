package ch.uzh.ifi.sopra22.rest.mapper;

import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventChatMessage;
import ch.uzh.ifi.sopra22.entity.EventTask;
import ch.uzh.ifi.sopra22.rest.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper
public interface EventDTOMapper {

    EventDTOMapper INSTANCE = Mappers.getMapper(EventDTOMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "eventDate", target = "eventDate")
    @Mapping(source = "eventLocation.name", target = "locationName")
    @Mapping(source = "eventLocation.longitude", target = "longitude")
    @Mapping(source = "eventLocation.latitude", target = "latitude")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "gameMode", target = "gameMode")
    EventGetDTO convertEntityToEventGetDTO(Event event);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "eventDate", target = "eventDate")
    @Mapping(source = "eventLocation.name", target = "locationName")
    @Mapping(source = "eventLocation.longitude", target = "longitude")
    @Mapping(source = "eventLocation.latitude", target = "latitude")
    @Mapping(source = "status", target = "status")
    UserEventGetDTO convertEntityToUserEventGetDTO(Event event);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "eventDate", target = "eventDate")
    @Mapping(source = "locationName", target = "eventLocation.name")
    @Mapping(source = "latitude", target = "eventLocation.latitude")
    @Mapping(source = "longitude", target = "eventLocation.longitude")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "gameMode", target = "gameMode")
    Event convertEventPostDTOtoEntity(EventPostDTO eventPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "user.id", target = "userID")
    @Mapping(source = "event.id", target = "eventID")
    @Mapping(source = "description", target = "description")
    EventTaskGetDTO convertEventTaskToEventTaskGetDTO(EventTask eventTask);


    @Mapping(source = "description", target = "description")
    EventTask convertEventTaskPostDTOtoEntity(EventTaskPostDTO eventTaskPostDTO);


    @Mapping(source = "id", target = "id")
    @Mapping(source = "user.id", target = "userID")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "event.id", target = "eventID")
    @Mapping(source = "text", target = "text")
    @Mapping(source = "datetime", target = "datetime")
    EventChatMessageGetDTO convertMessageToDTO(EventChatMessage message);

    @Mapping(source = "text", target = "text")
    EventChatMessage convertDTOtoMessageEntity(EventChatMessagePostDTO messagePostDTO);

}
