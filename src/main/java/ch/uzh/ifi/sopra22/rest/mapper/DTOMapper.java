package ch.uzh.ifi.sopra22.rest.mapper;

import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.rest.dto.UserGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserGetLoginDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserPostDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserPutDTO;
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
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    //works for login and registration, same template to send
  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);


  //this works when I return user in GET, how the user will look like
  @Mapping(source = "id", target = "id")
  @Mapping(source = "token", target = "token")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "logged_in", target = "logged_in")
  @Mapping(source = "creation_date", target = "creation_date")
  @Mapping(source = "birthday", target = "birthday")
  UserGetLoginDTO convertEntityToUserGetLoginDTO(User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "logged_in", target = "logged_in")
    @Mapping(source = "creation_date", target = "creation_date")
    @Mapping(source = "birthday", target = "birthday")
    UserGetDTO convertEntityToUserGetDTO(User user);

//    @Mapping(source = "id", target = "id")
//    @Mapping(source = "username", target = "username")
//    @Mapping(source = "logged_in", target = "logged_in")
//    @Mapping(source = "creation_date", target = "creation_date")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "logged_in", target = "logged_in")
    @Mapping(source = "token", target = "token")
    User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);

}
