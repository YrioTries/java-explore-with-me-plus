package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventFullDto toEventFullDto(Event event);
}
