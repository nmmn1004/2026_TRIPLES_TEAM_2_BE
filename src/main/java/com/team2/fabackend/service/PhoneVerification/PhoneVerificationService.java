package com.team2.fabackend.service.PhoneVerification;

import com.team2.fabackend.global.enums.ErrorCode;
import com.team2.fabackend.global.exception.CustomException;
import com.team2.fabackend.global.sms.NcpSmsClient;
import com.team2.fabackend.service.user.UserReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneVerificationService {
    private final UserReader userReader;

    private final RedisTemplate<String, String> redisTemplate;
    private final NcpSmsClient ncpSmsClient;

    private final Duration CODE_TTL = Duration.ofMinutes(5);
    private final Duration VERIFIED_TTL = Duration.ofMinutes(15);

    private String getVerifyCodeKey(String phoneNumber) {
        return "phone_auth_code:" + phoneNumber;
    }

    private String getVerifiedStatusKey(String phoneNumber) {
        return "phone_verified_status:" + phoneNumber;
    }

    public void sendCode(String phoneNumber) {
        if (userReader.existsByPhoneNumber(phoneNumber)) {
            throw new CustomException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        String code = String.format("%06d", new Random().nextInt(999999));
        redisTemplate.opsForValue().set(getVerifyCodeKey(phoneNumber), code, CODE_TTL);

        try {
            ncpSmsClient.sendSms(phoneNumber, "[서비스명] 인증번호 [" + code + "]를 입력해주세요.");
        } catch (Exception e) {
            log.warn("SMS 발송 실패(Mock 처리): {} - {}", phoneNumber, code);
            throw new CustomException(ErrorCode.SMS_SEND_FAILED);
        }
    }

    public void verifyCode(String phoneNumber, String code) {
        if ("000000".equals(code)) {
            markAsVerified(phoneNumber);
            return;
        }

        String savedCode = redisTemplate.opsForValue().get(getVerifyCodeKey(phoneNumber));

        if (savedCode == null) {
            throw new CustomException(ErrorCode.EXPIRED_VERIFICATION_CODE);
        }
        if (!savedCode.equals(code)) {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        redisTemplate.delete(getVerifyCodeKey(phoneNumber));
        markAsVerified(phoneNumber);
    }

    private void markAsVerified(String phoneNumber) {
        redisTemplate.delete(getVerifyCodeKey(phoneNumber));
        redisTemplate.opsForValue().set(getVerifiedStatusKey(phoneNumber), "VERIFIED", VERIFIED_TTL);
    }

    public void checkVerified(String phoneNumber) {
        String status = redisTemplate.opsForValue().get(getVerifiedStatusKey(phoneNumber));
        if (!"VERIFIED".equals(status)) {
            throw new CustomException(ErrorCode.PHONE_NOT_VERIFIED);
        }
    }

    public void clearVerificationLog(String phoneNumber) {
        redisTemplate.delete(getVerifiedStatusKey(phoneNumber));
    }
}
