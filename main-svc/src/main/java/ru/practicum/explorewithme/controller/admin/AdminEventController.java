package ru.practicum.explorewithme.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.request.UpdateEventAdminRequest;
import ru.practicum.explorewithme.enums.State;
import ru.practicum.explorewithme.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventsByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<State> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("В контроллер админа пришёл запрос на получение списка ивентов");
        List<EventFullDto> eventFullDtos = eventService.getEventsByAdmin(users, states, categories, rangeStart,
                rangeEnd, from, size);
        log.info("Возвращаем список ивентов админу. Размер списка: {}", eventFullDtos.size());
        return eventFullDtos;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PositiveOrZero @PathVariable Long eventId,
                                           @Valid @RequestBody UpdateEventAdminRequest eventAdminRequest) {
        log.info("В контроллер админа пришёл запрос на редактирование ивента");
        log.info("Id ивента: {}", eventId);
        EventFullDto eventFullDto = eventService.updateEventOfAdmin(eventId, eventAdminRequest);
        log.info("Возвращаем данные о только что отредактированном ивенте");
        return eventFullDto;
    }
}
