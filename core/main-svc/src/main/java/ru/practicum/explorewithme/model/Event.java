package ru.practicum.explorewithme.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.explorewithme.enums.EventState;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    Long id;

    @Column(name = "annotation", length = 2000)
    String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @Transient
    private Long views;

    @Column(name = "confirmed_requests")
    Integer confirmedRequests;

    @Column(name = "created_on", nullable = false)
    LocalDateTime createdOn;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "description", length = 7000)
    String description;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    User initiator;

    @Embedded
    Location location;
    Boolean paid;

    @Column(name = "participant_limit")
    Integer participantLimit;

    @Enumerated(value = EnumType.STRING)
    EventState state;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    String title;
}

