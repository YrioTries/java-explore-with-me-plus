package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.dto.LocationDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    private String annotation;
    private Long category;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;
    private Boolean paid = false;
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    private String title;
}
