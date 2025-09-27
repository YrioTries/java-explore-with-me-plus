package ru.practicum.explorewithme.service.event;

import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventDto;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.request.UpdateEventAdminRequest;
import ru.practicum.explorewithme.dto.request.UpdateEventUserRequest;
import ru.practicum.explorewithme.enums.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventShortDto> getAllEventsByPublicUsers(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable,
                                                  String sort,
                                                  int from,
                                                  int size);

    EventFullDto getEventByIdByPublicUser(Long id);

    List<EventFullDto> getEventsByAdmin(List<Long> users,
                                        List<State> states,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Integer from,
                                        Integer size);

    EventFullDto updateEventOfAdmin(Long eventId, UpdateEventAdminRequest eventAdminRequest);

    List<EventFullDto> getAllUserEvents(Long userId, Integer from, Integer size);

    EventFullDto addNewEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUserEvent(Long userId, Long eventId);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest eventUserRequest);

    List<ParticipationRequestDto> getRequestsToUserEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatusToUserEvent(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest request);

}
