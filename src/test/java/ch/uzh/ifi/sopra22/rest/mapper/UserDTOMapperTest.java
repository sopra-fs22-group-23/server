package ch.uzh.ifi.sopra22.rest.mapper;

import ch.uzh.ifi.sopra22.constants.UserStatus;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.rest.dto.UserGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserPostDTO;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDTOMapperTest {
    @Test
    public void testCreateUser_fromUserPostDTO_toUser_success() {
        Date today = Calendar.getInstance().getTime();

        // create UserPostDTO
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("name");
        userPostDTO.setUsername("username");
        userPostDTO.setPassword("password");
        userPostDTO.setBirthday(today);
        userPostDTO.setId(1L);
        userPostDTO.setBiography("This is me");
        userPostDTO.setEmail("2@gmail.com");

        // MAP -> Create user
        User user = UserDTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // check content
        assertEquals(userPostDTO.getName(), user.getName());
        assertEquals(userPostDTO.getUsername(), user.getUsername());
        assertEquals(userPostDTO.getPassword(),user.getPassword());
        assertEquals(userPostDTO.getBirthday(),user.getBirthday());
        assertEquals(userPostDTO.getId(), user.getId());
        assertEquals(userPostDTO.getBiography(), user.getBiography());
        assertEquals(userPostDTO.getEmail(), user.getEmail());
    }

    @Test
    public void testGetUser_fromUser_toUserGetDTO_success() {
        Date today = Calendar.getInstance().getTime();
        // create User
        User user = new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");
        user.setId(1L);
        user.setBirthday(today);
        user.setBiography("This is me");

        // MAP -> Create UserGetDTO
        UserGetDTO userGetDTO = UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        // check content
        assertEquals(user.getId(), userGetDTO.getId());
        assertEquals(user.getName(), userGetDTO.getName());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getStatus(), userGetDTO.getStatus());
        assertEquals(user.getBirthday(), userGetDTO.getBirthday());
        assertEquals(user.getBiography(),userGetDTO.getBiography());
    }
}