package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StatsClient {
    private final String serverUrl;
    private final RestTemplate restTemplate;

    public StatsClient(String serverUrl, RestTemplate restTemplate) {
        this.serverUrl = serverUrl;
        this.restTemplate = restTemplate;
    }

    public void hit(EndpointHitDto endpointHitDto) {
        String url = serverUrl + "/hit";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EndpointHitDto> request = new HttpEntity<>(endpointHitDto, headers);

        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(url, request, Object.class);
            log.info("Хит успешно отправлен: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("Ошибка при отправке хита в сервис статистики: {}", e.getMessage());
        }
    }

    public List<StatResponseDto> getStats(LocalDateTime start, LocalDateTime end,
                                          List<String> uris, Boolean unique) {
        String url = serverUrl + "/stats?start={start}&end={end}&unique={unique}";

        if (uris != null && !uris.isEmpty()) {
            url += "&uris={uris}";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Map<String, Object> params = new HashMap<>();
        params.put("start", start.format(formatter));
        params.put("end", end.format(formatter));
        params.put("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            params.put("uris", String.join(",", uris));
        } else {
            params.put("uris", "");
        }

        try {
            ResponseEntity<StatResponseDto[]> response = restTemplate.getForEntity(
                    url,
                    StatResponseDto[].class,
                    params
            );
            log.info("Статистика успешно получена от сервиса");
            return List.of(response.getBody());
        } catch (Exception e) {
            log.error("Ошибка при получении статистики от сервиса: {}", e.getMessage());
            return List.of();
        }
    }
}