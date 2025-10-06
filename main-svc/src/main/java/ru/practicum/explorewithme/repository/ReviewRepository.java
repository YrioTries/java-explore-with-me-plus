package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.explorewithme.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    List<Review> findAllById(Long eventId, Pageable pageable);

}