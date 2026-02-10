package com.team2.fabackend.service.ledger;

import com.team2.fabackend.api.ledger.dto.LedgerRequest;
import com.team2.fabackend.domain.goals.Goal; // 패키지 경로 확인 필요
import com.team2.fabackend.domain.goals.GoalRepository; // 패키지 경로 확인 필요
import com.team2.fabackend.domain.ledger.Ledger;
import com.team2.fabackend.domain.ledger.LedgerRepository;
import com.team2.fabackend.domain.ledger.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final GoalRepository goalRepository; // 🚨 목표 갱신을 위해 추가

    // 가계부 내역 저장하기(C)
    public void saveLedger(Long userId, LedgerRequest request) {
        // 1. 내역 저장 (goalId는 엔티티에서 빼거나 null로 처리)
        Ledger ledger = Ledger.builder()
                .userId(userId) // 🚨 DTO가 아닌 토큰에서 받은 userId 사용
                .amount(request.getAmount())
                .category(request.getCategory())
                .memo(request.getMemo())
                .type(request.getType())
                .date(request.getDate())
                .time(request.getTime())
                .build();

        ledgerRepository.save(ledger);

        // 2. 지출(EXPENSE)인 경우 모든 활성 목표에 자동 반영
        if (request.getType() == TransactionType.EXPENSE) {
            updateRelatedGoals(userId, request);
        }
    }

    // 목표 업데이트 로직 분리 (가독성)
    private void updateRelatedGoals(Long userId, LedgerRequest request) {
        // 유저의 모든 '진행 중'인 목표 조회 (GoalStatus는 프로젝트 설정에 맞게 조절)
        List<Goal> activeGoals = goalRepository.findAllByUserId(userId);

        for (Goal goal : activeGoals) {
            // 카테고리가 일치하거나, '전체' 예산 목표인 경우 금액 합산
            if (goal.getCategory().equals(request.getCategory()) || goal.getCategory().equals("전체")) {
                goal.addCurrentAmount(request.getAmount()); // Goal 엔티티에 이 메서드 만드셔야 해요!
            }
        }
    }

    // 특정 유저의 내역만 가져오기 (전체 조회가 아니라 유저별 조회가 안전합니다)
    @Transactional(readOnly = true)
    public List<Ledger> findAllByUserId(Long userId) {
        return ledgerRepository.findAllByUserId(userId);
    }

    // 수정하기(U) - 수정 시에도 목표 금액이 바뀌어야 하므로 로직이 복잡해질 수 있음 주의!
    @Transactional
    public void update(Long id, LedgerRequest request) {
        Ledger ledger = ledgerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 내역이 없습니다. id=" + id));

        // 기존 금액과 차액만큼 목표를 수정하는 로직이 추가되면 더 완벽합니다.
        ledger.update(request.getAmount(),
                request.getCategory(),
                request.getMemo(),
                request.getType(),
                request.getDate(),
                request.getTime());
    }

    // 지우기(D)
    @Transactional
    public void delete(Long id) {
        Ledger ledger = ledgerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 내역이 없습니다. id=" + id));

        // 지울 때도 목표에서 그만큼 금액을 빼주는 로직이 필요할 수 있습니다.
        ledgerRepository.delete(ledger);
    }
}