package ch.uzh.ifi.sopra22.controller;

import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.model.UploadResponseMessage;
import ch.uzh.ifi.sopra22.rest.dto.UserEventGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserPostDTO;
import ch.uzh.ifi.sopra22.rest.mapper.UserDTOMapper;
import ch.uzh.ifi.sopra22.service.EventUserService;
import ch.uzh.ifi.sopra22.service.FileService;
import ch.uzh.ifi.sopra22.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    private final UserService userService;
    private final FileService fileService;

    @Autowired
    public UserController(UserService userService, FileService fileService){
        this.userService = userService;
        this.fileService = fileService;
    }

    @Operation(summary = "Get a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users were found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this request", content = @Content) })
    @GetMapping(value = "/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers(@RequestHeader("Authorization") String token,
                                        @RequestParam(required = false, name = "search") String search) {
        userService.checkTokenExists(token);

        List<User> users = userService.getUsers();
        // search function
        users = userService.sortUsersBySearch(users, search);
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
    public UserGetDTO getUserByUserID(@Parameter(description = "userId") @PathVariable Long userId, @RequestHeader("Authorization") String token) {
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
    public void updateUserByID(@Parameter(description = "userId") @PathVariable Long userId,
                               @RequestHeader("Authorization") String token,
                               @RequestBody UserPostDTO userPostDTO) {

        userService.checkTokenExists(token);
        User userRepo = userService.validateUser(userId, userService.parseBearerToken(token));
        userPostDTO.setId(userId);

        User userInput = UserDTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        User updatedUser = userService.editUser(userInput);

    }

    @Operation(summary = "Add user Image with ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User profile image was saved", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this request", content = @Content),
            @ApiResponse(responseCode = "404", description = "User was not found", content = @Content) })
    @PostMapping(value = "/users/{userId}/image")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<UploadResponseMessage> createProfileImage(@Parameter(description = "userId") @PathVariable Long userId,
                                                                    @RequestHeader("Authorization") String token,
                                                                    @RequestParam("file") MultipartFile file){

        userService.checkTokenExists(token);
        User userRepo = userService.validateUser(userId, userService.parseBearerToken(token));
        System.out.println("Get's in here");
        String createRandomName = fileService.createRandomName(file.getOriginalFilename());
        //String randomString = RandomStringUtils.random(20,true,true);
        System.out.println(createRandomName);
        //file.setOrginalFilename(randomString);
        try {
            fileService.save(file,createRandomName);
            //userService.linkImageToUser(userRepo, file.getOriginalFilename());
            userService.linkImageToUser(userRepo,createRandomName);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new UploadResponseMessage("Uploaded the file successfully: " + createRandomName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new UploadResponseMessage("Could not upload the file: " + file.getOriginalFilename() + "!"));
        }
    }

    @Operation(summary = "Get user picture with ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User profile image was saved", content = @Content),
            //@ApiResponse(responseCode = "400", description = "No file found for this User", content = @Content),
            @ApiResponse(responseCode = "404", description = "User was not found", content = @Content) })
    @GetMapping(value = "/users/{userId}/image")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    //public ResponseEntity<UploadResponseMessage> getFile(@Parameter(description = "userId") @PathVariable Long userId) {
    public ResponseEntity<Resource> getFile(@Parameter(description = "userId") @PathVariable Long userId) {
        User user = userService.getUserByIDNum(userId);

        try {
            Resource file = fileService.load(user.getImageFile());
            return ( ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(file));
        }catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "there is no image");
        }
        //System.out.println("Filename: "+ file.getFilename() + ". File length: " + file.getDescription());

        //return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; NO FILE EXISTENT!!!")
          //      .body(null);
    }



}