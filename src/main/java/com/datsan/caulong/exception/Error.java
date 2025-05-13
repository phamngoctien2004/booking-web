package com.datsan.caulong.exception;

public enum Error {
    USER_NOT_FOUND("U01","Người dùng không tồn tại"),
    PASSWORD_INCORRECT("U02", "Mâu khẩu không đúng"),
    USER_UNACTiVED("U03", "Người dùng chưa được kích hoạt"),
    USER_EXISTED("U04", "Email đã tồn tại"),
    VERIFY_FAILED("U05", "Xác thực không hợp lệ");
    private String id;
    private String message;

    Error(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
