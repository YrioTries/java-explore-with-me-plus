package ru.practicum.explorewithme.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventDto;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.request.UpdateEventAdminRequest;
import ru.practicum.explorewithme.dto.request.UpdateEventUserRequest;
import ru.practicum.explorewithme.enums.State;
import ru.practicum.explorewithme.enums.Status;
import ru.practicum.explorewithme.exceptions.NotFoundException;
import ru.practicum.explorewithme.exceptions.ValidationException;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.mapper.RequestMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.Request;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.RequestRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;

    @Override
    public List<EventShortDto> getAllEventsByPublicUsers(String text, List<Long> categories, Boolean paid,
                                                         LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                         Boolean onlyAvailable, String sort, int from, int size) {
        log.info("Сервис получил запрос на получение ивентов для публичного доступа");

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events;

        if (rangeStart == null) rangeStart = LocalDateTime.now();
        if (rangeEnd == null) rangeEnd = LocalDateTime.now().plusYears(100);

        if (rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Дата начала не может быть после даты окончания");
        }

        events = eventRepository.findByStateAndEventDateAfter(State.PUBLISHED, rangeStart, pageable);

        if (categories != null && !categories.isEmpty()) {
            events = events.stream()
                    .filter(event -> categories.contains(event.getCategory().getId()))
                    .collect(Collectors.toList());
        }

        if (text != null && !text.isEmpty()) {
            String searchText = text.toLowerCase();
            events = events.stream()
                    .filter(event -> event.getAnnotation().toLowerCase().contains(searchText) ||
                            event.getDescription().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
        }

        if (paid != null) {
            events = events.stream()
                    .filter(event -> event.getPaid().equals(paid))
                    .collect(Collectors.toList());
        }

        if (Boolean.TRUE.equals(onlyAvailable)) {
            events = events.stream()
                    .filter(event -> event.getParticipantLimit() == 0 ||
                            event.getConfirmedRequests() < event.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdByPublicUser(Long id) {
        log.info("Сервис получил запрос на получение ивента неавторизованным пользователем");
        Event event = eventRepository.findEventByIdAndState(id, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Ивент с id " + id + " не найден"));
        log.info("Ивент получен из БД и передаётся в контроллер...");
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        log.info("Сервис получил запрос на получение ивентов для админа");

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events;

        if (rangeStart == null) rangeStart = LocalDateTime.now().minusYears(100);
        if (rangeEnd == null) rangeEnd = LocalDateTime.now().plusYears(100);

        if (rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Дата начала не может быть после даты окончания");
        }

        events = eventRepository.findByEventDateBetween(rangeStart, rangeEnd, pageable);

        if (users != null && !users.isEmpty()) {
            events = events.stream()
                    .filter(event -> users.contains(event.getInitiator().getId()))
                    .collect(Collectors.toList());
        }

        if (states != null && !states.isEmpty()) {
            events = events.stream()
                    .filter(event -> states.contains(event.getState()))
                    .collect(Collectors.toList());
        }

        if (categories != null && !categories.isEmpty()) {
            events = events.stream()
                    .filter(event -> categories.contains(event.getCategory().getId()))
                    .collect(Collectors.toList());
        }

        return events.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventOfAdmin(Long eventId, UpdateEventAdminRequest eventAdminRequest) {
        log.info("Сервис получил запрос на обновление ивента админом");

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ивент с id " + eventId + " не найден"));

        if (eventAdminRequest.getEventDate() != null) {
            if (eventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ValidationException("Дата события должна быть не ранее чем через 1 час от текущего момента");
            }
        }

        if (eventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(eventAdminRequest.getAnnotation());
        }
        if (eventAdminRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(eventAdminRequest.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена"));
            event.setCategory(category);
        }
        if (eventAdminRequest.getDescription() != null) {
            event.setDescription(eventAdminRequest.getDescription());
        }
        if (eventAdminRequest.getEventDate() != null) {
            event.setEventDate(eventAdminRequest.getEventDate());
        }
        if (eventAdminRequest.getPaid() != null) {
            event.setPaid(eventAdminRequest.getPaid());
        }
        if (eventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(eventAdminRequest.getParticipantLimit());
        }
        if (eventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(eventAdminRequest.getRequestModeration());
        }
        if (eventAdminRequest.getTitle() != null) {
            event.setTitle(eventAdminRequest.getTitle());
        }

        if (eventAdminRequest.getStateAction() != null) {
            if (event.getState() != State.PENDING) {
                throw new ValidationException("Событие должно быть в состоянии ожидания публикации");
            }

            switch (eventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(State.CANCELED);
                    break;
            }
        }

        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public List<EventFullDto> getAllUserEvents(Long userId, Integer from, Integer size) {
        log.info("Сервис получил запрос на получение ивентов пользователя с id {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);

        return events.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
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
        event.setCategory(category);
        event.setInitiator(user);
        event.setState(State.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setConfirmedRequests(0L);

        Event savedEvent = eventRepository.save(event);
        log.info("Ивент успешно сохранён в БД. Его id = {}", savedEvent.getId());
        return eventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        log.info("Сервис получил запрос на получение конкретного ивента пользователя");

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ивент с id " + eventId + " не найден"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Ивент с id " + eventId + " не принадлежит пользователю с id " + userId);
        }

        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        log.info("Сервис получил запрос на обновление ивента пользователем");

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ивент с id " + eventId + " не найден"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Только инициатор события может его редактировать");
        }

        if (event.getState() == State.PUBLISHED) {
            throw new ValidationException("Нельзя редактировать опубликованное событие");
        }

        if (updateRequest.getEventDate() != null &&
                updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата события должна быть не ранее чем через 2 часа от текущего момента");
        }

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена"));
            event.setCategory(category);
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
            }
        }

        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsToUserEvent(Long userId, Long eventId) {
        log.info("Сервис получил запрос на получение заявок к ивенту пользователя");

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ивент с id " + eventId + " не найден"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Только инициатор события может просматривать заявки");
        }

        List<Request> requests = requestRepository.findByEventId(eventId);
        return requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatusToUserEvent(Long userId, Long eventId,
                                                                         EventRequestStatusUpdateRequest request) {
        log.info("Сервис получил запрос на изменение статуса заявок");

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ивент с id " + eventId + " не найден"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Только инициатор события может изменять статус заявок");
        }

        if (event.getParticipantLimit() > 0 &&
                event.getConfirmedRequests() >= event.getParticipantLimit() &&
                request.getStatus() == Status.CONFIRMED) {
            throw new ValidationException("Достигнут лимит участников");
        }

        List<Request> requestsToUpdate = requestRepository.findAllById(request.getRequestIds());

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (Request req : requestsToUpdate) {
            if (!req.getEvent().getId().equals(eventId)) {
                throw new ValidationException("Заявка не принадлежит указанному событию");
            }

            if (req.getStatus() != Status.PENDING) {
                throw new ValidationException("Можно изменять статус только заявок в состоянии ожидания");
            }

            if (request.getStatus() == Status.CONFIRMED) {
                if (event.getParticipantLimit() > 0 &&
                        event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    req.setStatus(Status.REJECTED);
                    rejectedRequests.add(requestMapper.toParticipationRequestDto(req));
                } else {
                    req.setStatus(Status.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    confirmedRequests.add(requestMapper.toParticipationRequestDto(req));
                }
            } else if (request.getStatus() == Status.REJECTED) {
                req.setStatus(Status.REJECTED);
                rejectedRequests.add(requestMapper.toParticipationRequestDto(req));
            }
        }

        eventRepository.save(event);
        requestRepository.saveAll(requestsToUpdate);

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }
}