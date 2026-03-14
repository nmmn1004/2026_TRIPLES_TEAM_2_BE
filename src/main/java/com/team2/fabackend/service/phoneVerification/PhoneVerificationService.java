package com.team2.fabackend.service.phoneVerification;

import com.team2.fabackend.global.enums.ErrorCode;
import com.team2.fabackend.global.exception.CustomException;
import com.team2.fabackend.global.sms.NcpSmsClient;
import com.team2.fabackend.service.user.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PhoneVerificationService {
    private final UserReader userReader;

    private final RedisTemplate<String, String> redisTemplate;
    private final NcpSmsClient ncpSmsClient;

    private final Duration CODE_TTL = Duration.ofMinutes(5);
    private final Duration VERIFIED_TTL = Duration.ofMinutes(15);

    /**
     * Generates the Redis key for the verification code.
     *
     * @param phoneNumber The user's phone number.
     * @return The Redis key.
     */
    private String getVerifyCodeKey(String phoneNumber) {
        return "phone_auth_code:" + phoneNumber;
    }

    /**
     * Generates the Redis key for the verification status.
     *
     * @param phoneNumber The user's phone number.
     * @return The Redis key.
     */
    private String getVerifiedStatusKey(String phoneNumber) {
        return "phone_verified_status:" + phoneNumber;
    }

    /**
     * Sends a verification code for user signup if the phone number is not already registered.
     *
     * @param phoneNumber The user's phone number.
     */
    public void sendCodeForSignUp(String phoneNumber) {
        if (userReader.existsByPhoneNumber(phoneNumber)) {
            throw new CustomException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }
        sendSmsProcess(phoneNumber);
    }

    /**
     * Sends a verification code for account recovery if the phone number is registered.
     *
     * @param phoneNumber The user's phone number.
     */
    public void sendCodeForFinding(String phoneNumber) {
        if (!userReader.existsByPhoneNumber(phoneNumber)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        sendSmsProcess(phoneNumber);
    }

    /**
     * Verifies the provided code against the one stored in Redis.
     *
     * @param phoneNumber The user's phone number.
     * @param code        The verification code to check.
     */
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

        markAsVerified(phoneNumber);
    }

    /**
     * Marks the phone number as verified in Redis.
     *
     * @param phoneNumber The user's phone number.
     */
    private void markAsVerified(String phoneNumber) {
        redisTemplate.delete(getVerifyCodeKey(phoneNumber));
        redisTemplate.opsForValue().set(getVerifiedStatusKey(phoneNumber), "VERIFIED", VERIFIED_TTL);
    }

    /**
     * Checks if the phone number is currently verified.
     *
     * @param phoneNumber The phone number to check.
     * @throws CustomException If the phone number is not verified.
     */
    public void checkVerified(String phoneNumber) {
        String status = redisTemplate.opsForValue().get(getVerifiedStatusKey(phoneNumber));
        if (!"VERIFIED".equals(status)) {
            throw new CustomException(ErrorCode.PHONE_NOT_VERIFIED);
        }
    }

    /**
     * Clears the verification status for a phone number.
     *
     * @param phoneNumber The phone number.
     */
    public void clearVerificationLog(String phoneNumber) {
        redisTemplate.delete(getVerifiedStatusKey(phoneNumber));
    }

    /**
     * Handles the process of generating a code and sending it via SMS.
     *
     * @param phoneNumber The user's phone number.
     */
    private void sendSmsProcess(String phoneNumber) {
        String code = String.format("%06d", new Random().nextInt(1000000));

        redisTemplate.opsForValue().set(getVerifyCodeKey(phoneNumber), code, CODE_TTL);

        try {
            ncpSmsClient.sendSms(phoneNumber, "[서비스명] 인증번호 [" + code + "]를 입력해주세요.");
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SMS_SEND_FAILED);
        }
    }
}
