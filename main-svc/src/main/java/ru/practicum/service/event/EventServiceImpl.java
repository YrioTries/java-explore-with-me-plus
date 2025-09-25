package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.enums.State;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    public List<EventShortDto> getAllEventsByPublicUsers(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            int from,
            int size) {
        return List.of();
    }

    @Override
    public EventFullDto getEventByIdByPublicUser(Long id) {
        log.info("Сервис получил запрос на получение ивента неавторизованным пользователем");
        Event event = eventRepository.findEventByIdAndState(id, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Ивент с id " + id + " не найден"));
        log.info("Ивент получен из БД и передаётся в контроллер...");
        // здесь должен быть метод, который проставляет ip в поле views
        return eventMapper.toEventFullDto(event);
    }
}
