package com.team2.fabackend.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."), 
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "U002", "비밀번호가 일치하지 않습니다."), 
    INSUFFICIENT_ADMIN_AUTHORITY(HttpStatus.FORBIDDEN, "U003", "관리자 권한이 필요합니다."),
    USER_LOCKED(HttpStatus.FORBIDDEN, "U004", "비밀번호 5회 실패로 계정이 잠겼습니다. 관리자에게 문의하세요."),
    USER_DELETED(HttpStatus.FORBIDDEN, "U005", "삭제된 계정입니다."),

    DUPLICATE_USER_ID(HttpStatus.CONFLICT, "A001", "이미 가입된 이메일입니다."), 
    DUPLICATE_DEVICE_ID(HttpStatus.CONFLICT, "A002", "해당 기기에서 이미 가입된 아이디가 존재합니다."), 
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "A003", "이미 사용 중인 닉네임입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "T001", "유효하지 않거나 만료된 리프레시 토큰입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "T001", "리프레시 토큰이 만료되었습니다. 다시 로그인해주세요."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "T002", "리프레시 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "T003", "리프레시 토큰이 일치하지 않습니다. 보안 위험으로 인해 로그아웃됩니다."),

    INVALID_VERIFICATION_TOKEN(HttpStatus.FORBIDDEN, "V001", "인증 토큰이 만료되었거나 유효하지 않습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "S002", "잘못된 입력값입니다."), 
    INVALID_DATA_VALUE(HttpStatus.BAD_REQUEST, "S003", "해당 값을 찾을 수 없습니다."),
    AI_REPORT_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S004", "AI 리포트 생성에 실패했습니다."),

    EXPIRED_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "P002", "인증번호가 만료되었습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "P003", "인증번호가 일치하지 않습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "P004", "이메일 인증이 완료되지 않았거나 시간이 초과되었습니다."),

    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "이메일 발송에 실패하였습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
