package ch.uzh.ifi.sopra22.controller;

import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.rest.dto.EventGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.EventPostDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserGetDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserPostDTO;
import ch.uzh.ifi.sopra22.rest.mapper.EventDTOMapper;
import ch.uzh.ifi.sopra22.rest.mapper.UserDTOMapper;
import ch.uzh.ifi.sopra22.service.EventService;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class EventController {
    private final EventService eventService;
    private final UserService userService;

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @Operation(summary = "Get a list of all public events and private events where authorized")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events were found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this request", content = @Content),
            @ApiResponse(responseCode = "400", description = "Date is in the wrong fromat (yyyy-MM-dd)")})
    @GetMapping(value = "/events")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<EventGetDTO> getAvailableEvents(@RequestHeader("Authorization") String token,
                                                @RequestParam(required = false, name = "type") EventType eventType,
                                                @RequestParam(required = false, name = "role") EventUserRole userRole,
                                                @RequestParam(required = false, name = "from") String fromStringDate,
                                                @RequestParam(required = false, name = "to") String toStringDate,
                                                @RequestParam(required = false, name = "location") String location){
        List<Event> availableEvents = eventService.getAvailableEvents(token);
        System.out.println("Event Type: " + eventType);
        if (userRole != null){
            System.out.println("query Event Role");
            availableEvents = eventService.getQueryEventsUserRole(availableEvents,token,userRole);
        }

        //Transform date
        Date fromDate = eventService.stringToDate(fromStringDate);
        Date toDate = eventService.stringToDate(toStringDate);

        //Go through all the Parameters
        /**
         List<Event> eventsByEventType = eventService.getEventsByEventType(availableEvents, eventType);
         List<Event> eventsByUserRole = eventService.getEventsByEventUserRole(eventsByEventType, userRole, token);
         List<Event> eventsByFromDate = eventService.getEventsByFromDate(eventsByUserRole, fromDate);
         List<Event> eventByToDate = eventService.getEventsByToDate(eventsByFromDate, toDate);
         List<Event> eventByLocation = eventService.getEventsByLocation(eventByToDate, location);*/

        /**List<Event> eventsByUserRole;
         if(userRole != null){
         eventsByUserRole = eventService.getEventfromUserRole(userRole);
         }*/

        List<EventGetDTO> eventGetDTOS = new ArrayList<>();


        // convert each user to the API representation
        for (Event event : availableEvents) {
            //Checks the eventType, before/after dates and location by name before tranforming the representation
            if ((eventType == null || event.getType() == eventType) && (fromDate == null || event.getEventDate().after(fromDate))
                    && (toDate == null || event.getEventDate().before(toDate))){
                if(location == null){
                    eventGetDTOS.add(EventDTOMapper.INSTANCE.convertEntityToEventGetDTO(event));
                }
                if(event.getEventLocation().getName() != null){
                    if (event.getEventLocation().getName().equals(location)){
                        eventGetDTOS.add(EventDTOMapper.INSTANCE.convertEntityToEventGetDTO(event));
                    }
                }
            }

        }
        return eventGetDTOS;
    }


    @Operation(summary = "Create new event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event was created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = EventGetDTO.class))}),
            @ApiResponse(responseCode = "409", description = "Conflict, event not unique", content = @Content),
            @ApiResponse(responseCode = "401", description = "No user with this Token", content = @Content)}
    )
    @PostMapping(value = "/events")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public EventGetDTO createEvent(@RequestHeader("Authorization") String token, @RequestBody EventPostDTO eventPostDTO) {
        User user = eventService.validateToken(token);

        Event eventInput = EventDTOMapper.INSTANCE.convertEventPostDTOtoEntity(eventPostDTO);
        Event createdEvent = eventService.createEvent(eventInput);
        EventUser admin = eventService.createDefaultAdmin(user, createdEvent);
        //to generate the bidirectional relation
        eventService.linkEventUsertoEvent(createdEvent, admin);
        userService.linkEventUsertoUser(user, admin);
/**
        List<EventUser> eventUsers = createdEvent.getEventUsers();
        for (EventUser eventUser: eventUsers){
            System.out.println(eventUser.getRole());
        }*/

        return EventDTOMapper.INSTANCE.convertEntityToEventGetDTO(createdEvent);
    }

    @Operation(summary = "Get a list of all users from an Event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users were found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this request", content = @Content) })
    @GetMapping(value = "events/{eventId}/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers(@Parameter(description = "eventId") @PathVariable Long eventId
            ,@RequestHeader("Authorization") String token) {
        userService.checkTokenExists(token);
        Event event = eventService.getEventByIDNum(eventId);

        List<User> users = eventService.getUsers(event);
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    @Operation(summary = "Get event with ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event was found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = EventGetDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this request", content = @Content),
            @ApiResponse(responseCode = "404", description = "User was not found", content = @Content) })
    @GetMapping(value = "/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public EventGetDTO getUserByUserID(@Parameter(description = "eventId") @PathVariable Long eventId, @RequestHeader("Authorization") String token) {
        userService.checkTokenExists(token);
        userService.validateToken(token);

        Event event =eventService.getEventByIDNum(eventId);

        return EventDTOMapper.INSTANCE.convertEntityToEventGetDTO(event);
    }

    @Operation(summary = "Add a user to an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User was created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserGetDTO.class))}),
            @ApiResponse(responseCode = "409", description = "Conflict, user not unique", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized User, could not be found by token", content = @Content)}
    )
    @PostMapping(value = "/events/{eventId}/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO addUserToEvent(@Parameter(description = "eventId") @PathVariable Long eventId, @RequestHeader("Authorization") String token,
                                     @RequestBody(required = false) UserPostDTO userPostDTO, HttpServletResponse response) {
        // convert API user to internal representation
        User userInput = UserDTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        //get the event
        Event event = eventService.getEventByIDNum(eventId);

        //either get a user by its token, or if no token is passed, create a new user
        User addingUser;
        if (token.isEmpty()) {
            addingUser = userService.createUser(userInput);
            String created_token = addingUser.getToken();
            response.addHeader("token", created_token);
            EventUser guest = eventService.createGuest(addingUser,event);
            eventService.linkEventUsertoEvent(event, guest);
            userService.linkEventUsertoUser(addingUser, guest);

            //eventService.addUserToEvent(event, addingUser);
        } else {
            addingUser = userService.getUserByToken(userService.parseBearerToken(token));
            EventUser guest = eventService.createGuest(addingUser,event);
            eventService.linkEventUsertoEvent(event, guest);
            userService.linkEventUsertoUser(addingUser, guest);
        }

        return UserDTOMapper.INSTANCE.convertEntityToUserGetDTO(addingUser);
    }

}