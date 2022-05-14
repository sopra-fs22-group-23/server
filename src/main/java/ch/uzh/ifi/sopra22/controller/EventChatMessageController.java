package ch.uzh.ifi.sopra22.controller;

import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.entity.EventChatMessage;
import ch.uzh.ifi.sopra22.entity.EventTask;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.rest.dto.EventChatMessageGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.EventChatMessagePostDTO;
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

@RestController
public class EventChatMessageController {


    private final EventService eventService;
    private final EventUserService eventUserService;

    public EventChatMessageController(EventService eventService, EventUserService eventUserService) {
        this.eventService = eventService;
        this.eventUserService = eventUserService;
    }

    @Operation(summary = "Get all messages for one event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns ", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "404", description = "No such event exists - TODO", content = @Content),
            @ApiResponse(responseCode = "403", description = "Only collaborators and Admins can view event tasks", content = @Content),
            @ApiResponse(responseCode = "403", description = "Token not received, Authorization has failed", content = @Content)
    })
    @GetMapping(value = "/events/{eventID}/messages")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<EventChatMessageGetDTO> getMessages(@RequestHeader("Authorization") String token,
                                               @PathVariable Long eventID) {
        User user = eventService.validateToken(token);//verify that user has rights to access the api

        if(!(eventUserService.canUserAccessEvent(user, eventID, EventUserRole.ADMIN ) ||
                eventUserService.canUserAccessEvent(user, eventID, EventUserRole.COLLABORATOR)
        )){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to access this event");
        }
        //verify id the user is collaborator or admin in the event

        return getMessageDTOs(eventID);
    }


    //------------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Post message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Returns all tasks for the created event", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "404", description = "No such event exists - TODO", content = @Content),
            @ApiResponse(responseCode = "403", description = "Only Admins can access this endpoint", content = @Content),
            @ApiResponse(responseCode = "403", description = "Token not received, Authorization has failed", content = @Content)
    })
    @PostMapping(value = "/events/{eventID}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<EventChatMessageGetDTO> createMessage(@RequestHeader("Authorization") String token,
                                                  @PathVariable Long eventID,
                                                  @RequestBody EventChatMessagePostDTO messagePostDTO

    ) {
        EventChatMessage message = EventDTOMapper.INSTANCE.convertDTOtoMessageEntity(messagePostDTO);

        User user = eventService.validateToken(token);//verify that user has rights to access the api
        if(!(eventUserService.canUserAccessEvent(user, eventID, EventUserRole.ADMIN)
                || eventUserService.canUserAccessEvent(user, eventID, EventUserRole.COLLABORATOR)))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to access this event");
        }//only admin and collab can send messages
        message.setUser(user);
        eventService.addMessage(eventID, message);

        return getMessageDTOs(eventID);
    }

    private List<EventChatMessageGetDTO> getMessageDTOs(Long eventID) {
        List<EventChatMessage> messages = eventService.getMessagesByEventID(eventID);//get all possible tasks for the event id
        List<EventChatMessageGetDTO> DTOs = new ArrayList<>();
        for (EventChatMessage oneMessage : messages) {
            DTOs.add(EventDTOMapper.INSTANCE.convertMessageToDTO(oneMessage));
        }
        return DTOs;
    }
}
