package com.team2.fabackend.domain.user;

import com.team2.fabackend.global.entity.BaseEntity;
import com.team2.fabackend.global.enums.SocialType;
import com.team2.fabackend.global.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  Account info
    @Column(nullable = false, unique = true)
    private String userId;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SocialType socialType;
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    //  User info
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String nickName;
    @Column(nullable = false)
    private LocalDate birth;
    @Enumerated(value = EnumType.STRING)
    private UserType userType = UserType.USER;

    @Builder
    protected User(
            String userId,
            String password,
            SocialType socialType,

            String name,
            String nickName,
            LocalDate birth,
            String phoneNumber,
            UserType userType
    ) {
        this.userId = userId;
        this.password = password;
        this.socialType = socialType != null ? socialType : SocialType.LOCAL;
        this.name = name;
        this.nickName = nickName;
        this.birth = birth;
        this.phoneNumber = phoneNumber;
        this.userType = userType != null ? userType : UserType.USER;
    }

    public void updatePassword(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new IllegalArgumentException("새로운 비밀번호는 비어 있을 수 없습니다.");
        }

        this.password = encodedPassword;
    }

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("새로운 이름은 비어 있을 수 없습니다.");
        }

        this.name = name;
    }

    public void updateNickName(String nickName) {
        if (nickName == null || nickName.isBlank()) {
            throw new IllegalArgumentException("새로운 별명은 비어 있을 수 없습니다.");
        }

        this.nickName = nickName;
    }

    public void updateBirth(LocalDate birth) {
        if (birth == null) {
            throw new IllegalArgumentException("생년월일은 비어 있을 수 없습니다.");
        }

        this.birth = birth;
    }
}
