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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements RequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestMapper participationRequestMapper;

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event not found");
        }

        return participationRequestRepository.findByEventId(eventId).stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event not found");
        }

        List<Long> requestIds = new ArrayList<>(updateRequest.getRequestIds());
        List<ParticipationRequest> requests = participationRequestRepository.findByIdIn(requestIds);

        requests.forEach(request -> {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Request must be in PENDING state");
            }
        });

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        if (updateRequest.getStatus() == RequestStatus.CONFIRMED) {
            Long confirmedCount = participationRequestRepository.countConfirmedRequestsByEventId(eventId);
            List<Long> confirmedRequestIds = new ArrayList<>();
            List<Long> rejectedRequestIds = new ArrayList<>();

            for (ParticipationRequest request : requests) {
                if (event.getParticipantLimit() == 0 || !event.getRequestModeration() || confirmedCount < event.getParticipantLimit()) {
                    confirmedRequestIds.add(request.getId());
                    confirmedCount++;
                } else {
                    rejectedRequestIds.add(request.getId());
                }
            }

            participationRequestRepository.updateStatusForRequests(confirmedRequestIds, RequestStatus.CONFIRMED);
            participationRequestRepository.updateStatusForRequests(rejectedRequestIds, RequestStatus.REJECTED);

            confirmedRequests.addAll(toListOfParticipationRequestDtos(participationRequestRepository.findByIdIn(confirmedRequestIds)));

            rejectedRequests.addAll(toListOfParticipationRequestDtos(participationRequestRepository.findByIdIn(rejectedRequestIds)));

            if (confirmedCount >= event.getParticipantLimit()) {
                participationRequestRepository.updateStatusForPendingRequests(eventId, RequestStatus.PENDING, RequestStatus.REJECTED);
                List<ParticipationRequest> newlyRejectedRequests = participationRequestRepository
                        .findByEventIdAndStatus(eventId, RequestStatus.REJECTED);

                rejectedRequests.addAll(toListOfParticipationRequestDtos(newlyRejectedRequests));
            }
        } else if (updateRequest.getStatus().equals(RequestStatus.REJECTED)) {
            participationRequestRepository.updateStatusForRequests(requestIds, RequestStatus.REJECTED);
            rejectedRequests.addAll(toListOfParticipationRequestDtos(requests));
        }

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        return participationRequestRepository.findByRequesterId(userId).stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (participationRequestRepository.findByEventAndRequester(event, user).isPresent()) {
            throw new ConflictException("Request already exists");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot request participation in their own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event must be published");
        }

        Long confirmedCount = participationRequestRepository.countConfirmedRequestsByEventId(eventId);
        if (event.getParticipantLimit() > 0 && confirmedCount >= event.getParticipantLimit()) {
            throw new ConflictException("Participant limit reached");
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

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        ParticipationRequest request = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Request not found");
        }

        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest savedRequest = participationRequestRepository.save(request);
        return participationRequestMapper.toParticipationRequestDto(savedRequest);
    }

    private List<ParticipationRequestDto> toListOfParticipationRequestDtos(List<ParticipationRequest> requests) {
        return requests
                .stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .toList();
    }
}