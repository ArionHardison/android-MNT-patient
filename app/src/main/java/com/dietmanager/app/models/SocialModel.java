package com.dietmanager.app.models;

import java.io.Serializable;

/**
 * Created by Prasanth on 09-10-2019.
 */
public class SocialModel implements Serializable {

    String mAccessToken;
    String mName;
    String mEmail;
    String mImageUrl;
    String mLoginBy;

    public String getmAccessToken() {
        return mAccessToken;
    }

    public void setmAccessToken(String mAccessToken) {
        this.mAccessToken = mAccessToken;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmLoginBy() {
        return mLoginBy;
    }

    public void setmLoginBy(String mLoginBy) {
        this.mLoginBy = mLoginBy;
    }
}
