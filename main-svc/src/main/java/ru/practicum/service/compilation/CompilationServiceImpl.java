package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.repository.CompilationRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

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
