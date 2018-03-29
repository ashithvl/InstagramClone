package com.blueangles.instagramclone.Models;

/**
 * Created by Ashith VL on 10/14/2017.
 */


/**
 * variable name must be same as entered in Firebase
 */
public class User {
    private String email;
    private long phone_number;
    private String user_id;
    private String user_name;

    public User() {
    }

    public User(String email, long phone_number, String user_id, String user_name) {
        this.email = email;
        this.phone_number = phone_number;
        this.user_id = user_id;
        this.user_name = user_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
