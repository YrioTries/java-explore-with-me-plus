package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.repository.EventRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto addNewCompilation(NewCompilationDto newComp) {
        log.info("Сервис получил запрос на создание подборки");
        Compilation compilation = compilationMapper.toCompilation(newComp);
        compilation.setPinned(Optional.ofNullable(newComp.getPinned()).orElse(false));
        Set<Long> eventsId = (newComp.getEvents() != null) ? newComp.getEvents() : Collections.emptySet();
        Set<Event> events = eventRepository.findAllByIdIn(eventsId);
        compilation.setEvents(events);
        Compilation compToReturn = compilationRepository.save(compilation);
        log.info("Подборка была успешно сохранена в БД. Её id={}", compToReturn.getId());
        return compilationMapper.toCompilationDto(compToReturn);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long compId) {
        log.info("Сервис получил запрос на удаление подборки");
        if (!compilationRepository.existsById(compId)) {
            log.error("Подборки с id {} нет в БД", compId);
            throw new NotFoundException("Подборки с id " + compId + " нет в БД. Удаление невозможно");
        }
        compilationRepository.deleteById(compId);
        log.info("Подборка была успешно удалена из БД");
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {
        log.info("Сервис получил запрос на редактирование подборки");
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборки с id " + compId + " нет в БД"));
        Set<Long> eventsId = dto.getEvents();
        if (eventsId != null && !eventsId.isEmpty()) {
            log.info("Обновляем подборку новыми ивентами");
            compilation.setEvents(eventRepository.findAllByIdIn(eventsId));
        }
        Optional.ofNullable(dto.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(dto.getPinned()).ifPresent(compilation::setPinned);
        Compilation updatedComp = compilationRepository.save(compilation);
        log.info("Подборка с id {} успешно обновлена", compId);
        return compilationMapper.toCompilationDto(updatedComp);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("Сервис получил запрос на получение списка подборок");
        PageRequest pageRequest = PageRequest.of(from, size);
        if (pinned != null) {
            log.info("Получаем из БД подборки, у которых поле pinned={}", pinned);
            return compilationRepository.findAllByPinned(pinned, pageRequest)
                    .stream()
                    .map(compilationMapper::toCompilationDto)
                    .toList();
        } else {
            log.info("Получаем из БД все подборки");
            return compilationRepository.findAll(pageRequest)
                    .stream()
                    .map(compilationMapper::toCompilationDto)
                    .toList();
        }
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.info("Сервис получил запрос на получение подборки");
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборки с id " + compId + " нет в БД"));
        log.info("Подборка получена из БД и передаётся в контроллер");
        return compilationMapper.toCompilationDto(compilation);
    }
}
