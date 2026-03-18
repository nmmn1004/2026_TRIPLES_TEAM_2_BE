package com.team2.fabackend.domain.user;

import com.team2.fabackend.domain.advice.AdviceHistory;
import com.team2.fabackend.domain.budget.BudgetGoal;
import com.team2.fabackend.domain.goals.Goal;
import com.team2.fabackend.domain.ledger.Ledger;
import com.team2.fabackend.domain.userTerm.UserTerm;
import com.team2.fabackend.global.entity.BaseEntity;
import com.team2.fabackend.global.enums.AccountStatus;
import com.team2.fabackend.global.enums.SocialType;
import com.team2.fabackend.global.enums.UserType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_email_social", columnNames = {"email", "socialType"}),
        @UniqueConstraint(name = "uk_user_nickname", columnNames = {"nickName"})
})
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    @Enumerated(value = EnumType.STRING)
    private SocialType socialType;

    @Column(unique = true, length = 100)
    private String deviceId;

    @Column(nullable = false, length = 50)
    private String nickName;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false, length = 20)
    @Enumerated(value = EnumType.STRING)
    private UserType userType = UserType.USER;

    @Column(nullable = false, length = 20)
    @Enumerated(value = EnumType.STRING)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    private LocalDateTime lastLoginAt;

    @Column(nullable = false)
    private int loginFailCount = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTerm> userTerms = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ledger> ledgers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Goal> goals = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private BudgetGoal budgetGoal;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdviceHistory> adviceHistories = new ArrayList<>();

    /**
     * 지정된 세부 정보로 새로운 User를 생성합니다.
     *
     * @param email      사용자의 email입니다.
     * @param password    사용자의 인코딩된 비밀번호입니다.
     * @param socialType  소셜 로그인 유형입니다.
     * @param deviceId    사용자의 기기 고유 ID입니다.
     * @param nickName    사용자의 별명입니다.
     * @param birth       사용자의 생년월일입니다.
     * @param userType    사용자 유형 (예: USER, ADMIN)입니다.
     */
    @Builder
    protected User(
            String email,
            String password,
            SocialType socialType,
            String deviceId,
            String nickName,
            LocalDate birth,
            UserType userType
    ) {
        this.email = email;
        this.password = password;
        this.socialType = socialType != null ? socialType : SocialType.LOCAL;
        this.deviceId = deviceId;
        this.nickName = nickName;
        this.birth = birth;
        this.userType = userType != null ? userType : UserType.USER;
        this.accountStatus = AccountStatus.ACTIVE;
        this.loginFailCount = 0;
    }

    /**
     * 마지막 로그인 시간을 현재 시간으로 업데이트합니다.
     */
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
        resetLoginFailCount();
    }

    /**
     * 로그인 실패 횟수를 1 증가시킵니다.
     * 실패 횟수가 5회 이상이면 계정을 잠금 상태로 변경합니다.
     */
    public void increaseLoginFailCount() {
        this.loginFailCount++;
        if (this.loginFailCount >= 5) {
            this.accountStatus = AccountStatus.LOCKED;
        }
    }

    /**
     * 로그인 실패 횟수를 0으로 초기화합니다.
     */
    public void resetLoginFailCount() {
        this.loginFailCount = 0;
    }

    /**
     * 계정 상태를 변경합니다.
     * @param status 새로운 계정 상태
     */
    public void updateAccountStatus(AccountStatus status) {
        this.accountStatus = status;
    }

    /**
     * 사용자의 비밀번호를 업데이트합니다.
     *
     * @param encodedPassword 새로운 인코딩된 비밀번호입니다.
     */
    public void updatePassword(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new IllegalArgumentException("새로운 비밀번호는 비어 있을 수 없습니다.");
        }

        this.password = encodedPassword;
    }

    /**
     * 사용자의 별명을 업데이트합니다.
     *
     * @param nickName 새로운 별명입니다.
     */
    public void updateNickName(String nickName) {
        if (nickName == null || nickName.isBlank()) {
            throw new IllegalArgumentException("새로운 별명은 비어 있을 수 없습니다.");
        }

        this.nickName = nickName;
    }

    /**
     * 사용자의 생년월일을 업데이트합니다.
     *
     * @param birth 새로운 생년월일입니다.
     */
    public void updateBirth(LocalDate birth) {
        if (birth == null) {
            throw new IllegalArgumentException("생년월일은 비어 있을 수 없습니다.");
        }

        this.birth = birth;
    }
}
