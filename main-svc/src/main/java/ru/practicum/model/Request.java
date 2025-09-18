package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.enums.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    private Long id;

    private LocalDateTime created = LocalDateTime.now();

    private Event event;

    private User requester;

    private Status status;
}
