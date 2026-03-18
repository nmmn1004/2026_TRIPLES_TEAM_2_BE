package com.team2.fabackend.global.exception;

import com.team2.fabackend.api.error.dto.ErrorResponse;
import com.team2.fabackend.global.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * CustomException을 처리하고 적절한 상태 코드와 함께 ErrorResponse를 반환합니다.
     *
     * @param e CustomException 인스턴스.
     * @return ErrorResponse를 포함하는 ResponseEntity.
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorCode ec = e.getErrorCode();
        return ResponseEntity.status(ec.getStatus())
                .body(new ErrorResponse(ec.getCode(), ec.getMessage()));
    }

    /**
     * 처리되지 않은 모든 예외를 처리하고 일반적인 내부 서버 오류 응답을 반환합니다.
     *
     * @param e 예외 인스턴스.
     * @return 일반적인 ErrorResponse를 포함하는 ResponseEntity.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("S001", "서버 오류가 발생했습니다."));
    }
}
