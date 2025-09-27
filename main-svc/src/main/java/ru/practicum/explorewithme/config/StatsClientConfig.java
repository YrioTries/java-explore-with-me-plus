package ru.practicum.explorewithme.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import ru.practicum.StatsClient;

import java.time.Duration;

@Configuration
public class StatsClientConfig {

    @Value("${stats-server.url:http://localhost:9090}")
    private String serverUrl;

    @Bean
    public RestClient restClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        requestFactory.setReadTimeout((int) Duration.ofSeconds(5).toMillis());

        return RestClient.builder()
                .baseUrl(serverUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Bean
    public StatsClient statsClient(RestClient restClient) {
        return new StatsClient(serverUrl, restClient);
    }
}