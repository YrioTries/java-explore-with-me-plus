package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.SearchEventParams;
import ru.practicum.dto.event.SearchEventParamsAdmin;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.UpdateEventAdminRequest;
import ru.practicum.dto.request.UpdateEventUserRequest;

import java.util.List;

public interface EventService {

    List<EventFullDto> getAllEventFromAdmin(SearchEventParamsAdmin searchEventParamsAdmin);

    EventFullDto updateEventFromAdmin(Long eventId, UpdateEventAdminRequest inputUpdate);

    List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto addNewEvent(Long userId, NewEventDto input);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto updateEventByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserRequest inputUpdate);

    List<ParticipationRequestDto> getAllParticipationRequestsFromEventByOwner(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatusRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest inputUpdate);

    List<EventShortDto> getAllEventFromPublic(SearchEventParams searchEventParams, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);

}
