package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventTask;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.repository.EventUserRepository;
import ch.uzh.ifi.sopra22.rest.dto.UserEventGetDTO;
import ch.uzh.ifi.sopra22.rest.mapper.EventDTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Transactional
public class EventUserService {

    private final Logger log = LoggerFactory.getLogger(EventUserService.class);

    private final EventUserRepository eventUserRepository;

    private final UserService userService;

    @Autowired
    public EventUserService(@Qualifier("eventUserRepository") EventUserRepository eventUserRepository, @Qualifier("userService") UserService userService) {
        this.eventUserRepository = eventUserRepository;
        this.userService = userService;
    }

    public List<EventUser> getAllEventUsers() {
        return this.eventUserRepository.findAll();
    }

    public EventUser updateRepository(EventUser eventUser) {
        EventUser savedEventUser = eventUserRepository.save(eventUser);
        eventUserRepository.flush();
        return savedEventUser;
    }

    private void checkIfEventUserExists(EventUser eventUserToBe) {
        List<EventUser> eventUsersByUserId = eventUserRepository.findByUserId(eventUserToBe.getUser().getId());
        List<Long> eventUsersEventIds = new ArrayList<>();
        for (EventUser eventUser : eventUsersByUserId) {
            eventUsersEventIds.add(eventUser.getEvent().getId());
        }

        String baseErrorMessage = "This user has already been signed up or invited";

        if (eventUsersEventIds.contains(eventUserToBe.getEvent().getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
        }
    }

    public EventUser ensureEventUserExists(Long eventId, Long userId) {
        List<EventUser> eventUsersByUserId = eventUserRepository.findByUserId(userId);

        EventUser eventUser = null;
        for (EventUser ev : eventUsersByUserId) {
            if (ev.getEvent().getId().equals(eventId)) {
                eventUser = ev;
                break;
            }
        }

        // Check existence
        if (eventUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This signup does not exist and hence cannot be manipulated");
        }
        return eventUser;
    }

    public EventUser createEventUser(EventUser newEventUser) {
        checkIfEventUserExists(newEventUser);

        if (newEventUser.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status attribute has to be defined!");
        }
        if (newEventUser.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role attribute has to be defined!");
        }

        newEventUser.setCreationDate(new Date(System.currentTimeMillis()));
        EventUser savedEventUser = updateRepository(newEventUser);

        return savedEventUser;
    }

    public List<Long> getEventIDNums(Long userId) {
        List<EventUser> eventUsers = getAllEventUsers();
        List<Long> eventIds = new ArrayList<>();

        for (int i=0; i < eventUsers.size(); i++) {
            if (userId.equals(eventUsers.get(i).getUser().getId())) {
                eventIds.add(eventUsers.get(i).getEvent().getId());
            }
        }

        return eventIds;
    }

    /**
     * return all event ID's that are connected to the user, find out by token
     */
    public List<Long> getEventIdsFromToken(String token) {
        User user = userService.getUserByToken(token);
        Long userId = user.getId();
        return getEventIDNums(userId);
    }

    public List<EventUser> getEventUsers(User user) {
        List<EventUser> eventUsers = eventUserRepository.findByUserId(user.getId());
        if (eventUsers == null) {
            eventUsers = new ArrayList<>();
            return eventUsers;
        }
        return eventUsers;
    }

    // Test
    public boolean canUserAccessEvent(User user, Long eventID, EventUserRole role){
        //TODO check if event exists
        return eventUserRepository.existsByUserAndEvent_idAndRole(user, eventID, role);
    }

    public List<EventTask> getUserTasks(List<EventTask> tasks, Long userID) {
        List<EventTask> userTask = new ArrayList<>();
        for ( EventTask task : tasks){
            if (task.getUser().getId() == userID){
                userTask.add(task);
            }
        }
        return userTask;
    }
}
