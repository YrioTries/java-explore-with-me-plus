package ru.practicum.explorewithme.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.review.NewReviewDto;
import ru.practicum.explorewithme.dto.review.ReviewDto;
import ru.practicum.explorewithme.enums.EventState;
import ru.practicum.explorewithme.enums.RequestStatus;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.ReviewMapper;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.ParticipationRequest;
import ru.practicum.explorewithme.model.Review;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.ParticipationRequestRepository;
import ru.practicum.explorewithme.repository.ReviewRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PrivateReviewServiceImpl implements PrivateReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository requestRepository;

    private final ReviewMapper reviewMapper;

    @Override
    public ReviewDto addReview(Long userId, Long eventId, NewReviewDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + userId + " нет в БД!"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ивента с id=" + eventId + " нет в БД!"));
        verifyReview(user, event);
        Review review = reviewMapper.toReview(dto);
        review.setAuthor(user);
        review.setEvent(event);
        review.setCreatedOn(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toReviewDto(savedReview);
    }

    private void verifyReview(User user, Event event) {
        if (user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Инициатор ивента не может оставлять отзыв на свой ивент!");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Чтобы оставить отзыв, статус ивента должен быть PUBLISHED!");
        }
        if (!event.getEventDate().plusHours(1).isBefore(LocalDateTime.now())) {
            throw new ConflictException("Нельзя оставить отзыв на ивент, который ещё не закончился!");
        }
        ParticipationRequest request = requestRepository.findByEventIdAndRequesterId(event.getId(), user.getId())
                .orElseThrow(() -> new NotFoundException("Заявки юзера с id=" + user.getId() + " на участие в ивенте " +
                        "с id=" + event + " нет в БД!"));
        if (!request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ConflictException("Чтобы оставить отзыв, статус заявки юзера на участие в ивенте должен быть CONFIRMED!");
        }
    }
}
