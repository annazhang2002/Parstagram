package com.example.parstagram.models;

import android.text.format.DateUtils;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//@Parcel
@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "Image";
    public static final String KEY_USER = "author";
    public static final String KEY_CREATEDAT = "createdAt";
    public static final String KEY_PROFILEPIC = "profilePic";
    public static final String KEY_NAME = "name";
    public static final String KEY_BIO = "bio";
    private static final String KEY_LIKES = "likes";
    private static final String TAG = "Post";

    //
    public Post() {}

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

    public int getLikeNum() {
        JSONArray likes = getJSONArray(KEY_LIKES);
        if (likes != null) {
            return likes.length();
        } else {
            return 0;
        }
    }

    public void addLike() {
        Log.i(TAG, "adding like!");
        JSONArray likes = getJSONArray(KEY_LIKES);
        List<String> newLikes = new ArrayList<>();
        if (likes != null) {
            for (int i =0; i< likes.length(); i++) {
                try {
                    String username = likes.getString(i);
                    if (username.equals(ParseUser.getCurrentUser().getUsername())) {
                        return;
                    }
                    newLikes.add(username);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        newLikes.add(ParseUser.getCurrentUser().getUsername());
        put(KEY_LIKES, newLikes);

    }

    public String getTimestamp() {
        String createdAt = getCreatedAt().toString();
        return getSimpleDate(createdAt);
    }

    public String getRelativeTime() {
        String createdAt = getCreatedAt().toString();
        return getRelativeTimeAgo(createdAt);
    }

    public ParseFile getProfile() {
        return getParseUser(KEY_USER).getParseFile(KEY_PROFILEPIC);
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
    public static String getSimpleDate(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        String newFormat = "MMMM dd, yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.applyPattern(newFormat);
        String newDate = sf.format(new Date(rawJsonDate));
        Log.i("Tweet", "newDate: " + newDate);
        return newDate;
    }


}
