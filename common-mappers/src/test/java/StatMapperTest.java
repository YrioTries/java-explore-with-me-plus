import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.explorewithme.Stat;
import ru.practicum.mappers.StatMapper;
import ru.practicum.stats.StatRequestDto;
import ru.practicum.stats.StatResponseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class StatMapperTest {
    private StatMapper statMapper;

    @BeforeEach
    public void setUp() {
        statMapper = Mappers.getMapper(StatMapper.class);
    }

    @Test
    public void mapToStatFromStatRequestDto() {
        LocalDateTime now = LocalDateTime.now();
        StatRequestDto statRequestDto = new StatRequestDto(
                now,
                now.plusMinutes(15),
                new ArrayList<>(),
                true
        );

        Stat stat = statMapper.toStat(statRequestDto);

        assertNotNull(stat);
        assertEquals(now, stat.getCreated());
    }

    @Test
    public void toStatResponseDto_ShouldMapAllFieldsCorrectly() {
        Stat stat = new Stat();
        stat.setId(1L);
        stat.setAppName("TestApp");
        stat.setUri("/test-uri");
        stat.setIp("192.168.1.1");
        stat.setCreated(LocalDateTime.now());

        StatResponseDto statResponseDto = statMapper.toStatResponseDto(stat);

        assertNotNull(statResponseDto);
        assertEquals("TestApp", statResponseDto.getApp());
        assertEquals("/test-uri", statResponseDto.getUrl());
        assertNull(statResponseDto.getCount());
    }

    @Test
    public void toStatResponseDto_ShouldHandleNullFields() {
        Stat stat = new Stat();
        stat.setId(1L);
        stat.setAppName(null);
        stat.setUri(null);
        stat.setIp("192.168.1.1");
        stat.setCreated(LocalDateTime.now());

        StatResponseDto statResponseDto = statMapper.toStatResponseDto(stat);

        assertNotNull(statResponseDto);
        assertNull(statResponseDto.getApp());
        assertNull(statResponseDto.getUrl());
    }
}
