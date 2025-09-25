package ru.practicum.controller.privates;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.request.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestPrivateController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getParticipationRequests(
            @PositiveOrZero @PathVariable Long userId) {
        log.info("В private-контроллер пришёл запрос на получение юзером с id {} списка заявок на участие", userId);
        List<ParticipationRequestDto> participationRequests = requestService.getParticipationRequests(userId);
        log.info("Возвращаем список заявок юзера. Размер списка: {}", participationRequests.size());
        return participationRequests;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createParticipationRequest(
            @PositiveOrZero @PathVariable Long userId,
            @PositiveOrZero @RequestParam Long eventId) {
        log.info("В private-контроллер пришёл запрос, что юзер хочет поучаствовать в ивенте другого юзера");
        log.info("Id юзера = {}, id ивента = {}", userId, eventId);
        ParticipationRequestDto participationRequest = requestService.createParticipationRequest(userId, eventId);
        log.info("Возвращаем данные юзеру о созданной им заявке на участие");
        return participationRequest;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(
            @PositiveOrZero @PathVariable Long userId,
            @PositiveOrZero @PathVariable Long requestId) {
        log.info("В private-контроллер пришёл запрос, что юзер хочет отменить заявку на участие в ивенте");
        log.info("Id юзера = {}, id заявки = {}", userId, requestId);
        ParticipationRequestDto canceledRequest = requestService.cancelParticipationRequest(userId, requestId);
        log.info("Возвращаем данные юзеру об отменённоё им заявке на участие");
        return canceledRequest;
    }
}
