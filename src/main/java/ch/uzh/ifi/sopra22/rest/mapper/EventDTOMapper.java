package ch.uzh.ifi.sopra22.rest.mapper;

import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.rest.dto.EventGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.EventPostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper
public interface EventDTOMapper {

    EventDTOMapper INSTANCE = Mappers.getMapper(EventDTOMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "picture", target = "picture")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "eventDate", target = "eventDate")
    @Mapping(source = "eventLocation.name", target = "locationName")
    @Mapping(source = "eventLocation.longitude", target = "longitude")
    @Mapping(source = "eventLocation.latitude", target = "latitude")
    @Mapping(source = "status", target = "status")
    EventGetDTO convertEntityToEventGetDTO(Event event);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "picture", target = "picture")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "eventDate", target = "eventDate")
    @Mapping(source = "locationName", target = "eventLocation.name")
    @Mapping(source = "latitude", target = "eventLocation.latitude")
    @Mapping(source = "longitude", target = "eventLocation.longitude")
    @Mapping(source = "status", target = "status")
    Event convertEventPostDTOtoEntity(EventPostDTO eventPostDTO);

}
