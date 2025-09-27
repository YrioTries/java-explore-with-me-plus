package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.model.Compilation;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Page<Compilation> findByPinned(Boolean pinned, Pageable pageable);
}