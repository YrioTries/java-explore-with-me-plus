package ru.practicum.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDto {
    @Min(-90)
    @Max(90)
    @NotNull
    private Float lat;

    @Min(-180)
    @Max(180)
    @NotNull
    private Float lon;
}
