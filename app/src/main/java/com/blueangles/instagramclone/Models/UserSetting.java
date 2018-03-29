package com.blueangles.instagramclone.Models;

/**
 * Created by Ashith VL on 10/18/2017.
 */

public class UserSetting {

    private User user;
    private UserAccountSetting userAccountSetting;

    public UserSetting(User user, UserAccountSetting userAccountSetting) {
        this.user = user;
        this.userAccountSetting = userAccountSetting;
    }

    public UserSetting() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAccountSetting getUserAccountSetting() {
        return userAccountSetting;
    }

    public void setUserAccountSetting(UserAccountSetting userAccountSetting) {
        this.userAccountSetting = userAccountSetting;
    }
}
