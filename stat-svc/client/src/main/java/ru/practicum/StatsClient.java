package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

@Slf4j
@Component
public class StatsClient {
    private final String serverUrl;
    private final RestClient restClient;

    public StatsClient(@Value("${stats-server.url:http://localhost:9090}") String serverUrl, RestClient restClient) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        requestFactory.setReadTimeout((int) Duration.ofSeconds(5).toMillis());
        this.serverUrl = serverUrl;
        this.restClient = RestClient.builder()
                .baseUrl(serverUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public void hit(EndpointHitDto endpointHitDto) {
        String url = serverUrl + "/hit";
        try {
            restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(endpointHitDto)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Хит успешно отправлен");
        } catch (Exception e) {
            log.error("Ошибка при отправке хита в сервис статистики: {}", e.getMessage());
        }
    }

    public List<StatResponseDto> getStats(LocalDateTime start, LocalDateTime end,
                                          List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringJoiner uriJoiner = new StringJoiner(",");
        if (uris != null && !uris.isEmpty()) {
            uris.forEach(uriJoiner::add);
        }

        String url = String.format(
                "%s/stats?start=%s&end=%s&unique=%s&uris=%s",
                serverUrl,
                start.format(formatter),
                end.format(formatter),
                unique,
                uriJoiner.toString()
        );

        try {
            StatResponseDto[] response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(StatResponseDto[].class);
            log.info("Статистика успешно получена от сервиса");
            return List.of(response);
        } catch (Exception e) {
            log.error("Ошибка при получении статистики от сервиса: {}", e.getMessage());
            return List.of();
        }
    }
}
