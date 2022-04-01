package ch.uzh.ifi.sopra22.service;

import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.EventUserId;
import ch.uzh.ifi.sopra22.entity.User;
import ch.uzh.ifi.sopra22.repository.EventUserRepository;
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
import java.util.Optional;



@Service
@Transactional
public class EventUserService {

    private final Logger log = LoggerFactory.getLogger(EventUserService.class);

    private final EventUserRepository eventUserRepository;

    private final UserService userService;

    @Autowired
    public EventUserService(@Qualifier("eventUserRepository") EventUserRepository eventUserRepository, UserService userService) {
        this.eventUserRepository = eventUserRepository;
        this.userService = userService;
    }

    private List<EventUser> getAllEventUsers() {
        return this.eventUserRepository.findAll();
    }

    private void updateRepository(EventUser eventUser) {
        eventUserRepository.save(eventUser);
        eventUserRepository.flush();
    }

    private void checkIfEventUserExists(EventUser eventUserToBe) {
        EventUserId newId = new EventUserId(eventUserToBe.getUserId(), eventUserToBe.getEventId());
        Optional<EventUser> eventUserById = eventUserRepository.findById(newId);
        String baseErrorMessage = "The EventUser provided is not unique. Therefore, the EventUser could not be created!";

        if (eventUserById.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
        }
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
        updateRepository(newEventUser);

        return newEventUser;
    }

    private List<Long> getEventIDNums(Long userId) {
        List<EventUser> eventUsers = getAllEventUsers();
        List<Long> eventIds = new ArrayList<>();

        for (int i=0; i < eventUsers.size(); i++) {
            if (userId.equals(eventUsers.get(i).getUserId())) {
                eventIds.add(eventUsers.get(i).getEventId());
            }
        }

        return eventIds;
    }

    public List<Long> getEventIdsFromToken(String token) {
        User user = userService.getUserByToken(token);
        Long userId = user.getId();
        return getEventIDNums(userId);
    }
}
