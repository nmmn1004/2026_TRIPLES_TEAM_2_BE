package com.team2.fabackend.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    /**
     * [U] User 관련 에러 (사용자 정보 및 상태)
     */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."), // DB에 해당 ID가 없는 경우
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "U002", "비밀번호가 일치하지 않습니다."), // 로그인/비밀번호 확인 시 불일치

    /**
     * [A] Auth 관련 에러 (회원가입 및 본인인증)
     */
    DUPLICATE_USER_ID(HttpStatus.CONFLICT, "A001", "이미 가입된 아이디입니다."), // 회원가입 시 ID(이메일) 중복
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "A002", "이미 가입된 전화번호입니다."), // 회원가입 시 번호 중복

    /**
     * [T] Token 관련 에러 (JWT, Refresh Token)
     */
    // AOS 가이드: 아래 T 계열 에러 발생 시 앱 내 저장된 모든 토큰을 삭제하고 로그인 화면으로 유도
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "T001", "유효하지 않거나 만료된 리프레시 토큰입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "T001", "리프레시 토큰이 만료되었습니다. 다시 로그인해주세요."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "T002", "리프레시 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "T003", "리프레시 토큰이 일치하지 않습니다. 보안 위험으로 인해 로그아웃됩니다."),

    /**
     * [V] Verification 관련 에러 (비밀번호 확인 후 2단계 인증 티켓)
     */
    // AOS 가이드: 헤더에 담은 X-Password-Confirm_Token이 만료되었을 때 발생 (재인증 필요 팝업 노출)
    INVALID_VERIFICATION_TOKEN(HttpStatus.FORBIDDEN, "V001", "인증 토큰이 만료되었거나 유효하지 않습니다."),

    /**
     * [S] System 관련 에러 (공통)
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "S002", "잘못된 입력값입니다."), // @Valid 검증 실패 시 등
    INVALID_DATA_VALUE(HttpStatus.BAD_REQUEST, "S003", "해당 값을 찾을 수 없습니다."),

    /**
     * [P] Phone Verification 관련 에러
     */
    SMS_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "P001", "SMS 발송에 실패했습니다."),
    EXPIRED_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "P002", "인증번호가 만료되었습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "P003", "인증번호가 일치하지 않습니다."),
    PHONE_NOT_VERIFIED(HttpStatus.FORBIDDEN, "P004", "전화번호 인증이 완료되지 않았거나 시간이 초과되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
