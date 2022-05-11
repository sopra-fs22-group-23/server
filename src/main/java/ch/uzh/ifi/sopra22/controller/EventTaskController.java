package ch.uzh.ifi.sopra22.controller;

import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.entity.EventTask;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.rest.dto.EventTaskGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.EventTaskPostDTO;
import ch.uzh.ifi.sopra22.rest.mapper.EventDTOMapper;
import ch.uzh.ifi.sopra22.service.EventService;
import ch.uzh.ifi.sopra22.service.EventUserService;
import ch.uzh.ifi.sopra22.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class EventTaskController {

    private final EventService eventService;
    private final UserService userService;
    private final EventUserService eventUserService;

    public EventTaskController(EventService eventService, UserService userService, EventUserService eventUserService) {
        this.eventService = eventService;
        this.userService = userService;
        this.eventUserService = eventUserService;
    }

    @Operation(summary = "Get all tasks for one event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns ", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "404", description = "No such event exists - TODO", content = @Content),
            @ApiResponse(responseCode = "403", description = "Only collaborators and Admins can view event tasks", content = @Content),
            @ApiResponse(responseCode = "403", description = "Token not received, Authorization has failed", content = @Content)
    })
    @GetMapping(value = "/events/{eventID}/tasks")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<EventTaskGetDTO> getEventTasks(@RequestHeader("Authorization") String token,
                                               @PathVariable Long eventID) {
        User user = eventService.validateToken(token);//verify that user has rights to access the api

        if(!(eventUserService.canUserAccessEvent(user, eventID, EventUserRole.ADMIN ) ||
                eventUserService.canUserAccessEvent(user, eventID, EventUserRole.COLLABORATOR)
            )){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to access this event");
        }
       //verify id the user is collaborator or admin in the event

        return getEventTaskGetDTOS(eventID);
    }


    //------------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Post Event task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Returns all tasks for the created event", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "404", description = "No such event exists - TODO", content = @Content),
            @ApiResponse(responseCode = "403", description = "Only Admins can access this endpoint", content = @Content),
            @ApiResponse(responseCode = "403", description = "Token not received, Authorization has failed", content = @Content)
    })
    @PostMapping(value = "/events/{eventID}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<EventTaskGetDTO> createEventTasks(@RequestHeader("Authorization") String token,
                                                  @PathVariable Long eventID,
                                                  @RequestBody EventTaskPostDTO eventTaskPostDTO

    ) {
        EventTask task = EventDTOMapper.INSTANCE.convertEventTaskPostDTOtoEntity(eventTaskPostDTO);

        User user = eventService.validateToken(token);//verify that user has rights to access the api
        if(!eventUserService.canUserAccessEvent(user, eventID, EventUserRole.ADMIN)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to access this event");
        }//only admin can add new tasks to the session
        System.out.println("user can access it");
        eventService.addTask(eventID, task);

        return getEventTaskGetDTOS(eventID);
    }


    @Operation(summary = "Update task - working only for userID and description, if one is null, it is not updated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "task was updated", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "404", description = "No such event exists - TODO", content = @Content),
            @ApiResponse(responseCode = "403", description = "Only Admins can access this endpoint", content = @Content),
            @ApiResponse(responseCode = "403", description = "Token not received, Authorization has failed", content = @Content)
    })
    @PutMapping(value = "/events/{eventID}/tasks/{taskID}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<EventTaskGetDTO> updateEventTasks(@RequestHeader("Authorization") String token,
                                                  @PathVariable Long taskID,
                                                  @PathVariable Long eventID,
                                                  @RequestBody EventTaskPostDTO eventTaskPostDTO

    ) {

        User user = eventService.validateToken(token);//verify that user has rights to access the api - ADMIN or COLLAB

        if(!(eventUserService.canUserAccessEvent(user, eventID, EventUserRole.ADMIN) || eventUserService.canUserAccessEvent(user, eventID, EventUserRole.COLLABORATOR))){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to access this event");
        }

        // update task
        eventService.updateTask(taskID, eventTaskPostDTO);

        return getEventTaskGetDTOS(eventID);
    }

    @Operation(summary = "Get all tasks of one event for one user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the tasks", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "404", description = "No such event exists", content = @Content),
            @ApiResponse(responseCode = "403", description = "Only collaborators and Admins can view event tasks", content = @Content),
            @ApiResponse(responseCode = "403", description = "Token not received, Authorization has failed", content = @Content)
    })
    @GetMapping(value = "/events/{eventID}/users/{userID}/tasks")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<EventTaskGetDTO> getEventTasksFromUser(@RequestHeader("Authorization") String token,
                                               @PathVariable Long eventID, @PathVariable Long userID) {
        User user = eventService.validateToken(token);//verify that user has rights to access the api
        //can only access own events and tasks
        if (!user.getId().equals(userID)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Can only access your own event tasks");
        }

        List<EventTask> tasks = eventService.getTasksByEventID(eventID);

        List<EventTask> userTasks = eventUserService.getUserTasks(tasks,userID);

        List<EventTaskGetDTO> tasksDTOs = new ArrayList<>();
        for (EventTask oneTask : userTasks) {
            tasksDTOs.add(EventDTOMapper.INSTANCE.convertEventTaskToEventTaskGetDTO(oneTask));
        }
        return tasksDTOs;
    }



    /**
     * help function to transfer tasks to DTO representation
     */
    private List<EventTaskGetDTO> getEventTaskGetDTOS(Long eventID) {
        List<EventTask> tasks = eventService.getTasksByEventID(eventID);//get all possible tasks for the event id
        List<EventTaskGetDTO> tasksDTOs = new ArrayList<>();
        for (EventTask oneTask : tasks) {
            tasksDTOs.add(EventDTOMapper.INSTANCE.convertEventTaskToEventTaskGetDTO(oneTask));
        }
        return tasksDTOs;
    }

}
