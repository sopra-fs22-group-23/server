package ch.uzh.ifi.sopra22.repository;

import java.util.List;
import ch.uzh.ifi.sopra22.entity.EventUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EventUserRepository extends JpaRepository<EventUser, Long> {

    List<EventUser> findByUserId(Long userId);

    List<EventUser> findByEventId(Long eventId);


}
