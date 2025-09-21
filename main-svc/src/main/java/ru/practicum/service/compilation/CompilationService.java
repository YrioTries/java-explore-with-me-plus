package ru.practicum.service.compilation;

import ru.practicum.dto.compilation.CompilationDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);

}
