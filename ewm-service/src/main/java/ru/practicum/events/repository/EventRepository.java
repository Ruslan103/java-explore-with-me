package ru.practicum.events.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.StateEvent;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Boolean findEventByCategoryId(long catId);

    List<Event> findEventByInitiator(Long userId, PageRequest page);

    List<Event> findEventsByIdAndInitiator(Long eventId, Long initiatorId);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.initiator.id IN ?1 " +
            "AND e.state IN ?2 " +
            "AND e.category.id IN ?3 " +
            "AND e.eventDate >= ?4 " +
            "AND e.eventDate <= ?5")
    List<Event> getEventsAdmin(List<Long> usersId,
                               List<StateEvent> states,
                               List<Long> categories,
                               LocalDateTime rangeStart,
                               LocalDateTime rangeEnd,
                               PageRequest page);


    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE UPPER(e.annotation) LIKE UPPER(?1) " +
            "OR UPPER(e.description) LIKE UPPER(?1) " +
            "OR UPPER(e.title) LIKE UPPER(?1) " +
            "AND e.category.id IN ?2 " +
            "AND e.paid = ?3 " +
            "AND e.eventDate >= ?4 " +
            "AND e.eventDate <= ?5 " +
            "AND (false = ?6 OR (true = ?6 and e.participantLimit > 0))")
    List<Event> getEventsPublic(String text,
                                List<Long> categories,
                                Boolean paid,
                                LocalDateTime rangeStart,
                                LocalDateTime rangeEnd,
                                Boolean onlyAvailable,
                                PageRequest pageRequest);
}
