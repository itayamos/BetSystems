package com.ashcollege.responses;

public class JsonResponse extends BasicResponse{
    private String message;

    public JsonResponse(boolean success, Integer errorCode, String message) {
        super(success, errorCode);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setData(String message) {
        this.message = message;
    }
}
