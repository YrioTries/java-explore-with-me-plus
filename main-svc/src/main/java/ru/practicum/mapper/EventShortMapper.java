package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.Event;

@Mapper(componentModel = "spring")
public interface EventShortMapper {

    EventShortDto toEventShortDto(Event event);
}