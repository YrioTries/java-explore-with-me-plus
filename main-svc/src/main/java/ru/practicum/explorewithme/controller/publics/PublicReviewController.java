package ru.practicum.explorewithme.controller.publics;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.review.ReviewDto;
import ru.practicum.explorewithme.service.review.PublicReviewService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class PublicReviewController {
    private final PublicReviewService reviewService;

    @GetMapping("/{eventId}")
    public List<ReviewDto> getEventReviewsByPublic(@Positive @PathVariable Long eventId,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET-запрос публичного контроллера на получение списка отзывов к ивенту с id={}", eventId);
        return reviewService.getEventReviewsByPublic(eventId, from, size);
    }
}
