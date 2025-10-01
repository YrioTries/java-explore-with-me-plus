package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.enums.RequestStatus;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.ParticipationRequest;
import ru.practicum.explorewithme.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByRequesterId(Long requesterId);

    List<ParticipationRequest> findByEventId(Long eventId);

    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long requesterId);

    Optional<ParticipationRequest> findByEventAndRequester(Event event, User requester);

    List<ParticipationRequest> findByIdIn(List<Long> requestIds);

    @Query("SELECT COUNT(pr) FROM ParticipationRequest pr WHERE pr.event.id = :eventId AND pr.status = 'CONFIRMED'")
    Long countConfirmedRequestsByEventId(@Param("eventId") Long eventId);

    List<ParticipationRequest> findByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEventIdInAndStatus(List<Long> eventIds, RequestStatus status);
}