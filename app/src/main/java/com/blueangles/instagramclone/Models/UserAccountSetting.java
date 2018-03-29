package com.blueangles.instagramclone.Models;

/**
 * Created by Ashith VL on 10/14/2017.
 */

public class UserAccountSetting {
    private String description;
    private String display_name;
    private String profile_path;
    private String username;
    private String website;
    private String user_id;
    private long followers;
    private long following;
    private long post;

    public UserAccountSetting() {
    }

    public UserAccountSetting(String description, String display_name, String profile_path,
                              String username, String website, long followers, long following, long post, String user_id) {
        this.description = description;
        this.display_name = display_name;
        this.profile_path = profile_path;
        this.username = username;
        this.website = website;
        this.followers = followers;
        this.following = following;
        this.post = post;
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getPost() {
        return post;
    }

    public void setPost(long post) {
        this.post = post;
    }
}
