package ru.practicum.explorewithme.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatResponseDto;
import ru.practicum.explorewithme.model.entity.Stat;

@Mapper(componentModel = "spring")
public interface StatMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "timestamp", target = "created")
    Stat toStat(EndpointHitDto endpointHitDto);

    StatResponseDto toStatResponseDto(Stat stat);
}