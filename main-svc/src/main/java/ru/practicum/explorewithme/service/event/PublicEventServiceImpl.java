package ru.practicum.explorewithme.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.enums.EventState;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.ParticipationRequestRepository;
import ru.practicum.explorewithme.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatsService statsService;

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable, String sort, Pageable pageable) {
        if (rangeStart == null) rangeStart = LocalDateTime.now();
        if (rangeEnd == null) rangeEnd = LocalDateTime.now().plusYears(100);

        List<Event> events = eventRepository.findEventsPublic(text, categories, paid, rangeStart, rangeEnd, pageable)
                .getContent();

        if (Boolean.TRUE.equals(onlyAvailable)) {
            events = events.stream()
                    .filter(event -> {
                        Long confirmedRequests = participationRequestRepository.countConfirmedRequestsByEventId(event.getId());
                        return event.getParticipantLimit() == 0 || confirmedRequests < event.getParticipantLimit();
                    })
                    .collect(Collectors.toList());
        }

        if ("VIEWS".equals(sort)) {
            events.sort((e1, e2) -> Long.compare(e2.getViews(), e1.getViews()));
        } else {
            events.sort((e1, e2) -> e2.getEventDate().compareTo(e1.getEventDate()));
        }

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event not found");
        }

        event.setViews(event.getViews() + 1);
        Event updatedEvent = eventRepository.save(event);

        statsService.recordEventView(id);

        return eventMapper.toEventFullDto(updatedEvent);
    }
}