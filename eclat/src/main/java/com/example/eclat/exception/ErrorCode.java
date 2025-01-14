package com.example.eclat.exception;

public enum ErrorCode {
    KEY_INVALID(1002 , "Invalid key"),
    USERNAME_INVALID(1002 , "Username at least 8 characters"),
    UNCATEGORIZED_EXCEPTION(9999 , "Uncategorized Exception"),
    USER_EXISTED(1001 , "User Existed"),
    USER_NOT_EXISTED(1003 , "User Not Existed"),
    UNAUTHENTICATED(1004 , "Unauthenticated"),
    ;

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
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
