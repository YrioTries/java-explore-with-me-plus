package ru.practicum.explorewithme.service.event;

import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {
    List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Boolean onlyAvailable, String sort, Pageable pageable);

    EventFullDto getEventById(Long id);
}