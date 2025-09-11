package ru.practicum.explorewithme;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.StatResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    void createStat(HttpServletRequest request);

    List<StatResponseDto> getStats(LocalDateTime start, LocalDateTime end,
                                   List<String> uris, Boolean unique);
}
