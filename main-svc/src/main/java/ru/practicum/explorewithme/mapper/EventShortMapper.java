package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.model.Event;

@Mapper(componentModel = "spring")
public interface EventShortMapper {

    EventShortDto toEventShortDto(Event event);
}