package com.ashcollege.responses;

import com.ashcollege.entities.User;

public class UserDetailsResponse extends BasicResponse{
    private User user;

    public UserDetailsResponse(boolean success, Integer errorCode, User user) {
        super(success, errorCode);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
