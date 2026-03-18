package com.team2.fabackend.service.user;

import com.team2.fabackend.api.user.dto.UserDeleteRequest;
import com.team2.fabackend.api.user.dto.UserInfoRequest;
import com.team2.fabackend.api.user.dto.UserInfoResponse;
import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.domain.user.UserDeleteReason;
import com.team2.fabackend.global.enums.ErrorCode;
import com.team2.fabackend.global.exception.CustomException;
import com.team2.fabackend.service.auth.AuthVerificationService;
import com.team2.fabackend.service.auth.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserReader userReader;
    private final UserWriter userWriter;

    private final AuthVerificationService authVerificationService;
    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder passwordEncoder;

    /**
     * 특정 사용자의 상세 정보를 조회합니다.
     * 
     * @param id 유저 식별자
     * @return 사용자의 정보를 담은 응답 DTO
     */
    @Transactional
    public UserInfoResponse getUser(Long id) {
        User user = userReader.findById(id);

        return UserInfoResponse.from(user);
    }

    /**
     * 전체 사용자 목록을 페이징 처리하여 조회합니다.
     * 
     * @param pageable 페이징 정보 (페이지 번호, 사이즈, 정렬 등)
     * @return 페이징 처리된 사용자 정보 응답
     */
    @Transactional
    public Page<UserInfoResponse> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userReader.findAllUsers(pageable);

        return usersPage.map(UserInfoResponse::from);
    }

    /**
     * 사용자의 현재 비밀번호가 일치하는지 검증하고, 성공 시 2차 인증용 토큰을 발급합니다.
     * 발급된 토큰은 정보 수정이나 탈퇴 등 민감한 작업 시 헤더에 포함하여 사용됩니다.
     * 
     * @param userId 유저 식별자
     * @param rawPassword 검증할 평문 비밀번호
     * @return 발급된 2차 인증용 토큰 (UUID)
     */
    @Transactional(readOnly = true)
    public String verifyCurrentPassword(Long userId, String rawPassword) {
        User user = userReader.findById(userId);

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String confirmToken = UUID.randomUUID().toString();
        authVerificationService.saveVerificationToken(userId, confirmToken);

        return confirmToken;
    }

    /**
     * 2차 인증 토큰 검증 후 사용자의 비밀번호를 새로운 비밀번호로 변경합니다.
     * 
     * @param userId 유저 식별자
     * @param confirmToken 발급받은 2차 인증 토큰
     * @param newPassword 새로 설정할 비밀번호
     */
    @Transactional
    public void updatePassword(Long userId, String confirmToken, String newPassword) {
        authVerificationService.validateVerificationToken(userId, confirmToken);

        User user = userReader.findById(userId);
        String encodedPassword = passwordEncoder.encode(newPassword);

        userWriter.updatePassword(user, encodedPassword);

        authVerificationService.deleteVerification(userId);
    }

    /**
     * 2차 인증 토큰 검증 후 사용자의 기본 프로필 정보(닉네임, 생년월일)를 수정합니다.
     * 
     * @param userId 유저 식별자
     * @param passwordConfirmToken 발급받은 2차 인증 토큰
     * @param request 수정할 프로필 정보가 담긴 DTO
     */
    @Transactional
    public void updateProfile(Long userId, String passwordConfirmToken, UserInfoRequest request) {
        authVerificationService.validateVerificationToken(userId, passwordConfirmToken);

        User user = userReader.findById(userId);

        userWriter.updateProfile(user, request.getNickName(), request.getBirth());

        authVerificationService.deleteVerification(userId);
    }

    /**
     * 2차 인증 토큰 검증 후 사용자의 계정을 탈퇴 처리합니다.
     * 탈퇴 시 사유를 별도로 저장하며, 연관된 리프레시 토큰 등을 정리합니다.
     * 
     * @param userId 유저 식별자
     * @param passwordConfirmToken 발급받은 2차 인증 토큰
     * @param request 탈퇴 사유 정보가 담긴 DTO
     */
    @Transactional
    public void deleteUser(Long userId, String passwordConfirmToken, UserDeleteRequest request) {
        authVerificationService.validateVerificationToken(userId, passwordConfirmToken);

        User user = userReader.findById(userId);

        UserDeleteReason reason = UserDeleteReason.builder()
                .birthDate(user.getBirth())
                .reason(request.getReason())
                .reason_detail(request.getReason_detail())
                .build();

        userWriter.createReason(reason);

        userWriter.delete(user);

        authVerificationService.deleteVerification(userId);
        refreshTokenService.deleteRefreshToken(userId);
    }
}
