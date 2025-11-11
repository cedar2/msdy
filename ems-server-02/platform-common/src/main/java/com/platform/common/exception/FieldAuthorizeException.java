package com.platform.common.exception;

/**
 * 字段数据权限异常
 *
 * @author chenkw
 */
public class FieldAuthorizeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public FieldAuthorizeException() {
    }

    public FieldAuthorizeException(String message) {
        this.message = message;
    }
}
