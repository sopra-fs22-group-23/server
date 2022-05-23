package ch.uzh.ifi.sopra22.rest.mapper;

import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.rest.dto.EventUserGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.EventUserPostDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserPostDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface UserDTOMapper {

    UserDTOMapper INSTANCE = Mappers.getMapper(UserDTOMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "biography", target = "biography")
    @Mapping(source = "email", target = "email")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source= "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "biography", target = "biography")
    @Mapping(source = "email", target = "email")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "biography", target = "biography")
    EventUserGetDTO convertEntityToEventUserGetDTO(User user);

    @Mapping(source= "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "biography", target = "biography")
    @Mapping(source = "email", target = "email")
    User convertEventUserPostDTOtoEntity(EventUserPostDTO eventUserPostDTO);

}
