package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class GatewayServerStarter {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServerStarter.class, args);
    }
}
