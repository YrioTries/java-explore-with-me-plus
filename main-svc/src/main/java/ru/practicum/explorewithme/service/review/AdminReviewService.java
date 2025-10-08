package ru.practicum.explorewithme.service.review;

import ru.practicum.explorewithme.dto.review.ReviewDto;

import java.util.List;

public interface AdminReviewService {

    List<ReviewDto> getReviewsByAdmin(String text,
                                      List<Long> users,
                                      List<Long> events,
                                      Integer from,
                                      Integer size);

    void deleteReview(Long reviewId);

}
