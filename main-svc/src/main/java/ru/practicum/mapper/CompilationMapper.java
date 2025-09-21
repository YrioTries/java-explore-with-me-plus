package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.model.Compilation;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    // Здесь пока не маппится множество с сущностями Event,
    // для этого нужно создать маппер EventMapper, мапящий Event в EventShortDto
    CompilationDto toCompilationDto(Compilation compilation);
}
