package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findByIdIn(List<Long> ids, Pageable pageable);

    Boolean existsByEmail(String email);
}