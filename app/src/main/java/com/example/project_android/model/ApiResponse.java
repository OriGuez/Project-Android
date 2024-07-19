package com.example.project_android.model;

public class ApiResponse {
    private boolean successful;
    private int code;

    public ApiResponse(boolean successful, int code) {
        this.successful = successful;
        this.code = code;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public int getCode() {
        return code;
    }
}
