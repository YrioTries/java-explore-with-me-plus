package ru.practicum.service.event;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;

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

}
