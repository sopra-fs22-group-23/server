package ch.uzh.ifi.sopra22.repository;

import ch.uzh.ifi.sopra22.entity.Event;
import java.util.List;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.EventUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EventUserRepository extends JpaRepository<EventUser, EventUserId> {
    //List<EventUser> findByIdUserId(Long userId);

    //List<EventUser> findByIdEventId(Long eventId);

    ;
}
