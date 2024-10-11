package com.gdsc.imageupload.global.exception.custom;

import com.gdsc.imageupload.global.exception.ErrorCode;

public class S3Exception extends RuntimeException{
    private final ErrorCode errorCode;

    public S3Exception(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "S3Exception{" +
                "errorCode=" + errorCode +
                ", message=" + errorCode.getMessage() +
                '}';
    }
}
