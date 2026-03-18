package com.team2.fabackend.service.ledger;

import com.team2.fabackend.api.ledger.dto.LedgerRequest;
import com.team2.fabackend.domain.goals.Goal; 
import com.team2.fabackend.domain.goals.GoalRepository; 
import com.team2.fabackend.domain.ledger.Ledger;
import com.team2.fabackend.domain.ledger.LedgerRepository;
import com.team2.fabackend.domain.ledger.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.team2.fabackend.service.user.UserReader;
...
@Service
@RequiredArgsConstructor
@Transactional
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final GoalRepository goalRepository;
    private final UserReader userReader;

    /**
     * 사용자의 가계부 내역을 저장하고, 지출(EXPENSE) 타입인 경우 연동된 저축 목표에 금액을 반영합니다.
     * 
     * @param userId 유저 식별자
     * @param request 가계부 저장 요청 정보 (금액, 카테고리, 메모, 타입, 일시 등)
     */
    public void saveLedger(Long userId, LedgerRequest request) {
        User user = userReader.findById(userId);
        
        Ledger ledger = Ledger.builder()
                .user(user)
                .amount(request.getAmount())
                .category(request.getCategory())
                .memo(request.getMemo())
                .type(request.getType())
                .date(request.getDate())
                .time(request.getTime())
                .build();

        ledgerRepository.save(ledger);

        if (request.getType() == TransactionType.EXPENSE) {
            updateRelatedGoals(userId, request);
        }
    }

    /**
     * 사용자의 활성화된 저축 목표 중 해당 지출 카테고리에 부합하는 목표의 현재 달성 금액을 업데이트합니다.
     * 
     * @param userId 유저 식별자
     * @param request 가계부 지출 정보
     */
    private void updateRelatedGoals(Long userId, LedgerRequest request) {
        List<Goal> activeGoals = goalRepository.findAllByUser_Id(userId);

        for (Goal goal : activeGoals) {
            String goalCategory = goal.getCategory();
            String requestCategory = request.getCategory();

            if ("전체".equals(goalCategory) || (goalCategory != null && goalCategory.equals(requestCategory))) {
                goal.addCurrentAmount(request.getAmount());
            }
        }
    }

    /**
     * 특정 사용자의 모든 가계부 내역을 조회합니다.
     * 
     * @param userId 유저 식별자
     * @return 해당 사용자의 가계부 내역 리스트
     */
    @Transactional(readOnly = true)
    public List<Ledger> findAllByUserId(Long userId) {
        return ledgerRepository.findAllByUser_Id(userId);
    }

    /**
     * 기존 가계부 내역의 정보를 수정합니다.
     * 
     * @param id 수정할 가계부 내역의 식별자
     * @param request 수정할 가계부 정보
     */
    @Transactional
    public void update(Long id, LedgerRequest request) {
        Ledger ledger = ledgerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 내역이 없습니다. id=" + id));

        ledger.update(request.getAmount(),
                request.getCategory(),
                request.getMemo(),
                request.getType(),
                request.getDate(),
                request.getTime());
    }

    /**
     * 특정 가계부 내역을 삭제합니다.
     * 
     * @param id 삭제할 가계부 내역의 식별자
     */
    @Transactional
    public void delete(Long id) {
        Ledger ledger = ledgerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 내역이 없습니다. id=" + id));

        ledgerRepository.delete(ledger);
    }
}
