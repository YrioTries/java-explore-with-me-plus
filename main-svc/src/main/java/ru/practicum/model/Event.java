package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.enums.State;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Long id;

    private String title;

    private String annotation;

    private Category category;

    private Long views;

    private Long confirmedRequests;

    private LocalDateTime createdOn;

    private LocalDateTime publishedOn;

    private String description;

    private LocalDateTime eventDate;

    private User initiator;

    private Float lat;

    private Float lon;

    private Boolean paid;

    private State state;

    private int participantLimit;

    private Boolean requestModeration;
}
