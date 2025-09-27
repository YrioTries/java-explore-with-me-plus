package ru.practicum.explorewithme.model;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Column(name = "lat")
    private Float lat;

    @Column(name = "lon")
    private Float lon;
}