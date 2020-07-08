package com.example.parstagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parstagram.EndlessRecyclerViewScrollListener;
import com.example.parstagram.GridPostsAdapter;
import com.example.parstagram.Post;
import com.example.parstagram.PostsAdapter;
import com.example.parstagram.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfilePostsGridFragment extends PostsFragment{

    private static final String TAG = "ProfilePostsGridFragmen";
    RecyclerView rvPosts;
    GridPostsAdapter adapter;
    List<Post> allPosts;
    EndlessRecyclerViewScrollListener scrollListener;

    ParseUser user;

    public ProfilePostsGridFragment(ParseUser user) {
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);
        allPosts = new ArrayList<>();
        adapter = new GridPostsAdapter(getContext(), allPosts);
        rvPosts.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false);
        rvPosts.setLayoutManager(layoutManager);

        // implementing endless scroll
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                queryMorePosts(allPosts.size());
            }
        };
        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);
        queryPosts();
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

    @Override
    protected void queryPosts() {
        Log.i(TAG, "queryPosts");
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, user);
        query.setLimit(10);
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
                adapter.clear();
                adapter.addAll(posts);
                swipeContainer.setRefreshing(false);
            }
        });
    }
}