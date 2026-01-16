package ru.practicum.explorewithme.service.review.public_rights;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.review.ReviewDto;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.ReviewMapper;
import ru.practicum.explorewithme.model.Review;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.ReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicReviewServiceImpl implements PublicReviewService {
    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;

    private final ReviewMapper reviewMapper;

    @Override
    public List<ReviewDto> getEventReviewsByPublic(Long eventId, Integer from, Integer size) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Ивента с id=" + eventId + " нет в БД!");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Review> reviews = reviewRepository.findAllByEventId(eventId, pageRequest);
        return reviews
                .stream()
                .map(reviewMapper::toReviewDto)
                .toList();
    }
}
