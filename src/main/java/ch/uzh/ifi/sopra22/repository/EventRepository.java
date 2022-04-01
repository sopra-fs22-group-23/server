package ch.uzh.ifi.sopra22.repository;

import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.Event;
import ch.uzh.ifi.sopra22.entity.EventLocation;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Event findByTitle(String title);

    Event findByLocation(EventLocation location);

    Event findByEventDate(Date date);
}
