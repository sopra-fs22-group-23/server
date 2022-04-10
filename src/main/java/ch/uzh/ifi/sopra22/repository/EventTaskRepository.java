package ch.uzh.ifi.sopra22.repository;

import ch.uzh.ifi.sopra22.entity.EventTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventTaskRepository extends JpaRepository<EventTask, Long> {
    List<EventTask> findAllByEvent_id(Long eventID);
}
