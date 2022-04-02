package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.EventUserId;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.rest.mapper.EventDTOMapper;
import ch.uzh.ifi.sopra22.service.EventUserService;
import ch.uzh.ifi.sopra22.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class EventService {

    private final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;

    private final EventUserService eventUserService;
    private final UserService userService;


    @Autowired
    public EventService(@Qualifier("eventRepository") EventRepository eventRepository, UserService userService, EventUserService eventUserService) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.eventUserService = eventUserService;
    }

    private void updateRepository(Event event) {
        eventRepository.save(event);
        eventRepository.flush();
    }

    private List<Event> getEvents() {
        return this.eventRepository.findAll();
    }

    private Event getEventByIDNum(Long eventId) {
        Optional<Event> eventRepo = eventRepository.findById(eventId);
        Event event;
        try{
            event = eventRepo.orElse(null);
            if (event == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID not found");
            }
        }catch (NullPointerException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID not found");
        }
        return event;
    }

    private List<Event> getPublicEvents() {
        List<Event> allEvents = getEvents();
        List<Event> publicEvents = new ArrayList<>();

        for (Event allEvent : allEvents) {
            if (allEvent.getType() == EventType.PUBLIC) {
                publicEvents.add(allEvent);
            }
        }

        return publicEvents;
    }

    public List<Event> getAvailableEvents(String token) {
        List<Event> availableEvents = getPublicEvents();
        try {
            List<Long> eventIds = eventUserService.getEventIdsFromToken(userService.parseBearerToken(token));
            for (Long eventId : eventIds) {
                availableEvents.add(getEventByIDNum(eventId));
            }
        } catch (Exception ignored) {
            ;
        }

        return availableEvents;
    }

    public Event createEvent(Event newEvent) {
        if (newEvent.getTitle() == null){
            newEvent.setTitle("My New Event");
        }
        if (newEvent.getType() == null){
            newEvent.setType(EventType.PRIVATE);
        }
        if (newEvent.getDescription() == null){
            newEvent.setDescription("Please edit your event description here...");
        }

        newEvent.setStatus(EventStatus.IN_PLANNING);
        updateRepository(newEvent);

        return newEvent;
    }

    public Long validateToken(String token) {
        User user = userService.getUserByToken(userService.parseBearerToken(token));

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is invalid");
        }

        return user.getId();
    }

    public EventUser createDefaultAdmin(Long userId, Long eventId) {
        EventUser newSignup = new EventUser();
        newSignup.setUserId(userId);
        newSignup.setEventId(eventId);
        newSignup.setRole(EventUserRole.ADMIN);
        newSignup.setStatus(EventUserStatus.CONFIRMED);

        EventUser createdEventUser = eventUserService.createEventUser(newSignup);

        return createdEventUser;
    }
}
