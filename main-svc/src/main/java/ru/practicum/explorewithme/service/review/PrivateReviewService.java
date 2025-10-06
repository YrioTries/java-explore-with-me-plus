package ru.practicum.explorewithme.service.review;

import ru.practicum.explorewithme.dto.review.NewReviewDto;
import ru.practicum.explorewithme.dto.review.ReviewDto;

import java.util.List;

public interface PrivateReviewService {

    ReviewDto addReview(Long userId, Long eventId, NewReviewDto dto);

    void deleteReviewByAuthor(Long userId, Long reviewId);

    ReviewDto getReviewById(Long userId, Long reviewId);

    List<ReviewDto> getReviewsByAuthor(Long userId);

}
