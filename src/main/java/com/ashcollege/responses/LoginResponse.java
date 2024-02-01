package com.ashcollege.responses;

import com.ashcollege.entities.User;

public class LoginResponse extends BasicResponse {
    //private int id;
    private User user;

    public LoginResponse(boolean success, Integer errorCode,User user) {
        super(success, errorCode);
        //this.id = id;
        this.user=user;
    }

    public LoginResponse(boolean success, Integer errorCode) {
        super(success, errorCode);
    }

    /*public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
