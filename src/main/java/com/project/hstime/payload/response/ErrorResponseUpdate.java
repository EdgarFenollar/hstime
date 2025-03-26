package com.project.hstime.payload.response;

public class ErrorResponseUpdate {
    private String message;

    public ErrorResponseUpdate(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}