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

import java.time.LocalDateTime;

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
    private String email;
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
    private Integer birthYear;
    @Enumerated(value = EnumType.STRING)
    private UserType userType = UserType.USER;

    @Builder
    protected User(
            String email,
            String password,
            SocialType socialType,
            String name,
            String nickName,
            Integer birthYear,
            String phoneNumber,
            UserType userType
    ) {
        this.email = email;
        this.password = password;
        this.socialType = socialType != null ? socialType : SocialType.LOCAL;
        this.name = name;
        this.nickName = nickName;
        this.birthYear = birthYear;
        this.phoneNumber = phoneNumber;
        this.userType = userType != null ? userType : UserType.USER;
    }

    public void changePassword(String encodedPassword) {

    }
}
