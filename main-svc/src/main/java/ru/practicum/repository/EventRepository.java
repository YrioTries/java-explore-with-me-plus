package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.enums.State;
import ru.practicum.model.Event;
import ru.practicum.model.Request;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findEventByIdAndState(Long id, State state);

    Set<Event> findAllByIdIn(Set<Long> events);

}
