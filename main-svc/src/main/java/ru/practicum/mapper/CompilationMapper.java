package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.EventRepository;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class CompilationMapper {

    @Autowired
    protected EventRepository eventRepository;

    @Autowired
    protected EventMapper eventMapper;

    @Mapping(target = "events", expression = "java(mapEventsToEventShortDtos(compilation.getEvents()))")
    public abstract CompilationDto toCompilationDto(Compilation compilation);

    @Mapping(target = "events", expression = "java(mapEventIdsToEvents(newCompilationDto.getEvents()))")
    public abstract Compilation toCompilation(NewCompilationDto newCompilationDto);

    protected Set<Event> mapEventIdsToEvents(Set<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptySet();
        }
        return eventRepository.findAllByIdIn(eventIds);
    }

    protected Set<ru.practicum.dto.event.EventShortDto> mapEventsToEventShortDtos(Set<Event> events) {
        if (events == null || events.isEmpty()) {
            return Collections.emptySet();
        }
        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toSet());
    }
}