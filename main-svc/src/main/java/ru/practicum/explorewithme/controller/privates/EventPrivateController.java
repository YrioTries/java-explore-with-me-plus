package ru.practicum.explorewithme.controller.privates;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.NewEventDto;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.request.UpdateEventUserRequest;
import ru.practicum.explorewithme.service.event.EventService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAllUserEvents(
            @PositiveOrZero @PathVariable Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("В private-контроллер пришёл запрос на получение ивентов юзера с id {}", userId);
        List<EventFullDto> eventFullDtos = eventService.getAllUserEvents(userId, from, size);
        log.info("Возвращаем список ивентов юзера. Размер списка: {}", eventFullDtos.size());
        return eventFullDtos;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addNewEvent(@PositiveOrZero @PathVariable Long userId,
                                    @RequestBody @Valid NewEventDto newEventDto) {
        log.info("В private-контроллер пришёл запрос на создание ивента юзером с id {}", userId);
        EventFullDto newEvent = eventService.addNewEvent(userId, newEventDto);
        log.info("Возвращаем данные клиенту о только что созданном ивенте");
        return newEvent;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(@PositiveOrZero @PathVariable Long userId,
                                       @PositiveOrZero @PathVariable Long eventId) {
        log.info("В private-контроллер пришёл запрос на получение ивента юзером");
        log.info("Id ивента = {}, id юзера = {}", eventId, userId);
        EventFullDto eventFullDto = eventService.getUserEvent(userId, eventId);
        log.info("Возвращаем данные клиенту о конкретном ивенте юзера");
        return eventFullDto;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateUserEvent(@Valid @RequestBody UpdateEventUserRequest updateRequest,
                                          @PositiveOrZero @PathVariable Long userId,
                                          @PositiveOrZero @PathVariable Long eventId) {
        log.info("В private-контроллер пришёл запрос на редактирование ивента юзером");
        log.info("Id ивента = {}, id юзера = {}", eventId, userId);
        EventFullDto updatedUserEvent = eventService.updateUserEvent(userId, eventId, updateRequest);
        log.info("Возвращаем данные клиенту об отредактированном ивенте юзера");
        return updatedUserEvent;
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsToUserEvent(@PositiveOrZero @PathVariable Long userId,
                                                                @PositiveOrZero @PathVariable Long eventId) {
        log.info("В private-контроллер пришёл запрос на просмотр заявок к ивенту юзера");
        log.info("Id ивента = {}, id юзера = {}", eventId, userId);
        List<ParticipationRequestDto> participationRequests = eventService.getRequestsToUserEvent(userId, eventId);
        log.info("Возвращаем данные клиенту об отредактированном ивенте юзера");
        return participationRequests;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatusToUserEvent(
            @PositiveOrZero @PathVariable Long userId,
            @PositiveOrZero @PathVariable Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("В private-контроллер пришёл запрос на изменение статуса заявок на участие в ивенте юзера");
        log.info("Id ивента = {}, id юзера = {}, заявка = {}", eventId, userId, eventRequestStatusUpdateRequest);
        EventRequestStatusUpdateResult eventRequest = eventService.updateRequestStatusToUserEvent(userId, eventId,
                eventRequestStatusUpdateRequest);
        log.info("Возвращаем отредактированную заявку к ивенту юзера");
        return eventRequest;
    }
}
