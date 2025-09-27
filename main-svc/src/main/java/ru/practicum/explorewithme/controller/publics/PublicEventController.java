package ru.practicum.explorewithme.controller.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.service.event.PublicEventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final PublicEventService publicEventService;

    @GetMapping
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return publicEventService.getEventsPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, PageRequest.of(from / size, size));
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long id) {
        return publicEventService.getEventById(id);
    }
}