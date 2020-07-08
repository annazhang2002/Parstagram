package com.example.parstagram.models;

import android.text.format.DateUtils;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//@Parcel
@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_POST = "post";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_PROFILEPIC = "profilePic";
    public static final String KEY_CREATEDAT = "createdAt";

    public Comment() {}

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
    public ParseObject getCommentPost() {
        return getParseObject(KEY_POST);
    }
    public String getMessage() {
        return getString(KEY_MESSAGE);
    }
    public ParseFile getProfile() {
        return getParseUser(KEY_USER).getParseFile(KEY_PROFILEPIC);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
    public void setCommentPost(Post post) {
        put(KEY_POST, post);
    }
    public void setMessage(String message) {
        put(KEY_MESSAGE, message);
    }


}
