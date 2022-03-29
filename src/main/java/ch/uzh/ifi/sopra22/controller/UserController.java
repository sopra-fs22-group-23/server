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

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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

    @Operation(summary = "Get user with ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserGetDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Unathorized for this request", content = @Content),
            @ApiResponse(responseCode = "404", description = "User was not found", content = @Content) })
    @GetMapping(value = "/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUserByUserID(@Parameter(description = "UserID") @PathVariable Long userId, @RequestHeader("Authorization") String token) {
        userService.checkTokenExists(token);

        User user = userService.getUserByIDNum(userId);

        return UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }
}