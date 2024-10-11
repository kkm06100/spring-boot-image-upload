package com.gdsc.imageupload.global.exception;

public enum ErrorCode {
        EMPTY_FILE_EXCEPTION("File is empty or filename is missing"),
        IO_EXCEPTION_ON_IMAGE_UPLOAD("IO Exception occurred during image upload"),

        NO_FILE_EXTENTION("File is not exist"),
        INVALID_FILE_EXTENTION("File is invalid"),
        PUT_OBJECT_EXCEPTION(""),
        EXCEPTION_ON_LIST_IMAGES(""),

        IO_EXCEPTION_ON_IMAGE_DELETE("");

        private final String message;

        ErrorCode(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

}
