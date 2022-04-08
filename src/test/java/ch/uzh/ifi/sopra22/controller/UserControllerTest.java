package ch.uzh.ifi.sopra22.controller;

import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.rest.dto.UserPostDTO;
import ch.uzh.ifi.sopra22.service.FileService;
import ch.uzh.ifi.sopra22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private FileService fileService;

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setPassword("password");
        user.setId(1L);
        user.setToken("1");

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization",user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("[0].birthday", is(user.getBirthday())));
    }
    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setToken("1");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("password");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.birthday", is(user.getBirthday())));
    }
    @Test
    public void createUserOnlyUsername_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("testUsername");
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setToken("1");

        UserPostDTO userPostDTO = new UserPostDTO();
        //userPostDTO.setName("Test User");
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("password");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.birthday", is(user.getBirthday())));
    }

    @Test
    public void authenticateUser_validInput_userAuthenticated() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setToken("1");
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("password");

        given(userService.authenticateUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.birthday", is(user.getBirthday())));

    }

    @Test
    public void authenticateUser_invalidPassword() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setToken("1");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("password9");


        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is incorrect"))
                .when(userService).authenticateUser(Mockito.any());

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest());
    }


    @Test
    public void authenticateUser_invalidUsername() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setToken("1");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsernam");
        userPostDTO.setPassword("password");


        // when/then -> do the request + validate the result

        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username not found"))
                .when(userService).authenticateUser(Mockito.any());

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isBadRequest());
    }
    @Test
    public void logoutUser_validInput() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setToken("1");

        given(userService.getUserByIDNum(Mockito.any())).willReturn(user);

        //when
        MockHttpServletRequestBuilder putRequest = put("/logout/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization",user.getToken());

        //then
        mockMvc.perform(putRequest).andExpect(status().isNoContent()); //check status
    }

    @Test
    public void getUserFromUserID_validInputs_getUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setToken("1");

        given(userService.getUserByIDNum(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization",user.getToken());

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.birthday", is(user.getBirthday())));
    }


    @Test
    public void updateUserFromUserID_validInput() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setToken("1");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testingUsername");
        userPostDTO.setBirthday(Calendar.getInstance().getTime());
        userPostDTO.setBiography("Its WeVent");
        userPostDTO.setEmail("2@gmail.com");

        given(userService.validateUser(Mockito.any(),Mockito.any())).willReturn(user);
        given(userService.editUser(Mockito.any())).willReturn(user);

        //when
        MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO))
                .header("Authorization",user.getToken());

        //then
        mockMvc.perform(putRequest).andExpect(status().isNoContent()); //check for change
    }
     @Test
    public void updateUser_invaildInput() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setToken("1");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testingUsername");
        userPostDTO.setBirthday(Calendar.getInstance().getTime());

         given(userService.validateUser(Mockito.any(),Mockito.any())).willReturn(user);
         given(userService.editUser(Mockito.any())).willReturn(user);

        //when
        MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO))
                .header("Authorization",user.getToken());

        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized for the update"))
                .when(userService).validateUser(Mockito.any(),Mockito.any());

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());
    }

/**
    @Test
    public void uploadfile_validInput() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setToken("1");


        given(userService.validateUser(Mockito.any(),Mockito.any())).willReturn(user);
        //Mock Request
        MockMultipartFile jsonFile = new MockMultipartFile("test.json", "", "application/json", "{\"key1\": \"value1\"}".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/1/image")
                .file(jsonFile)
                .header("Authorization",user.getToken())
                        .param("file", String.valueOf(jsonFile)))
                .andExpect(status().isCreated());

    }
    @Test
    public void downloadURL_validInput() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("password");
        user.setToken("1");

        MockMultipartFile jsonFile = new MockMultipartFile("test.json", "", "application/json", "{\"key1\": \"value1\"}".getBytes());

        given(userService.getUserByIDNum(Mockito.any())).willReturn(user);
        given(fileService.load(Mockito.any())).willReturn((Resource) jsonFile);

        MockHttpServletRequestBuilder getRequest = get("/users/1/image")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization",user.getToken());

        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
    }*/


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