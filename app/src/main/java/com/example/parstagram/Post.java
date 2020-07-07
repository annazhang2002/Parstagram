package com.example.parstagram;

import android.text.format.DateUtils;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.Locale;

//@Parcel
@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "Image";
    public static final String KEY_USER = "author";
    public static final String KEY_CREATEDAT = "createdAt";
    public static final String KEY_PROFILEPIC = "profilePic";
//
//    public Post() {}

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getTimestamp() {
        String createdAt = getCreatedAt().toString();
        return getRelativeTimeAgo(createdAt);
    }
// getRelativeTimeAgo("2020-07-07T16:07:26.465Z");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String parseFormat = "EEE MMM dd hh:mm:ss zzz yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(parseFormat, Locale.ENGLISH);

        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();

            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE ).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }


        return relativeDate;
    }
}
