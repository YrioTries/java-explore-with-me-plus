package ru.practicum.explorewithme.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.StatResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    void addHit(EndpointHitDto endpointHitDto);

    List<StatResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}