package ru.practicum.explorewithme.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewReviewDto {
    @NotBlank
    @Size(min = 2, max = 2000)
    private String text;
}
