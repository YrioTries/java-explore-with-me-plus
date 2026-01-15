package ru.practicum.explorewithme.controller;

import ru.practicum.EndpointHitDto;
import ru.practicum.StatResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import ru.practicum.explorewithme.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        statService.addHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<StatResponseDto> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        return statService.getStats(start, end, uris, unique);
    }
}