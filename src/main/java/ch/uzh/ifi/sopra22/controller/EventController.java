package ch.uzh.ifi.sopra22.controller;

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
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "Get a list of all public events and private events where authorized")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events were found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this request", content = @Content) })
    @GetMapping(value = "/events")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<EventGetDTO> getAvailableEvents(@RequestHeader("Authorization") String token) {
        List<Event> availableEvents = eventService.getAvailableEvents(token);

        List<EventGetDTO> eventGetDTOS = new ArrayList<>();

        // convert each user to the API representation
        for (Event event : availableEvents) {
            eventGetDTOS.add(EventDTOMapper.INSTANCE.convertEntityToEventGetDTO(event));
        }
        return eventGetDTOS;
    }


    @Operation(summary = "Create new event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event was created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = EventGetDTO.class))}),
            @ApiResponse(responseCode = "409", description = "Conflict, event not unique", content = @Content)}
    )
    @PostMapping(value = "/events")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public EventGetDTO createEvent(@RequestHeader("Authorization") String token, @RequestBody EventPostDTO eventPostDTO) {
        Long userId = eventService.validateToken(token);

        Event eventInput = EventDTOMapper.INSTANCE.convertEventPostDTOtoEntity(eventPostDTO);
        Event createdEvent = eventService.createEvent(eventInput);
        EventUser admin = eventService.createDefaultAdmin(userId, createdEvent.getId());

        return EventDTOMapper.INSTANCE.convertEntityToEventGetDTO(createdEvent);
    }

}