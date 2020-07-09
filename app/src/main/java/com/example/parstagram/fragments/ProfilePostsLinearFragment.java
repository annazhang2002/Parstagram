package com.example.parstagram.fragments;

import android.util.Log;

import com.example.parstagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfilePostsLinearFragment extends PostsFragment{

    private static final String TAG = "ProfileFragment" ;

    ParseUser user;

    public ProfilePostsLinearFragment(ParseUser user) {
        this.user = user;
    }

    @Override
    protected void queryPosts() {
        Log.i(TAG, "queryPosts");
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(10);
        query.whereEqualTo(Post.KEY_USER, user);
        query.addDescendingOrder(Post.KEY_CREATEDAT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                }
                for (Post post: posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username " + post.getUser().getUsername());
                }
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    // method to load more posts on scroll
    // takes in the current number of posts
    protected void queryMorePosts(int currNumPosts) {
        Log.i(TAG, "queryMorePosts");
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(10);
        query.whereEqualTo(Post.KEY_USER, user);
        query.setSkip(currNumPosts);
        query.addDescendingOrder(Post.KEY_CREATEDAT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                }
                for (Post post: posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username " + post.getUser().getUsername());
                }
                adapter.addAll(posts);
            }
        });
    }
}
