package com.example.worker;

public class FeedBack {
    private String message;
    private int statusCode;

    public FeedBack() {
    }

    public FeedBack(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "FeedBack{" +
                "message='" + message + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
