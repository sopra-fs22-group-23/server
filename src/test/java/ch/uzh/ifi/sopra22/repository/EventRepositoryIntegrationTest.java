package ch.uzh.ifi.sopra22.repository;
import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.Event.GameMode;
import ch.uzh.ifi.sopra22.constants.UserStatus;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventLocation;
import ch.uzh.ifi.sopra22.entity.User;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class EventRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;


    @Test
    public void findByTitle_success() {
        //given
        Event event = new Event();
        event.setTitle("We Events");
        event.setType(EventType.PUBLIC);
        event.setStatus(EventStatus.IN_PLANNING);
        event.setGameMode(GameMode.OFF);
        EventLocation eventLocation = new EventLocation();
        eventLocation.setName("Zurich");
        eventLocation.setLatitude(1.02F);
        eventLocation.setLongitude(1.02F);
        event.setEventLocation(eventLocation);

        entityManager.persist(event);
        entityManager.flush();

        // when
        List<Event> foundEvents = eventRepository.findByType(event.getType());
        Event found = foundEvents.get(0);

        // then
        assertNotNull(found.getId());
        assertEquals(found.getTitle(), event.getTitle());
        assertEquals(found.getType(), event.getType());
        assertEquals(found.getStatus(), event.getStatus());
        assertEquals(found.getEventDate(), event.getEventDate());
        assertEquals(found.getStatus(), event.getStatus());
        assertEquals(found.getEventLocation(), event.getEventLocation());
        assertEquals(found.getGameMode(),event.getGameMode());
    }

}