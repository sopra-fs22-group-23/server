package ch.uzh.ifi.sopra22.repository;

import java.util.List;
import ch.uzh.ifi.sopra22.entity.EventUser;
import ch.uzh.ifi.sopra22.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EventUserRepository extends JpaRepository<EventUser, Long> {

    List<EventUser> findByUserId(Long userId);

    List<EventUser> findByUser(User user);

    List<EventUser> findByEventId(Long eventId);


}
