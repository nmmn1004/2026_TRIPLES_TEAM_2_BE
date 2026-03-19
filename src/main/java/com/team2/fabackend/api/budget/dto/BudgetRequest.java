package com.team2.fabackend.api.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "예산 설문 기반 생성 요청 정보")
public class BudgetRequest {
    //1. 식비
    @Schema(description = "(식비) 하루 평균 식비 옵션 (1: 1만원 미만, 2: 1~2만원, 3: 2만원 이상 등)", example = "1")
    private int foodDailyOption; //(1) 하루 평균 식비
    @Schema(description = "(식비) 일주일 평균 배달 빈도 옵션 (0: 안함, 1: 1~2회, 2: 3회 이상)", example = "1")
    private int deliveryFreqOption; //(2) 일주일 평균 배달 빈도
    @Schema(description = "(식비) 일주일 평균 카페/디저트 비용 옵션", example = "1")
    private int dessertCostOption; //(3) 일주일 평균 카페/디저트 비용

    //2. 교통
    @Schema(description = "(교통) 주요 이동 수단 (1: 대중교통, 2: 자차, 3: 택시 위주)", example = "1")
    private int transportTypeOption; //(1) 주요 이동 수단
    @Schema(description = "(교통) 한달 평균 교통비 옵션", example = "1")
    private int transportMonthlyOption; //(2) 한달 평균 교통비
    @Schema(description = "(교통) 택시 이용 빈도 옵션", example = "1")
    private int taxiFreqOption; //(3) 택시 이용 빈도

    // 3. 여가
    @Schema(description = "(여가) 한 달 여가/취미 금액 옵션", example = "1")
    private int hobbyCostOption;      // (1)한 달 여가/취미 금액
    @Schema(description = "(여가) 정기 결제 콘텐츠 서비스 개수 옵션", example = "1")
    private int contentFreqOption;    // (2)정기 결제 콘텐츠 서비스 개수

    // 4. 고정지출금
    @Schema(description = "(고정비) 매달 나가는 고정비 옵션", example = "1")
    private int fixedMonthlyOption;   // (1)매달 나가는 고정비
    @Schema(description = "(고정비) 월세 또는 주거 비용 옵션", example = "1")
    private int residenceCostOption;  // (2)월세 또는 주거 비용
    @Schema(description = "(고정비) 통신비 옵션", example = "1")
    private int communicationCostOption; // (3)통신비
}
