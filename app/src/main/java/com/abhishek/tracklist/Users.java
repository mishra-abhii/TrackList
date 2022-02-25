package com.abhishek.tracklist;

public class Users {
    String userName , email , userId;

    public Users(String userName, String email , String userId) {
        this.userName = userName;
        this.email = email;
        this.userId = userId;
    }

    public Users(){
//        empty constructors for firebase
    }

//  Getters and setters for all the above variables
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
