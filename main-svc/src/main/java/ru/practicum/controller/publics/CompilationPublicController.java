package ru.practicum.controller.publics;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.compilation.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAllCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("В контроллер пришёл запрос на получение списка подборок");
        log.info("Параметры строки запроса: pinned={}, from={}, size={}", pinned, from, size);
        List<CompilationDto> compilationDtos = compilationService.getAllCompilations(pinned, from, size);
        log.info("Возвращаем список подборок клиенту. Размер списка: {}", compilationDtos.size());
        return compilationDtos;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PositiveOrZero @PathVariable Long compId) {
        log.info("В контроллер пришёл запрос на получение подборки");
        log.info("Id запрашиваемой подборки: {}", compId);
        CompilationDto compilationDto = compilationService.getCompilationById(compId);
        log.info("Возвращаем подборку клиенту");
        return compilationDto;
    }
}
