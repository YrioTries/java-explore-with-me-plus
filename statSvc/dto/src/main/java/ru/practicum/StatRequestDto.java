package ru.practicum;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatRequestDto {
    @NotNull
    @PastOrPresent
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull
    private Collection<String> uris;

    private Boolean unique;
}
