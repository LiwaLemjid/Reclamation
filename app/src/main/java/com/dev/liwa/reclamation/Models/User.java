package com.dev.liwa.reclamation.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String user_id;
    private String email;
    private String password;
    private String username;

    public User() {
    }

    public User(String user_id, String email, String password, String username) {
        this.user_id = user_id;
        this.email = email;
        this.password = password;
        this.username = username;
    }

    protected User(Parcel in) {
        user_id = in.readString();
        email = in.readString();
        password = in.readString();
        username = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(username);
    }
}