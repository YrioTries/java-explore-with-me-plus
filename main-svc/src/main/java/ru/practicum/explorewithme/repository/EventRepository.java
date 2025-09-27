package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.enums.EventState;
import ru.practicum.explorewithme.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    List<Event> findByCategoryId(Long categoryId);

    @Query("SELECT e FROM Event e WHERE " +
            "(:users IS NULL OR e.initiator.id IN :users) AND " +
            "(:states IS NULL OR e.state IN :states) AND " +
            "(:categories IS NULL OR e.category.id IN :categories) AND " +
            "(e.eventDate BETWEEN :rangeStart AND :rangeEnd)")
    Page<Event> findEventsByAdmin(@Param("users") List<Long> users,
                                  @Param("states") List<EventState> states,
                                  @Param("categories") List<Long> categories,
                                  @Param("rangeStart") LocalDateTime rangeStart,
                                  @Param("rangeEnd") LocalDateTime rangeEnd,
                                  Pageable pageable);

    @Query("SELECT e FROM Event e WHERE " +
            "e.state = 'PUBLISHED' AND " +
            "(:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) AND " +
            "(:categories IS NULL OR e.category.id IN :categories) AND " +
            "(:paid IS NULL OR e.paid = :paid) AND " +
            "(e.eventDate BETWEEN :rangeStart AND :rangeEnd)")
    Page<Event> findEventsPublic(@Param("text") String text,
                                 @Param("categories") List<Long> categories,
                                 @Param("paid") Boolean paid,
                                 @Param("rangeStart") LocalDateTime rangeStart,
                                 @Param("rangeEnd") LocalDateTime rangeEnd,
                                 Pageable pageable);
}