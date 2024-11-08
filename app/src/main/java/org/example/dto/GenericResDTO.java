package org.example.dto;

public class GenericResDTO {
    private int httpStatus;
    private boolean isSuccess;
    private String message;

    public int getHttpStatus() {
        return httpStatus;
    }
    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }
    public boolean isSuccess() {
        return isSuccess;
    }
    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
