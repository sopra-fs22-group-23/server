package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class EventService {

    private final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;

    private final EventUserService eventUserService;
    private final UserService userService;


    @Autowired
    public EventService(@Qualifier("eventRepository") EventRepository eventRepository, @Qualifier("userService") UserService userService,
                        @Qualifier("eventUserService") EventUserService eventUserService) {
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
        Event currentEvent;
        try {
            List<Long> eventIds = eventUserService.getEventIdsFromToken(userService.parseBearerToken(token));
            for (Long eventId : eventIds) {
                currentEvent = getEventByIDNum(eventId);
                if (currentEvent.getType() == EventType.PRIVATE) {
                    availableEvents.add(getEventByIDNum(eventId));
                }
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
            newEvent.setType(EventType.PUBLIC);
        }
        if (newEvent.getDescription() == null){
            newEvent.setDescription("Please edit your event description here...");
        }

        newEvent.setStatus(EventStatus.IN_PLANNING);
        updateRepository(newEvent);

        return newEvent;
    }

    public User validateToken(String token) {
        User user = userService.getUserByToken(userService.parseBearerToken(token));

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is invalid");
        }

        return user;
    }

    public EventUser createDefaultAdmin(User user, Long eventId) {
        EventUser newSignup = new EventUser();
        //newSignup.setUserId(userId);
        //newSignup.setEventId(eventId);
        newSignup.setUser(user);
        newSignup.setEvent(getEventByIDNum(eventId));
        newSignup.setRole(EventUserRole.ADMIN);
        newSignup.setStatus(EventUserStatus.CONFIRMED);

        return eventUserService.createEventUser(newSignup);
    }

    public void linkEventUsertoEvent(Event createdEvent, EventUser admin) {
        createdEvent.addEventUsers(admin);
        updateRepository(createdEvent);
    }
}
