package ru.practicum.events.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.StateEvent;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e " +
            "from Event AS e " +
            "JOIN FETCH e.initiator " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "where e.initiator.id = :userId"
    )
    List<Event> getEventsByUser(Long userId, PageRequest page);

    @Query("select e " +
            "from Event AS e " +
            "JOIN FETCH e.initiator " +
            "JOIN FETCH e.category " +
            "WHERE (cast(:rangeStart as timestamp) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (cast(:rangeEnd as timestamp) IS NULL OR e.eventDate <= :rangeEnd) " +
            "and (:users is null or e.initiator.id in :users) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:states is null or e.state in :states)")
    List<Event> findAllAdmin(@Param("users") List<Long> users,
                             @Param("states") List<StateEvent> states,
                             @Param("categories") List<Long> categories,
                             @Param("rangeStart") LocalDateTime rangeStart,
                             @Param("rangeEnd") LocalDateTime rangeEnd,
                             PageRequest page);

    @Query("select e " +
            "from Event AS e " +
            "JOIN FETCH e.initiator " +
            "JOIN FETCH e.category " +
            "where e.state = 'PUBLISHED' " +
            "AND (cast(:rangeStart as timestamp) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (cast(:rangeEnd as timestamp) IS NULL OR e.eventDate <= :rangeEnd) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:paid is null or e.paid = :paid) " +
            "and (:text is null or (upper(e.annotation) like upper(concat('%', :text, '%'))) " +
            "or (upper(e.description) like upper(concat('%', :text, '%')))" +
            "or (upper(e.title) like upper(concat('%', :text, '%'))))")
    List<Event> findAllPublishState(@Param("rangeStart") LocalDateTime rangeStart,
                                    @Param("rangeEnd") LocalDateTime rangeEnd,
                                    @Param("categories") List<Long> categories,
                                    @Param("paid") Boolean paid,
                                    @Param("text") String text,
                                    PageRequest page);

    Collection<Event> findAllByCategoryId(Long id);
}
