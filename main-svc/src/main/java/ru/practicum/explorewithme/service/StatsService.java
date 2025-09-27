package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsClient statsClient;

    public void recordEventView(Long eventId) {
        EndpointHitDto hitDto = new EndpointHitDto();
        hitDto.setApp("explore-with-me");
        hitDto.setUri("/events/" + eventId);
        hitDto.setIp("127.0.0.1");
        hitDto.setTimestamp(LocalDateTime.now());

        statsClient.hit(hitDto);
    }
}