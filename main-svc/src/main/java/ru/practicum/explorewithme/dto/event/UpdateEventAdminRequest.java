package ru.practicum.explorewithme.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.dto.LocationDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {
    private String annotation;
    private Long category;
    private String description;
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    private String title;
}