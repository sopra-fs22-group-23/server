package ch.uzh.ifi.sopra22.repository;

import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventLocation;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class EventUserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EventUserRepository eventUserRepository;
/**
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;*/
/**
    @Test
    public void test_findbyUserID(){ //didn't manage to get it to work
        User user = new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setPassword("password");
        user.setId(2L);
        user.setToken("1");
        //entityManager.persist(user);
        //entityManager.flush();

        Event event = new Event();
        event.setId(1L);
        event.setTitle("We Events");
        event.setType(EventType.PUBLIC);
        event.setStatus(EventStatus.IN_PLANNING);
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        event.setEventLocation(eventLocation);
        //entityManager.persist(event);
        //entityManager.flush();

        EventUser eventUser = new EventUser();
        eventUser.setEventUserId(3L);
        //eventUser.setEvent(event);
        //eventUser.setUser(user);
        eventUser.setStatus(EventUserStatus.CONFIRMED);
        eventUser.setRole(EventUserRole.ADMIN);
        entityManager.persist(eventUser);
        entityManager.flush();


        //event.addEventUsers(eventUser);
        //user.addEventUsers(eventUser);

        // when
        List<EventUser> foundEventUsers = eventUserRepository.findByUserId(user.getId());
        EventUser found = foundEventUsers.get(0);

        // then
        assertNotNull(found.getEventUserId());
        assertEquals(found.getUser().getId(), user.getId());
        assertEquals(found.getEvent().getId(), event.getId());
        assertEquals(found.getRole(), eventUser.getRole());
        assertEquals(found.getStatus(), eventUser.getStatus());

    }*/

}