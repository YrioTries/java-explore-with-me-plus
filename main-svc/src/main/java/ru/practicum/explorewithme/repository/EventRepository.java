package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.enums.State;
import ru.practicum.explorewithme.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findEventByIdAndState(Long id, State state);

    Set<Event> findAllByIdIn(Set<Long> events);

    List<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    List<Event> findByStateAndEventDateAfter(State state, LocalDateTime eventDate, Pageable pageable);

    List<Event> findByEventDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE " +
            "e.state = 'PUBLISHED' AND " +
            "(:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) AND " +
            "(:categories IS NULL OR e.category.id IN :categories) AND " +
            "(:paid IS NULL OR e.paid = :paid) AND " +
            "e.eventDate BETWEEN :rangeStart AND :rangeEnd")
    List<Event> findPublicEvents(@Param("text") String text,
                                 @Param("categories") List<Long> categories,
                                 @Param("paid") Boolean paid,
                                 @Param("rangeStart") LocalDateTime rangeStart,
                                 @Param("rangeEnd") LocalDateTime rangeEnd,
                                 Pageable pageable);

    boolean existsByIdAndInitiatorId(Long eventId, Long initiatorId);

    List<Event> findByCategoryId(Long categoryId);
}