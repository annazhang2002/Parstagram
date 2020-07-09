package com.example.parstagram.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parstagram.EndlessRecyclerViewScrollListener;
import com.example.parstagram.models.Post;
import com.example.parstagram.adapters.PostsAdapter;
import com.example.parstagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {
    private static final String TAG = "PostsFragment";
    RecyclerView rvPosts;
    PostsAdapter adapter;
    List<Post> allPosts;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    ProgressDialog pd;

    public PostsFragment() {
        // Required empty public constructor
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
        // initialize all of the components for the recycler view of posts
        rvPosts = view.findViewById(R.id.rvPosts);
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts);
        rvPosts.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvPosts.setLayoutManager(layoutManager);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                queryPosts();
            }
        });

        // initialize the progress dialog
        createProgressDialog();

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

    public void createProgressDialog() {
        pd = new ProgressDialog(getContext());
        pd.setTitle("Loading posts...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
    }

    // method to load more posts on scroll
    // takes in the current number of posts
    protected void queryMorePosts(int currNumPosts) {
        Log.i(TAG, "queryMorePosts");
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(10);
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

    protected void queryPosts() {
        Log.i(TAG, "queryPosts");
        pd.show();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
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
                pd.dismiss();
            }
        });
    }
}