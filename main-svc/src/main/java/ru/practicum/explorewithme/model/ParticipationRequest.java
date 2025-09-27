package ru.practicum.explorewithme.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.explorewithme.enums.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "participation_requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status = RequestStatus.PENDING;
}