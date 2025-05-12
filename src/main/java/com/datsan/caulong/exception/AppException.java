package com.datsan.caulong.exception;

public class AppException extends RuntimeException{
    private Error error;

    public AppException(Error error) {
        super();
        this.error = error;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
