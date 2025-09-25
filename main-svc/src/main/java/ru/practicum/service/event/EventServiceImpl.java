package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.UpdateEventAdminRequest;
import ru.practicum.dto.request.UpdateEventUserRequest;
import ru.practicum.enums.State;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

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

    @Override
    @Transactional
    public EventFullDto addNewEvent(Long userId, NewEventDto dto) {
        log.info("Сервис получил запрос на создание ивента");
        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Некорректная дата ивента: {}", dto.getEventDate());
            throw new ValidationException("Дата ивента не может быть раньше, чем через 2 часа от текущего момента");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Юзера с id " + userId + " нет в БД"));
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категории с id " + dto.getCategory() + " нет в БД"));
        Event event = eventMapper.toEvent(dto);
        event.setLat(dto.getLocation().getLat());
        event.setLon(dto.getLocation().getLon());
        event.setState(State.PENDING);
        event.setCategory(category);
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        Event savedEvent = eventRepository.save(event);
        log.info("Ивент успешно сохранён в БД. Его id = {}", savedEvent.getId());
        return eventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatusToUserEvent(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        return null;
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        return null;
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<State> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        return List.of();
    }

    @Override
    public EventFullDto updateEventOfAdmin(Long eventId, UpdateEventAdminRequest eventAdminRequest) {
        return null;
    }

    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest eventUserRequest) {
        return null;
    }

    @Override
    public List<EventFullDto> getAllUserEvents(Long userId, Integer from, Integer size) {
        return List.of();
    }

    @Override
    public List<ParticipationRequestDto> getRequestsToUserEvent(Long userId, Long eventId) {
        return List.of();
    }
}
