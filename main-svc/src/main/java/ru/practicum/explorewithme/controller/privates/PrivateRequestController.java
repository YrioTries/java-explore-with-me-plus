package ru.practicum.explorewithme.controller.privates;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.service.request.RequestService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final RequestService participationRequestService;

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        return participationRequestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest) {
        return participationRequestService.updateRequestStatus(userId, eventId, updateRequest);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        return participationRequestService.getUserRequests(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam Long eventId) {
        return participationRequestService.createRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        return participationRequestService.cancelRequest(userId, requestId);
    }
}