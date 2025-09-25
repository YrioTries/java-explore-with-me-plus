package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getParticipationRequests(Long userId) {
        log.info("В сервис пришёл запрос на получение юзером заявок на участие");
        if (!userRepository.existsById(userId)) {
            log.error("Юзера с id {} нет в БД", userId);
            throw new NotFoundException("Юзер с id " + userId + " не найден");
        }
        List<ParticipationRequestDto> result = requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
        log.info("Found {} requests for user with id={}", result.size(), userId);
        return result;
    }

    @Override
    public ParticipationRequestDto createParticipationRequest(Long userId, Long eventId) {
        return null;
    }

    @Override
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        return null;
    }
}
