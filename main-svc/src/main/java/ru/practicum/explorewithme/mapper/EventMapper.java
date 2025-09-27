package ru.practicum.explorewithme.mapper;

import org.mapstruct.*;
import ru.practicum.explorewithme.dto.LocationDto;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventDto;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.Location;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "views", ignore = true)
    Event toEvent(NewEventDto newEventDto);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    EventShortDto toEventShortDto(Event event);

    default Location toLocation(LocationDto dto) {
        if (dto == null) {
            return null;
        }
        Location location = new Location();
        location.setLat(dto.getLat());
        location.setLon(dto.getLon());
        return location;
    }

    @AfterMapping
    default void setDefaultValues(@MappingTarget Event event, NewEventDto newEventDto) {
        if (newEventDto.getPaid() == null) {
            event.setPaid(false);
        }
        if (newEventDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        if (newEventDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
    }

    default EventFullDto toEventFullDtoWithDetails(Event event, CategoryMapper categoryMapper, UserMapper userMapper) {
        EventFullDto dto = toEventFullDto(event);
        if (event.getCategory() != null) {
            dto.setCategory(categoryMapper.toCategoryDto(event.getCategory()));
        }
        if (event.getInitiator() != null) {
            dto.setInitiator(userMapper.toUserShortDto(event.getInitiator()));
        }
        return dto;
    }

    default EventShortDto toEventShortDtoWithDetails(Event event, CategoryMapper categoryMapper, UserMapper userMapper) {
        EventShortDto dto = toEventShortDto(event);
        if (event.getCategory() != null) {
            dto.setCategory(categoryMapper.toCategoryDto(event.getCategory()));
        }
        if (event.getInitiator() != null) {
            dto.setInitiator(userMapper.toUserShortDto(event.getInitiator()));
        }
        return dto;
    }
}