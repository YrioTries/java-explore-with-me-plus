package ru.practicum.explorewithme.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventDto;
import ru.practicum.explorewithme.dto.event.UpdateEventUserRequest;
import ru.practicum.explorewithme.enums.EventState;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsByUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        return eventRepository.findByInitiatorId(userId, pageable)
                .map(eventMapper::toEventShortDto)
                .getContent();
    }

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be at least 2 hours from now");
        }

        Event event = eventMapper.toEvent(newEventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        if (newEventDto.getLocation() != null) {
            ru.practicum.explorewithme.model.Location location =
                    eventMapper.toLocation(newEventDto.getLocation());
            event.setLocation(location);
        }

        Event savedEvent = eventRepository.save(event);
        return eventMapper.toEventFullDto(savedEvent);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        return eventMapper.toEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Cannot update published event");
        }

        if (updateRequest.getEventDate() != null &&
                updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be at least 2 hours from now");
        }

        if (updateRequest.getStateAction() != null) {
            if (updateRequest.getStateAction().equals("SEND_TO_REVIEW")) {
                event.setState(EventState.PENDING);
            } else if (updateRequest.getStateAction().equals("CANCEL_REVIEW")) {
                event.setState(EventState.CANCELED);
            }
        }

        updateEventFields(event, updateRequest);
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toEventFullDto(updatedEvent);
    }

    private void updateEventFields(Event event, UpdateEventUserRequest updateRequest) {
        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            event.setCategory(category);
        }
        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getLocation() != null) {
            ru.practicum.explorewithme.model.Location location =
                    eventMapper.toLocation(updateRequest.getLocation());
            event.setLocation(location);
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
    }
}