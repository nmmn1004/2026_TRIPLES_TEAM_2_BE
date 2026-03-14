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
     * Handles CustomException and returns an ErrorResponse with the appropriate status code.
     *
     * @param e The CustomException instance.
     * @return A ResponseEntity containing the ErrorResponse.
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorCode ec = e.getErrorCode();
        return ResponseEntity.status(ec.getStatus())
                .body(new ErrorResponse(ec.getCode(), ec.getMessage()));
    }

    /**
     * Handles all unhandled exceptions and returns a generic internal server error response.
     *
     * @param e The exception instance.
     * @return A ResponseEntity containing a generic ErrorResponse.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("S001", "서버 오류가 발생했습니다."));
    }
}
