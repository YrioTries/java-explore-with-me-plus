package ru.practicum.mappers;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.explorewithme.Stat;
import ru.practicum.stats.StatRequestDto;
import ru.practicum.stats.StatResponseDto;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface StatMapper {

    Stat toStat(StatRequestDto statRequestDto);

    StatResponseDto toStatResponseDto(Stat stat);
}
