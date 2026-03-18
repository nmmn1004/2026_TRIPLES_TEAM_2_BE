package com.team2.fabackend.domain.user;

import com.team2.fabackend.global.enums.SocialType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 사용자 email과 소셜 로그인 유형으로 사용자를 찾습니다.
     *
     * @param email      사용자의 email입니다.
     * @param socialType 소셜 로그인 유형입니다.
     * @return 발견된 경우 User를 포함하는 Optional입니다.
     */
    Optional<User> findByEmailAndSocialType(String email, SocialType socialType);

    /**
     * 모든 사용자의 페이지가 지정된 목록을 검색합니다.
     *
     * @param pageable 페이지네이션 정보입니다.
     * @return User 엔티티의 Page입니다.
     */
    @NotNull Page<User> findAll(@NotNull Pageable pageable);

    /**
     * 주어진 사용자 email과 소셜 로그인 유형을 가진 사용자가 존재하는지 확인합니다.
     *
     * @param email      사용자의 email입니다.
     * @param socialType 소셜 로그인 유형입니다.
     * @return 사용자가 존재하면 true, 그렇지 않으면 false입니다.
     */
    boolean existsByEmailAndSocialType(String email, SocialType socialType);

    /**
     * 주어진 기기 ID를 가진 사용자가 존재하는지 확인합니다.
     *
     * @param deviceId 기기 고유 ID입니다.
     * @return 사용자가 존재하면 true, 그렇지 않으면 false입니다.
     */
    boolean existsByDeviceId(String deviceId);

    /**
     * 주어진 닉네임을 가진 사용자가 존재하는지 확인합니다.
     *
     * @param nickName 닉네임입니다.
     * @return 사용자가 존재하면 true, 그렇지 않으면 false입니다.
     */
    boolean existsByNickName(String nickName);
}
