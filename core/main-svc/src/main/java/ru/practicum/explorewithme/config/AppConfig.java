package ru.practicum.explorewithme.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import ru.practicum.StatsClient;

@Configuration
public class AppConfig {

    @Bean
    public ru.practicum.StatsClient statsClient(@Value("${stats-server.url:http://localhost:9090}") String serverUrl) {
        RestClient restClient = RestClient.builder()
                .baseUrl(serverUrl)
                .requestFactory(new SimpleClientHttpRequestFactory())
                .build();
        return new StatsClient(serverUrl, restClient);
    }
}
