package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    ParticipationRequestDto toParticipationRequestDto(Request request);
}
