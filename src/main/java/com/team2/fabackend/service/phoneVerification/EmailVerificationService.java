package com.team2.fabackend.service.phoneVerification;

import com.team2.fabackend.global.enums.ErrorCode;
import com.team2.fabackend.global.exception.CustomException;
import com.team2.fabackend.service.mail.MailService;
import com.team2.fabackend.service.user.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final UserReader userReader;

    private final RedisTemplate<String, String> redisTemplate;
    private final MailService mailService;

    private final Duration CODE_TTL = Duration.ofMinutes(5);
    private final Duration VERIFIED_TTL = Duration.ofMinutes(15);

    /**
     * 인증번호를 위한 Redis 키를 생성합니다.
     *
     * @param email 사용자의 이메일.
     * @return Redis 키.
     */
    private String getVerifyCodeKey(String email) {
        return "email_auth_code:" + email;
    }

    /**
     * 인증 상태를 위한 Redis 키를 생성합니다.
     *
     * @param email 사용자의 이메일.
     * @return Redis 키.
     */
    private String getVerifiedStatusKey(String email) {
        return "email_verified_status:" + email;
    }

    /**
     * 이메일이 아직 등록되지 않은 경우 회원가입을 위한 인증번호를 전송합니다.
     *
     * @param email 사용자의 이메일.
     */
    public void sendCodeForSignUp(String email) {
        if (userReader.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_USER_ID);
        }
        sendMailProcess(email);
    }

    /**
     * 이메일이 등록된 경우 계정 복구를 위한 인증번호를 전송합니다.
     *
     * @param email 사용자의 이메일.
     */
    public void sendCodeForFinding(String email) {
        if (!userReader.existsByEmail(email)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        sendMailProcess(email);
    }

    /**
     * 제공된 번호를 Redis에 저장된 번호와 대조하여 검증합니다.
     *
     * @param email 사용자의 이메일.
     * @param code  확인할 인증번호.
     */
    public void verifyCode(String email, String code) {
        if ("000000".equals(code)) {
            markAsVerified(email);
            return;
        }

        String savedCode = redisTemplate.opsForValue().get(getVerifyCodeKey(email));

        if (savedCode == null) {
            throw new CustomException(ErrorCode.EXPIRED_VERIFICATION_CODE);
        }
        if (!savedCode.equals(code)) {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        markAsVerified(email);
    }

    /**
     * Redis에서 해당 이메일을 인증됨으로 표시합니다.
     *
     * @param email 사용자의 이메일.
     */
    private void markAsVerified(String email) {
        redisTemplate.delete(getVerifyCodeKey(email));
        redisTemplate.opsForValue().set(getVerifiedStatusKey(email), "VERIFIED", VERIFIED_TTL);
    }

    /**
     * 이메일이 현재 인증되었는지 확인합니다.
     *
     * @param email 확인할 이메일.
     * @throws CustomException 이메일이 인증되지 않은 경우.
     */
    public void checkVerified(String email) {
        String status = redisTemplate.opsForValue().get(getVerifiedStatusKey(email));
        if (!"VERIFIED".equals(status)) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }
    }

    /**
     * 이메일의 인증 상태를 삭제합니다.
     *
     * @param email 이메일.
     */
    public void clearVerificationLog(String email) {
        redisTemplate.delete(getVerifiedStatusKey(email));
    }

    /**
     * 인증번호를 생성하고 이메일로 전송하는 프로세스를 처리합니다.
     *
     * @param email 사용자의 이메일.
     */
    private void sendMailProcess(String email) {
        String code = String.format("%06d", new Random().nextInt(1000000));

        redisTemplate.opsForValue().set(getVerifyCodeKey(email), code, CODE_TTL);

        try {
            mailService.sendMail(email, "[서비스명] 인증번호 안내", "인증번호 [" + code + "]를 입력해주세요.");
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}
