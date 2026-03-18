package com.team2.fabackend.domain.user;

import com.team2.fabackend.domain.userTerm.UserTerm;
import com.team2.fabackend.global.entity.BaseEntity;
import com.team2.fabackend.global.enums.SocialType;
import com.team2.fabackend.global.enums.UserType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SocialType socialType;

    @Column(nullable = false)
    private String nickName;
    @Column(nullable = false)
    private LocalDate birth;
    @Enumerated(value = EnumType.STRING)
    private UserType userType = UserType.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTerm> userTerms = new ArrayList<>();

    /**
     * 지정된 세부 정보로 새로운 User를 생성합니다.
     *
     * @param email      사용자의 email입니다.
     * @param password    사용자의 인코딩된 비밀번호입니다.
     * @param socialType  소셜 로그인 유형입니다.
     * @param nickName    사용자의 별명입니다.
     * @param birth       사용자의 생년월일입니다.
     * @param userType    사용자 유형 (예: USER, ADMIN)입니다.
     */
    @Builder
    protected User(
            String email,
            String password,
            SocialType socialType,

            String nickName,
            LocalDate birth,
            UserType userType
    ) {
        this.email = email;
        this.password = password;
        this.socialType = socialType != null ? socialType : SocialType.LOCAL;
        this.nickName = nickName;
        this.birth = birth;
        this.userType = userType != null ? userType : UserType.USER;
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
