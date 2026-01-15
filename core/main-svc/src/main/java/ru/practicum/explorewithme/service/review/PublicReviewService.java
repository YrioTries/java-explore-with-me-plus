package ru.practicum.explorewithme.service.review;

import ru.practicum.explorewithme.dto.review.ReviewDto;

import java.util.List;

public interface PublicReviewService {

    List<ReviewDto> getEventReviewsByPublic(Long eventId, Integer from, Integer size);

}
