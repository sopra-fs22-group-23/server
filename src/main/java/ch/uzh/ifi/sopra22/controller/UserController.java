package ch.uzh.ifi.sopra22.controller;

import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.rest.dto.UserGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserPostDTO;
import ch.uzh.ifi.sopra22.rest.mapper.UserDTOMapper;
import ch.uzh.ifi.sopra22.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users were found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this request", content = @Content) })
    @GetMapping(value = "/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers(@RequestHeader("Authorization") String token) {
        userService.checkTokenExists(token);

        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }


    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User was created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserGetDTO.class))}),
            @ApiResponse(responseCode = "409", description = "Conflict, user not unique", content = @Content)}
    )
    @PostMapping(value = "/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO, HttpServletResponse response) {
        // convert API user to internal representation
        User userInput = UserDTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        User createdUser = userService.createUser(userInput);
        String token = createdUser.getToken();
        response.addHeader("token",token);

        return UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    @Operation(summary = "Login a User to existent account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User was created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserGetDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Login failed Invalid credentials", content = @Content)}
    )
    @PostMapping(value = "/login")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO authenticateUser(@RequestBody UserPostDTO userPostDTO, HttpServletResponse response) {
        // convert API user to internal representation
        User userInput = UserDTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        User authenticatedUser = userService.authenticateUser(userInput);
        String token = authenticatedUser.getToken();
        response.addHeader("token",token);

        return UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(authenticatedUser);
    }

    @Operation(summary = "User logout")
    @ApiResponses(value={
            @ApiResponse(responseCode = "204", description = "User Logged out", content = @Content),
            @ApiResponse(responseCode = "401", description = "User with userId not authorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "User was not found", content = @Content)
    })
    @PutMapping(value = "/logout/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void logout(@PathVariable("userId") Long userId, @RequestHeader("Authorization") String token){
        User user = userService.getUserByIDNum(userId);

        userService.compareUserByToken(user.getToken(),token);

        userService.logout(user);
    }

    @Operation(summary = "Get user with ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserGetDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this request", content = @Content),
            @ApiResponse(responseCode = "404", description = "User was not found", content = @Content) })
    @GetMapping(value = "/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUserByUserID(@Parameter(description = "UserID") @PathVariable Long userId, @RequestHeader("Authorization") String token) {
        userService.checkTokenExists(token);
        userService.validateToken(token);

        User user = userService.getUserByIDNum(userId);

        return UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    @Operation(summary = "Update user with ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User was updated", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserGetDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this request", content = @Content),
            @ApiResponse(responseCode = "404", description = "User was not found", content = @Content) })
    @PutMapping(value = "/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateUserByID(@Parameter(description = "UserID") @PathVariable Long userId,
                               @RequestHeader("Authorization") String token, @RequestBody UserPostDTO userPostDTO) {

        userService.checkTokenExists(token);
        User userRepo = userService.validateUser(userId, userService.parseBearerToken(token));
        userPostDTO.setId(userId);

        User userInput = UserDTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        User updatedUser = userService.editUser(userInput);
    }


}