package com.example.project_android.model;

public class ApiResponse {
    private boolean successful;
    private int code;
    private String message;

    public ApiResponse(boolean successful, int code) {
        this.successful = successful;
        this.code = code;
    }

    public ApiResponse(boolean successful, String message) {
        this.successful = successful;
        this.message = message;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public int getCode() {
        return code;
    }
}
