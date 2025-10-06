package ru.practicum.explorewithme.service.review;

import ru.practicum.explorewithme.dto.review.NewReviewDto;
import ru.practicum.explorewithme.dto.review.ReviewDto;

public interface PrivateReviewService {

    ReviewDto addReview(Long userId, Long eventId, NewReviewDto dto);

}
