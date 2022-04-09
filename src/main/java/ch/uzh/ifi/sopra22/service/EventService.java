package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.repository.EventRepository;
import ch.uzh.ifi.sopra22.repository.EventUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class EventService {

    private final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final EventUserRepository eventUserRepository;

    private final EventUserService eventUserService;
    private final UserService userService;


    @Autowired
    public EventService(@Qualifier("eventRepository") EventRepository eventRepository, @Qualifier("eventUserRepository") EventUserRepository eventUserRepository,
                        @Qualifier("userService") UserService userService, @Qualifier("eventUserService") EventUserService eventUserService) {
        this.eventRepository = eventRepository;
        this.eventUserRepository =eventUserRepository;
        this.userService = userService;
        this.eventUserService = eventUserService;
    }

    private Event updateRepository(Event event) {
        Event updatedEvent = eventRepository.save(event);
        eventRepository.flush();
        return updatedEvent;
    }

    private List<Event> getEvents() {
        return this.eventRepository.findAll();
    }

    public Event getEventByIDNum(Long eventId) {
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

    public List<Event> getAvailableEvents(String token) {
        List<Event> availableEvents = eventRepository.findByType(EventType.PUBLIC);
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
        Event savedEvent = updateRepository(newEvent);

        return savedEvent;
    }

    public User validateToken(String token) {
        User user = userService.getUserByToken(userService.parseBearerToken(token));

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is invalid");
        }

        return user;
    }

    public EventUser createEventUser(User user, Event event, EventUserRole userRole) {
        EventUser newSignup = new EventUser();
        //newSignup.setUserId(userId);
        //newSignup.setEventId(eventId);
        newSignup.setUser(user);
        newSignup.setEvent(event);
        newSignup.setRole(userRole);
        newSignup.setStatus(EventUserStatus.CONFIRMED);

        return eventUserService.createEventUser(newSignup);
    }

    public void linkEventUsertoEvent(Event createdEvent, EventUser admin) {
        createdEvent.addEventUsers(admin);
        updateRepository(createdEvent);
    }

    public Date stringToDate(String StringDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        if (StringDate != null) {
            try {
                date = format.parse(StringDate);
            } catch (Exception e) {
                System.out.println("Date Transformation not done, hence return null");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date is in the wrong format (yyyy-MM-dd)");
            }
        }
        return date;
    }

    public List<Event> getQueryEventsUserRole(List<Event> availableEvents, String token, EventUserRole userRole) {
        List<Event> eventUserAfterRole = new ArrayList<>();
        //List<EventUser>
        User user = userService.getUserByToken(userService.parseBearerToken(token));
        for (Event event: availableEvents){
            List<EventUser> eventUsers = event.getEventUsers();
            for (EventUser eventUser : eventUsers){
                if(eventUser.getUser() == user && eventUser.getRole() == userRole){
                    eventUserAfterRole.add(event);
                    break;
                }
            }
        }
        return eventUserAfterRole;
    }

    public List<User> getUsers(Event event) {
        List<EventUser> eventUsers = event.getEventUsers();
        List<User> users = new ArrayList<>();
        for (EventUser eventUser:eventUsers){
            users.add(eventUser.getUser());
        }
        return users;
    }
}
