package ru.practicum.explorewithme.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.explorewithme.enums.RequestStatus;

import java.time.LocalDateTime;

import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "participation_requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    Long id;
    @Builder.Default
    LocalDateTime created = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;
    @Enumerated(value = EnumType.STRING)
    RequestStatus status;

}