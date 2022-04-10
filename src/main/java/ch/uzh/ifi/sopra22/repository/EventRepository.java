package ch.uzh.ifi.sopra22.repository;

import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.entity.*;
import ch.uzh.ifi.sopra22.entity.Event;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    //Event findByTitle(String title);

    //Event findByEventLocation(EventLocation location);

    //Event findByEventDate(Date date);

    List<Event> findByType(EventType eventType);



    //List<Event> findByEventUsersContaining(EventUser eventUsers);
    ;
}
