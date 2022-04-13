package ch.uzh.ifi.sopra22.controller;

import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.model.UploadResponseMessage;
import ch.uzh.ifi.sopra22.rest.dto.*;
import ch.uzh.ifi.sopra22.rest.mapper.EventDTOMapper;
import ch.uzh.ifi.sopra22.rest.mapper.UserDTOMapper;
import ch.uzh.ifi.sopra22.service.EventService;
import ch.uzh.ifi.sopra22.service.FileService;
import ch.uzh.ifi.sopra22.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class EventController {

    private final EventService eventService;
    private final UserService userService;
    private final FileService fileService;

    public EventController(EventService eventService, UserService userService, FileService fileService) {
        this.eventService = eventService;
        this.userService = userService;
        this.fileService = fileService;
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
    public List<EventGetDTO> getAvailableEvents(@RequestHeader(value = "Authorization", required = false) String token,
                                                @RequestParam(required = false, name = "type") EventType eventType,
                                                @RequestParam(required = false, name = "role") EventUserRole userRole,
                                                @RequestParam(required = false, name = "from") String fromStringDate,
                                                @RequestParam(required = false, name = "to") String toStringDate,
                                                @RequestParam(required = false, name = "location") String location,
                                                @RequestParam(required = false, name = "search") String search){
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

        // change Sorting based on search score
        availableEvents = eventService.sortEventsBySearch(availableEvents, search);

        List<EventGetDTO> eventGetDTOS = new ArrayList<>();

        // convert each user to the API representation
        for (Event event : availableEvents) {
            //Checks the eventType, before/after dates and location by name before tranforming the representation
            if ((eventType == null || event.getType() == eventType) && (fromDate == null || event.getEventDate().after(fromDate))
                    && (toDate == null || event.getEventDate().before(toDate))){
                if(location == null){
                    eventGetDTOS.add(EventDTOMapper.INSTANCE.convertEntityToEventGetDTO(event));
                }
                else if(event.getEventLocation().getName() != null){
                    if (event.getEventLocation().getName().contains(location)){
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
        EventUser admin = eventService.createEventUser(user, createdEvent, EventUserRole.ADMIN);
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


    @Operation(summary = "Get event with ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event was found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = EventGetDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this request", content = @Content),
            @ApiResponse(responseCode = "404", description = "User was not found", content = @Content) })
    @GetMapping(value = "/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public EventGetDTO getEventByEventId(@Parameter(description = "eventId") @PathVariable Long eventId,
                                         @RequestHeader(value = "Authorization", required = false) String token) {
        Event event = eventService.getEventByIDNum(eventId);

        if (event.getType() == EventType.PRIVATE) {
            userService.checkTokenExists(token);
            eventService.validateTokenForEventGET(event, token);
        }

        return EventDTOMapper.INSTANCE.convertEntityToEventGetDTO(event);
    }

    @Operation(summary = "Update event with ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event was updated", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = EventGetDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this request", content = @Content),
            @ApiResponse(responseCode = "404", description = "User was not found", content = @Content) })
    @PutMapping(value = "/events/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateEventByEventId(@Parameter(description = "eventId") @PathVariable Long eventId, @RequestHeader("Authorization") String token,
                                     @RequestBody EventPostDTO eventPostDTO) {
        userService.checkTokenExists(token);
        User user = eventService.validateToken(token);

        Event event = eventService.getEventByIDNum(eventId);
        Event eventInput = EventDTOMapper.INSTANCE.convertEventPostDTOtoEntity(eventPostDTO);

        //Update the Event

        eventService.updateEvent(event,user,eventInput);


        //return EventDTOMapper.INSTANCE.convertEntityToEventGetDTO(event);
    }

    @Operation(summary = "Get a list of all users from an Event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users were found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this request", content = @Content) })
    @GetMapping(value = "events/{eventId}/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<EventUserGetDTO> getAllUsers(@Parameter(description = "eventId") @PathVariable Long eventId
            ,@RequestHeader(value = "Authorization", required = false) String token) {
        Event event = eventService.getEventByIDNum(eventId);

        // Check authorization
        if (event.getType() == EventType.PRIVATE) {
            userService.checkTokenExists(token);
            eventService.validateTokenForEventGET(event, token);
        }

        List<EventUser> eventUsers = eventService.getEventUsers(event);
        List<EventUserGetDTO> eventUserGetDTOS = new ArrayList<>();

        // convert each user to the API representation
        for (EventUser eventUser : eventUsers) {
            EventUserGetDTO eventUserGetDTO = UserDTOMapper.INSTANCE.convertEntityToEventUserGetDTO(eventUser.getUser());
            eventUserGetDTO.setEventUserRole(eventUser.getRole());
            eventUserGetDTO.setEventUserStatus(eventUser.getStatus());
            eventUserGetDTO.setEventId(eventUser.getEvent().getId());

            eventUserGetDTOS.add(eventUserGetDTO);
        }

        return eventUserGetDTOS;
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
    public EventUserGetDTO addUserToEvent(@Parameter(description = "eventId") @PathVariable Long eventId, @RequestHeader(value = "Authorization", required = false) String token,
                                     @RequestBody(required = false) EventUserPostDTO eventUserPostDTO, HttpServletResponse response) {
        // convert API user to internal representation
        User userInput = UserDTOMapper.INSTANCE.convertEventUserPostDTOtoEntity(eventUserPostDTO);

        //get the event & user to be added
        Event event = eventService.getEventByIDNum(eventId);
        User addedUser = userService.getUserByPartialUser(userInput);

        // Check and get eventUser
        EventUser newSignup = eventService.validEventUserPOST(userInput, event, eventUserPostDTO, token);

        // Link up all relationships & return addedUser
        eventService.linkEventUsertoEvent(event, newSignup);
        userService.linkEventUsertoUser(addedUser, newSignup);

        // Modify User with EventUser attributes
        EventUserGetDTO eventUserGetDTO = UserDTOMapper.INSTANCE.convertEntityToEventUserGetDTO(addedUser);
        eventUserGetDTO.setEventUserRole(newSignup.getRole());
        eventUserGetDTO.setEventUserStatus(newSignup.getStatus());
        eventUserGetDTO.setEventId(eventId);

        return eventUserGetDTO;
    }


    @Operation(summary = "Update existing EventUser")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "EventUser was updated", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserGetDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Conflict, user not unique", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized User, could not be found by token", content = @Content)}
    )
    @PutMapping(value = "/events/{eventId}/users")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void editEventUser(@Parameter(description = "eventId") @PathVariable Long eventId,
                              @RequestHeader(value = "Authorization", required = false) String token,
                                          @RequestBody(required = false) EventUserPostDTO eventUserPostDTO, HttpServletResponse response) {
        // convert API user to internal representation
        User userInput = UserDTOMapper.INSTANCE.convertEventUserPostDTOtoEntity(eventUserPostDTO);

        //get the event & user to be added
        Event event = eventService.getEventByIDNum(eventId);

        // Do manipulations if valid action
        EventUser eventUser = eventService.validEventUserPUT(userInput, event, eventUserPostDTO, token);

    }

        @Operation(summary = "Add event Image with ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event profile image was saved", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Event was not found", content = @Content) })
    @PostMapping(value = "/events/{eventId}/image")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<UploadResponseMessage> createProfileImage(@Parameter(description = "eventId") @PathVariable Long eventId,
                                                                    @RequestHeader("Authorization") String token,
                                                                    @RequestParam("file") MultipartFile file){

        userService.checkTokenExists(token);
        User user = eventService.validateToken(token);
        Event event = eventService.getEventByIDNum(eventId);
        eventService.isUserAloudToUpdate(event,user);
        String createRandomName = fileService.createRandomName(file.getOriginalFilename());
        //String randomString = RandomStringUtils.random(20,true,true);
        System.out.println(createRandomName);
        //file.setOrginalFilename(randomString);
        try {
            fileService.save(file,createRandomName);
            eventService.linkImageToEvent(event,createRandomName);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new UploadResponseMessage("Uploaded the file successfully: " + createRandomName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new UploadResponseMessage("Could not upload the file: " + file.getOriginalFilename() + "!"));
        }
    }

    @Operation(summary = "Get event picture with ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User profile image was saved", content = @Content),
            //@ApiResponse(responseCode = "400", description = "No file found for this User", content = @Content),
            @ApiResponse(responseCode = "404", description = "User was not found", content = @Content) })
    @GetMapping(value = "/events/{eventId}/image")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    //public ResponseEntity<UploadResponseMessage> getFile(@Parameter(description = "userId") @PathVariable Long userId) {
    public ResponseEntity<Resource> getFile(@Parameter(description = "eventId") @PathVariable Long eventId) {
        Event event = eventService.getEventByIDNum(eventId);

        try {
            Resource file = fileService.load(event.getPicture());
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

    @Operation(summary = "Get a list of all events of user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "events were found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this request", content = @Content) })
    @GetMapping(value = "/users/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserEventGetDTO> getAllUserEvents(@Parameter(description = "userId") @PathVariable Long userId,
                                                  @RequestHeader("Authorization") String token) {
        // Validate user by token
        User user = userService.validateUser(userId, token);

        // Get enhanced events list
        return eventService.generateUserEvents(user);
    }


}