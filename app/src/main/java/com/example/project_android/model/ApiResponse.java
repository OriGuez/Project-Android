package com.example.project_android.model;

public class ApiResponse {
    private boolean successful;
    private int code;
    private String message;
    private String _id;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
    public void setSuccessful(boolean isSuccess){
        this.successful = isSuccess;
    }
    public void setCode(int code){
        this.code = code;
    }
}
