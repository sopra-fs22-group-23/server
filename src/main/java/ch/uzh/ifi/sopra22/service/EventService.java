package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventTask;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.repository.EventRepository;
import ch.uzh.ifi.sopra22.repository.EventTaskRepository;
import ch.uzh.ifi.sopra22.repository.EventUserRepository;
import ch.uzh.ifi.sopra22.rest.dto.EventUserPostDTO;
import ch.uzh.ifi.sopra22.rest.dto.UserPostDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
@Transactional
public class EventService {

    private final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final EventUserRepository eventUserRepository;
    private final EventTaskRepository eventTaskRepository;

    private final EventUserService eventUserService;
    private final UserService userService;

    // Search constants
    private final int containsWholeFactor = 3;
    private final int titleWeight = 6;
    private final int locationNameWeight = 4;
    private final int descriptionWeight = 1;


    @Autowired
    public EventService(@Qualifier("eventRepository") EventRepository eventRepository,
                        @Qualifier("eventUserRepository") EventUserRepository eventUserRepository,
                        @Qualifier("eventTaskRepository") EventTaskRepository eventTaskRepository,
                        @Qualifier("userService") UserService userService,
                        @Qualifier("eventUserService") EventUserService eventUserService
    ) {
        this.eventRepository = eventRepository;
        this.eventUserRepository =eventUserRepository;
        this.eventTaskRepository =eventTaskRepository;
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

    public List<Event> sortEventsBySearch(List<Event> availableEvents, String search) {
        if (search == null || search.equals("")) {
            return availableEvents;
        }
        // parse string to have spaces
        search = userService.parseString(search);

        List<Integer> scores = new ArrayList<>();
        List<Integer> sortedScores = new ArrayList<>();
        List<Event> events = new ArrayList<>();

        // Assign scores to events
        for (Event event : availableEvents) {
            int score = 0;
            try {
                // Contains check
                if (event.getTitle().toLowerCase().contains(search)) {score += containsWholeFactor * titleWeight;}
                if (event.getDescription().toLowerCase().contains(search)) {score += containsWholeFactor * descriptionWeight;}

                //Check words of query (space = ' ', '_', '-', '+')
                List<String> words = new ArrayList<>();
                words = userService.getWordsFromString(search);
                for (String word : words) {
                    try {
                        if (event.getTitle().toLowerCase().contains(word)) {score += titleWeight;}
                        if (event.getDescription().toLowerCase().contains(word)) {score += descriptionWeight;}
                        if (event.getEventLocation().getName().toLowerCase().contains(word)) {score += locationNameWeight;}
                    } catch (Exception ignore) {;}
                }
                // last contains check to check occasionally missing locationName last in try block
                if (event.getEventLocation().getName().toLowerCase().contains(search)) {score += containsWholeFactor * locationNameWeight;}
            } catch (Exception ignore) {;}
            scores.add(score);
            sortedScores.add(score);
        }

        // Sort events based on scores
        Collections.sort(sortedScores); // ascending
        Collections.reverse(sortedScores); // descending

        for (int score : sortedScores) {
            if (score <= 0) {
                break;
            }
            events.add(availableEvents.get(scores.indexOf(score)));
            scores.set(scores.indexOf(score), -1);
        }
        return events;
    }

    public List<Event> getAvailableEvents(String token) {
        List<Event> availableEvents = eventRepository.findByType(EventType.PUBLIC);
        if (token != null) {
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

    public EventUser validateTokenForEventGET(Event event, String token) {
        User user = validateToken(token);
        EventUser validUser = new EventUser();
        boolean thrower = true;
        for (EventUser eventUser : event.getEventUsers()) {
            if (Objects.equals(eventUser.getUser().getId(), user.getId())) {
                thrower = false;
                validUser = eventUser;
                break;
            }
        }

        if (thrower) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is not authorized for this event");
        }
        return validUser;
    }

    public EventUser createEventUser(User user, Event event, EventUserRole userRole) {
        EventUser newSignup = new EventUser();
        newSignup.setUser(user);
        newSignup.setEvent(event);
        newSignup.setRole(userRole);
        newSignup.setStatus(EventUserStatus.CONFIRMED);

        return eventUserService.createEventUser(newSignup);
    }

    public EventUser validEventUserPOST(User inputUser, Event event, EventUserPostDTO eventUserPostDTO, String token) {
        User tokenUser = validateToken(token);
        EventUserRole userRole = eventUserPostDTO.getEventUserRole();
        // Check if inputUser matches tokenUser
        if (inputUser.getId().equals(tokenUser.getId()) && event.getType() == EventType.PUBLIC) {
            EventUser newSignup = new EventUser();
            newSignup.setUser(tokenUser);
            newSignup.setEvent(event);
            newSignup.setRole(EventUserRole.GUEST);
            newSignup.setStatus(EventUserStatus.CONFIRMED);
            return eventUserService.createEventUser(newSignup);
        } else {
            // Check if tokenUser is ADMIN or COLLABORATOR of event
            boolean thrower = true;
            for (EventUser eventUser : event.getEventUsers()) {
                if (eventUser.getUser().getId().equals(tokenUser.getId()) && (eventUser.getRole() == EventUserRole.ADMIN ||
                        eventUser.getRole() == EventUserRole.COLLABORATOR)) {
                    thrower = false;
                    break;
                }
            }
            // Check if thrower is still active (no match with requ. roles)
            if (thrower) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is not authorized for this action");
            }
            // Get user from posted id
            User addedUser = userService.getUserByIDNum(inputUser.getId());

            // Assign invitation eventUser
            EventUser newInvite = new EventUser();
            newInvite.setUser(addedUser);
            newInvite.setEvent(event);
            if (userRole == null) {
                userRole = EventUserRole.GUEST;
            }
            newInvite.setRole(userRole);
            newInvite.setStatus(EventUserStatus.INVITED);
            return eventUserService.createEventUser(newInvite);
        }
    }

    private void checkSingleAdmin(EventUser eventUser) {
        List<EventUser> eventUserList = eventUserRepository.findByEventId(eventUser.getEvent().getId());
        boolean singleAdmin = true;
        for (EventUser ev : eventUserList) {
            if (ev.getRole() == EventUserRole.ADMIN && !ev.getEventUserId().equals(eventUser.getEventUserId())) {
                singleAdmin = false;
            }
        }
        if (singleAdmin) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot cancel or change role if single admin");
        }
    }

    public EventUser validEventUserPUT(User inputUser, Event event, EventUserPostDTO eventUserPostDTO, String token) {
        User tokenUser = validateToken(token);
        EventUserRole userRole = eventUserPostDTO.getEventUserRole();

        // Check if eventUser exists
        EventUser eventUser = eventUserService.ensureEventUserExists(event.getId(), inputUser.getId());

        // Check singleAdmin
        checkSingleAdmin(eventUser);

        // Check if self-change
        if (tokenUser.getId().equals(inputUser.getId())) {
            boolean done = false;
            if (eventUserPostDTO.getEventUserRole() != null) {
                if (eventUser.getRole() == EventUserRole.ADMIN && eventUserPostDTO.getEventUserRole() == EventUserRole.COLLABORATOR) {
                    eventUser.setRole(eventUserPostDTO.getEventUserRole());
                    done = true;
                }
            }
            if (eventUserPostDTO.getEventUserStatus() != null && eventUserPostDTO.getEventUserStatus() != EventUserStatus.INVITED) {
                eventUser.setStatus(eventUserPostDTO.getEventUserStatus());
                done = true;
            }
            if (!done) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is not authorized for this action");
            }
        } else {
            // Check admin role
            List<EventUser> eventUsers = event.getEventUsers();
            boolean thrower = true;
            for (EventUser ev2 : eventUsers) {
                if (ev2.getUser().getId().equals(tokenUser.getId()) && ev2.getRole() == EventUserRole.ADMIN) {
                    thrower = false;
                    break;
                }
            }
            if (thrower) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is not authorized for this action");
            }
            // Admin validated
            if (eventUserPostDTO.getEventUserRole() != null) {
                eventUser.setRole(eventUserPostDTO.getEventUserRole());
            }
            if (eventUserPostDTO.getEventUserStatus() != null) {
                eventUser.setStatus(eventUserPostDTO.getEventUserStatus());
            }
        }
        eventUserService.updateRepository(eventUser);
        return eventUser;
    }

    public void linkEventUsertoEvent(Event createdEvent, EventUser admin) {
        createdEvent.addEventUsers(admin);
        Event event = updateRepository(createdEvent);
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


    /**
     * get all tasks according to event ID, connects only to eventTask repository
     */
    public List<EventTask> getTasksByEventID(Long eventID){
        return eventTaskRepository.findAllByEvent_id(eventID);
    }

    /**
     * add task to event, checks also if event exists
     */
    public void addTask(Long eventID, EventTask task){
        if(!eventRepository.existsById(eventID)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event with this ID does not exists");
        }
        Event e = eventRepository.findById(eventID).orElse(null);
        task.setEvent(e);
        eventTaskRepository.save(task);
    }

    public void updateTask(Long taskID, EventTask newTaskData){
        EventTask task = eventTaskRepository.getOne(taskID);

        if(newTaskData.getUser() != null){
            task.setUser(userService.getUserByIDNum(newTaskData.getUser().getId()));
        }

        if(newTaskData.getDescription() != null){
            task.setDescription(newTaskData.getDescription());
        }

    }


    public List<User> getUsers(Event event) {
        List<EventUser> eventUsers = event.getEventUsers();
        List<User> users = new ArrayList<>();
        for (EventUser eventUser:eventUsers){
            users.add(eventUser.getUser());
        }
        return users;
    }

    public List<EventUser> getEventUsers(Event event) {
        return event.getEventUsers();
    }


    public void updateEvent(Event event, User user, Event eventInput) {
        isUserAloudToUpdate(event, user);
        if(eventInput.getTitle() != null){
            event.setTitle(eventInput.getTitle());
        }/**else if (eventInput.getType() != null){ //Type should not be able to be changed, or should it?
            event.setType(eventInput.getType());
        }*/if (eventInput.getDescription() != null){
            event.setDescription(eventInput.getDescription());
        } if (eventInput.getEventDate() != null){
            event.setEventDate(eventInput.getEventDate());
        } if (eventInput.getEventLocation() != null){
            event.setEventLocation(eventInput.getEventLocation());
        }
        Event updatedEvent = updateRepository(event);
    }

    public void isUserAloudToUpdate(Event event, User user) {
        List<EventUser> eventUsers = event.getEventUsers();

        for (EventUser eventUser : eventUsers){
            if(user.getId().equals(eventUser.getUser().getId()) && eventUser.getRole() ==EventUserRole.ADMIN){
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is unauthorized to update event");
    }

    public void linkImageToEvent(Event event, String createRandomName) {
        event.setPicture(createRandomName);
        updateRepository(event);
    }
}
