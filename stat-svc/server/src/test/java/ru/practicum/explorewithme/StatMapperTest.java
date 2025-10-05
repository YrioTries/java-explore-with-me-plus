/*package ru.practicum.explorewithme;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatResponseDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class StatMapperTest {
    private StatMapper statMapper;

    @BeforeEach
    public void setUp() {
        statMapper = Mappers.getMapper(StatMapper.class);
    }

    @Test
    public void toStat_ShouldMapAllFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        EndpointHitDto endpointHitDto = new EndpointHitDto(
                "TestApp",
                "/test-uri",
                "192.168.1.1",
                now
        );

        Stat stat = statMapper.toStat(endpointHitDto);

        assertNotNull(stat);
        assertEquals("TestApp", stat.getApp());
        assertEquals("/test-uri", stat.getUri());
        assertEquals("192.168.1.1", stat.getIp());
        assertEquals(now, stat.getCreated());
    }

    @Test
    public void toStatResponseDto_ShouldMapAllFieldsCorrectly() {
        Stat stat = new Stat();
        stat.setId(1L);
        stat.setApp("TestApp");
        stat.setUri("/test-uri");
        stat.setIp("192.168.1.1");
        stat.setCreated(LocalDateTime.now());

        StatResponseDto statResponseDto = statMapper.toStatResponseDto(stat);

        assertNotNull(statResponseDto);
        assertEquals("TestApp", statResponseDto.getApp());
        assertEquals("/test-uri", statResponseDto.getUri());
        assertNull(statResponseDto.getHits()); // hits будет null, так как не маппится из Stat
    }

    @Test
    public void toStat_ShouldHandleNullFields() {
        EndpointHitDto endpointHitDto = new EndpointHitDto(
                null,
                null,
                "192.168.1.1",
                LocalDateTime.now()
        );

        Stat stat = statMapper.toStat(endpointHitDto);

        assertNotNull(stat);
        assertNull(stat.getApp());
        assertNull(stat.getUri());
        assertEquals("192.168.1.1", stat.getIp());
    }
}*/