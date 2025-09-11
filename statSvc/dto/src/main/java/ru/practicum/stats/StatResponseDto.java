package ru.practicum.stats;

import lombok.*;

import java.util.Objects;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class StatResponseDto {
    private String app;
    private String url;
    private Long count;
}
