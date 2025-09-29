package ru.practicum.explorewithme.service.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatResponseDto;
import ru.practicum.StatsClient;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.enums.EventState;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ParticipationRequestRepository participationRequestRepository;

    private final StatsClient statsClient;

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, Pageable pageable,
                                               HttpServletRequest request) {
        verifyRange(rangeStart, rangeEnd);
        statsClient.hit(EndpointHitDto
                .builder()
                .app("ewm-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
        Specification<Event> spec = Specification.where(null);
        if (text != null && !text.isBlank()) spec = spec.and(searchText(text.toLowerCase()));
        if (categories != null && !categories.isEmpty()) spec = spec.and(searchCategoryIn(categories));
        spec = spec.and(searchAfterDate(rangeStart));
        if (rangeEnd != null) spec = spec.and(searchBeforeDate(rangeEnd));
        if (onlyAvailable) spec = spec.and(searchAvailable());
        spec = spec.and(searchPublished());
        List<Event> results = eventRepository.findAll(spec, pageable).toList();
        Map<Long, Long> views = getEventsViews(results);
        return results
                .stream()
                .map(eventMapper::toEventShortDto)
                .peek(dto -> dto.setViews(views.getOrDefault(dto.getId(), 0L)))
                .toList();
    }

    @Transactional
    @Override
    public EventFullDto getEventById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event not found");
        }
        statsClient.hit(EndpointHitDto
                .builder()
                .app("ewm-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
        EventFullDto eventDto = eventMapper.toEventFullDto(event);
        Map<Long, Long> views = getEventsViews(List.of(event));
        eventDto.setViews(views.getOrDefault(id, 0L));
        return eventDto;
    }

    private Map<Long, Long> getEventsViews(List<Event> events) {
        List<String> uris = events
                .stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .toList();
        LocalDateTime startDate = events
                .stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        Map<Long, Long> viewStats = new HashMap<>();
        if (startDate != null) {
            List<StatResponseDto> stats = statsClient.getStats(startDate, LocalDateTime.now(),
                    uris, true);
            viewStats = stats
                    .stream()
                    .filter(s -> s.getUri().startsWith("/events/"))
                    .collect(Collectors.toMap(s -> Long.parseLong(s.getUri().substring("/events/".length())),
                            StatResponseDto::getHits));
        }
        return viewStats;
    }

    private void verifyRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new ConflictException("Дата начала ивента не может быть позже даты окончания");
            }
        }
    }

    private Specification<Event> searchText(String text) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text + "%")
                );
    }

    private Specification<Event> searchCategoryIn(List<Long> categories) {
        return (root, query, criteriaBuilder) ->
                root.get("category").get("id").in(categories);
    }

    private Specification<Event> searchAfterDate(LocalDateTime rangeStart) {
        LocalDateTime start = Objects.requireNonNullElse(rangeStart, LocalDateTime.now());
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), start);
    }

    private Specification<Event> searchBeforeDate(LocalDateTime rangeEnd) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd);
    }

    private Specification<Event> searchAvailable() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0);
    }

    private Specification<Event> searchPublished() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED);
    }
}