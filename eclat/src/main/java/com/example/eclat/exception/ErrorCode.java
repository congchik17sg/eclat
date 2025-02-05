package com.example.eclat.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    KEY_INVALID(1002, "Invalid key", HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_INVALID(1002, "Username at least 8 characters", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Exception", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "User Existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1003, "User Not Existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1004, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1005, "you do not have permission", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND(1006, "you do not have permission", HttpStatus.NOT_FOUND),
    ;

    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
