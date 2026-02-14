package com.team2.fabackend.api.budget.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BudgetRequest {
    //1. 식비
    private int foodDailyOption; //(1) 하루 평균 식비
    private int deliveryFreqOption; //(2) 일주일 평균 배달 빈도
    private int dessertCostOption; //(3) 일주일 평균 카페/디저트 비용

    //2. 교통
    private int transportTypeOption; //(1) 주요 이동 수단
    private int transportMonthlyOption; //(2) 한달 평균 교통비
    private int taxiFreqOption; //(3) 택시 이용 빈도

    // 3. 여가
    private int hobbyCostOption;      // (1)한 달 여가/취미 금액
    private int contentFreqOption;    // (2)정기 결제 콘텐츠 서비스 개수

    // 4. 고정지출금
    private int fixedMonthlyOption;   // (1)매달 나가는 고정비
    private int residenceCostOption;  // (2)월세 또는 주거 비용
    private int communicationCostOption; // (3)통신비
}
