package ch.uzh.ifi.sopra22.repository;

import ch.uzh.ifi.sopra22.entity.EventChatMessage;
import ch.uzh.ifi.sopra22.entity.EventTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventChatMessageRepository extends JpaRepository<EventChatMessage, Long> {
    List<EventChatMessage> findAllByEvent_id(Long eventID);
}
