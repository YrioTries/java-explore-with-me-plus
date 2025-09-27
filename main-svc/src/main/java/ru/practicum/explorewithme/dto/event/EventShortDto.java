package ru.practicum.explorewithme.dto.event;

import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.user.UserShortDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}