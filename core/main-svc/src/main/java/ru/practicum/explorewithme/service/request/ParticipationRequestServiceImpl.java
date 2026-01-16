package ru.practicum.explorewithme.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.enums.EventState;
import ru.practicum.explorewithme.enums.RequestStatus;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.ParticipationRequestMapper;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.ParticipationRequest;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.ParticipationRequestRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements RequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestMapper participationRequestMapper;

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " not found for user with id=" + userId);
        }

        return participationRequestRepository.findByEventId(eventId).stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " not found for user with id=" + userId);
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Event does not require request moderation");
        }

        List<Long> requestIds = new ArrayList<>(updateRequest.getRequestIds());
        if (requestIds.isEmpty()) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }

        Map<Long, ParticipationRequest> requestsMap = participationRequestRepository.findByIdIn(requestIds)
                .stream()
                .collect(Collectors.toMap(ParticipationRequest::getId, Function.identity()));

        validateAllRequestsFound(requestIds, requestsMap);

        validateRequestsMap(requestsMap, eventId);

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        RequestStatus status = updateRequest.getStatus();
        if (status == RequestStatus.CONFIRMED) {
            processConfirmationWithMap(event, requestsMap, requestIds, confirmedRequests, rejectedRequests);
        } else if (status == RequestStatus.REJECTED) {
            processRejectionWithMap(requestsMap, requestIds, rejectedRequests);
        }

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private void validateAllRequestsFound(List<Long> requestIds, Map<Long, ParticipationRequest> requestsMap) {
        if (requestsMap.size() != requestIds.size()) {
            List<Long> foundIds = new ArrayList<>(requestsMap.keySet());
            List<Long> missingIds = requestIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());
            throw new NotFoundException("Requests not found with ids: " + missingIds);
        }
    }

    private void validateRequestsMap(Map<Long, ParticipationRequest> requestsMap, Long eventId) {
        List<String> errors = new ArrayList<>();

        for (ParticipationRequest request : requestsMap.values()) {
            if (!request.getEvent().getId().equals(eventId)) {
                errors.add("Request with id=" + request.getId() + " does not belong to event with id=" + eventId);
            }
            if (request.getStatus() != RequestStatus.PENDING) {
                errors.add("Request with id=" + request.getId() + " must be in PENDING state");
            }
        }

        if (!errors.isEmpty()) {
            throw new ConflictException(String.join("; ", errors));
        }
    }

    private void processConfirmationWithMap(Event event, Map<Long, ParticipationRequest> requestsMap,
                                            List<Long> requestIds, List<ParticipationRequestDto> confirmedRequests,
                                            List<ParticipationRequestDto> rejectedRequests) {
        Long confirmedCount = participationRequestRepository.countConfirmedRequestsByEventId(event.getId());
        int participantLimit = event.getParticipantLimit();
        int availableSlots = participantLimit - confirmedCount.intValue();

        List<ParticipationRequest> requestsToUpdate = new ArrayList<>();
        List<ParticipationRequest> requestsToReject = new ArrayList<>();

        for (int i = 0; i < requestIds.size(); i++) {
            Long requestId = requestIds.get(i);
            ParticipationRequest request = requestsMap.get(requestId);

            if (i < availableSlots) {
                request.setStatus(RequestStatus.CONFIRMED);
                requestsToUpdate.add(request);
            } else {
                request.setStatus(RequestStatus.REJECTED);
                requestsToReject.add(request);
            }
        }

        saveAndMapResults(requestsToUpdate, requestsToReject, confirmedRequests, rejectedRequests);

        if (availableSlots <= requestsToUpdate.size()) {
            rejectAllPendingRequests(event.getId(), rejectedRequests);
        }
    }

    private void processRejectionWithMap(Map<Long, ParticipationRequest> requestsMap,
                                         List<Long> requestIds, List<ParticipationRequestDto> rejectedRequests) {
        List<ParticipationRequest> requestsToReject = requestIds.stream()
                .map(requestsMap::get)
                .collect(Collectors.toList());

        requestsToReject.forEach(request -> request.setStatus(RequestStatus.REJECTED));
        List<ParticipationRequest> savedRequests = participationRequestRepository.saveAll(requestsToReject);

        rejectedRequests.addAll(savedRequests.stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList()));
    }

    private void saveAndMapResults(List<ParticipationRequest> requestsToUpdate,
                                   List<ParticipationRequest> requestsToReject,
                                   List<ParticipationRequestDto> confirmedRequests,
                                   List<ParticipationRequestDto> rejectedRequests) {
        if (!requestsToUpdate.isEmpty()) {
            List<ParticipationRequest> savedConfirmed = participationRequestRepository.saveAll(requestsToUpdate);
            confirmedRequests.addAll(savedConfirmed.stream()
                    .map(participationRequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList()));
        }

        if (!requestsToReject.isEmpty()) {
            List<ParticipationRequest> savedRejected = participationRequestRepository.saveAll(requestsToReject);
            rejectedRequests.addAll(savedRejected.stream()
                    .map(participationRequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList()));
        }
    }

    private void rejectAllPendingRequests(Long eventId, List<ParticipationRequestDto> rejectedRequests) {
        List<ParticipationRequest> pendingRequests = participationRequestRepository
                .findByEventIdAndStatus(eventId, RequestStatus.PENDING);

        if (!pendingRequests.isEmpty()) {
            Map<Long, ParticipationRequest> pendingMap = pendingRequests.stream()
                    .collect(Collectors.toMap(ParticipationRequest::getId, Function.identity()));

            pendingMap.values().forEach(request -> request.setStatus(RequestStatus.REJECTED));
            List<ParticipationRequest> savedRequests = participationRequestRepository.saveAll(pendingMap.values());

            rejectedRequests.addAll(savedRequests.stream()
                    .map(participationRequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList()));
        }
    }


    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }

        return participationRequestRepository.findByRequesterId(userId).stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        participationRequestRepository.findByEventAndRequester(event, user)
                .ifPresent(existingRequest -> {
                    throw new ConflictException("Request from user with id=" + userId +
                            " to event with id=" + eventId + " already exists");
                });

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot request participation in their own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Cannot participate in unpublished event");
        }

        if (event.getParticipantLimit() > 0) {
            Long confirmedCount = participationRequestRepository.countConfirmedRequestsByEventId(eventId);
            if (confirmedCount >= event.getParticipantLimit()) {
                throw new ConflictException("Participant limit reached for event with id=" + eventId);
            }
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        ParticipationRequest savedRequest = participationRequestRepository.save(request);
        return participationRequestMapper.toParticipationRequestDto(savedRequest);
    }


    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

        ParticipationRequest request = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Request with id=" + requestId +
                    " not found for user with id=" + userId);
        }

        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest savedRequest = participationRequestRepository.save(request);
        return participationRequestMapper.toParticipationRequestDto(savedRequest);
    }
}