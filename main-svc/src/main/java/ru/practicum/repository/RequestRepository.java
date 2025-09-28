package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.enums.Status;
import ru.practicum.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByEventId(Long eventId);

    Optional<Request> findByEventIdAndId(Long eventId, Long id);

    int countByEventIdAndStatus(Long eventId, Status status);

    List<Request> findAllByEventIdInAndStatus(List<Long> eventIds, Status status);

    Boolean existsByEventIdAndRequesterId(Long eventId, Long userId);

    Optional<Request> findByIdAndRequesterId(Long id, Long requesterId);

    List<Request> findAllByRequesterId(Long userId);

    Optional<List<Request>> findByEventIdAndIdIn(Long eventId, List<Long> id);

}
