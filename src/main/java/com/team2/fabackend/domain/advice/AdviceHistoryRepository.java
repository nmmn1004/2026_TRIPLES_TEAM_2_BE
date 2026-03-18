package com.team2.fabackend.domain.advice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AdviceHistoryRepository extends JpaRepository<AdviceHistory, Long> {
    /**
     * 특정 날짜에 사용자에 대한 조언 기록 레코드가 존재하는지 확인합니다.
     *
     * @param userId    사용자의 ID입니다.
     * @param createdAt 확인할 날짜입니다.
     * @return 레코드가 존재하면 true, 그렇지 않으면 false입니다.
     */
    boolean existsByUserIdAndCreatedAt(Long userId, LocalDate createdAt);

    /**
     * 특정 날짜에 사용자에 대한 조언 기록 레코드를 찾습니다.
     *
     * @param userId    사용자의 ID입니다.
     * @param createdAt 검색할 날짜입니다.
     * @return 발견된 경우 AdviceHistory를 포함하는 Optional입니다.
     */
    Optional<AdviceHistory> findByUserIdAndCreatedAt(Long userId, LocalDate createdAt);
}
