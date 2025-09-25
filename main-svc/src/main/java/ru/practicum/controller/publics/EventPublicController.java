package ru.practicum.controller.publics;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventPublicController {
    private final EventService eventService;
    private final StatsClient statsClient;

    @Value("${stat-svc.url}")
    private String appName;

    @GetMapping
    public List<EventShortDto> getAllEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0")Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info("В контроллер пришёл запрос на получение списка ивентов");
        List<EventShortDto> eventShortDtos = eventService.getAllEventsByPublicUsers(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
        log.info("Возвращаем список ивентов клиенту. Размер списка: {}", eventShortDtos.size());
        statsClient.hit(new EndpointHitDto(appName, request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now()));
        return eventShortDtos;
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable @PositiveOrZero Long id,
                                     HttpServletRequest request) throws JsonProcessingException {
        log.info("В контроллер пришёл запрос на получение ивента");
        log.info("Id запрашиваемого ивента: {}", id);
        EventFullDto eventFullDto = eventService.getEventByIdByPublicUser(id);
        log.info("Возвращаем данные об ивенте клиенту");
        statsClient.hit(new EndpointHitDto(appName, request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now()));
        return eventFullDto;
    }
}
