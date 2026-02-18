package com.team2.fabackend.domain.advice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AdviceHistoryRepository extends JpaRepository<AdviceHistory, Long> {
    boolean existsByUserIdAndCreatedAt(Long userId, LocalDate createdAt);
    Optional<AdviceHistory> findByUserIdAndCreatedAt(Long userId, LocalDate createdAt);
}
