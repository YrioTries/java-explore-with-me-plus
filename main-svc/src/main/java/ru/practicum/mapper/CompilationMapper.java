package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "events", expression = "java(mapEvents(compilation.getEvents()))")
    CompilationDto toCompilationDto(Compilation compilation);

    default Set<EventShortDto> mapEvents(Set<Event> events) {
        if (events == null) {
            return new HashSet<>();
        }
        return new HashSet<>();
    }
}