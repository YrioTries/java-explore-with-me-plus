package ru.practicum.explorewithme;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatResponseDto;

@Mapper(componentModel = "spring")
public interface StatMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "timestamp", target = "created")
    Stat toStat(EndpointHitDto endpointHitDto);

    StatResponseDto toStatResponseDto(Stat stat);
}