package com.example.parstagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.parstagram.R;
import com.example.parstagram.adapters.CommentAdapter;
import com.example.parstagram.adapters.PostsAdapter;
import com.example.parstagram.fragments.ComposeDialogFragment;
import com.example.parstagram.models.Comment;
import com.example.parstagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity implements ComposeDialogFragment.EditNameDialogListener  {

    private static final String TAG = "PostDetailsActivity";
    TextView tvName;
    TextView tvTimestamp;
    TextView tvDescription;
    ImageView ivImage;
    RecyclerView rvComments;
    static CommentAdapter adapter;
    List<Comment> allComments;
    ImageView ivComment;
    ImageView ivLike;
    ImageView ivProfile;
    TextView tvLikeNum;

    static Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        tvName = findViewById(R.id.tvName);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        tvDescription = findViewById(R.id.tvDescription);
        ivImage = findViewById(R.id.ivImage);
        rvComments = findViewById(R.id.rvComments);
        ivComment = findViewById(R.id.ivComment);
        ivLike = findViewById(R.id.ivLike);
        ivProfile = findViewById(R.id.ivProfile);
        tvLikeNum = findViewById(R.id.tvLikeNum);


        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        boolean openCompose = getIntent().getBooleanExtra("openCompose", false);

        if (openCompose) {
            showEditDialog();
        }

        ParseFile profile = post.getProfile();
        if (profile != null) {
            Glide.with(this).load(profile.getUrl()).circleCrop().into(ivProfile);
        } else {
            Glide.with(this).load(R.drawable.default_pic).circleCrop().into(ivProfile);
        }
        allComments = new ArrayList<>();
        adapter = new CommentAdapter(this, allComments);
        rvComments.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(layoutManager);
        tvName.setText(post.getUser().getUsername());
        tvTimestamp.setText(post.getTimestamp());
        tvDescription.setText(post.getDescription());
        tvLikeNum.setText(post.getLikeNum() + " Likes");
        Glide.with(this).load(post.getImage().getUrl()).into(ivImage);
        if (post.isLiked()) {
            Glide.with(this).load(R.drawable.full_heart).into(ivLike);
        } else {
            Glide.with(this).load(R.drawable.ufi_heart).into(ivLike);
        }
        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Integer likeImage;
                if (post.addedLike()) {
                    likeImage = R.drawable.full_heart;
                } else {
                    likeImage = R.drawable.ufi_heart;
                }
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue saving the post like" , e);
                            return;
                        }
                        Log.i(TAG, "Like was saved!!");
                        tvLikeNum.setText(post.getLikeNum() + " Likes");
                        Glide.with(getApplicationContext()).load(likeImage).into(ivLike);
                    }
                });

            }
        });

        queryComments();

    }

    public void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeDialogFragment composeDialogFragment = ComposeDialogFragment.newInstance(this);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Post.class.getSimpleName(), Parcels.wrap(post));
        composeDialogFragment.setArguments(bundle);
        composeDialogFragment.show(fm, "fragment_compose_dialog");
    }

    public static void queryComments() {
        Log.i(TAG, "queryComments");
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.include(Comment.KEY_POST);
        query.whereEqualTo(Comment.KEY_POST, post);
        query.setLimit(10);
        query.addDescendingOrder(Comment.KEY_CREATEDAT);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting comments", e);
                }
                for (Comment comment : comments) {
                    Log.i(TAG, "Comment: " + comment.getMessage());
//                    Log.i(TAG, "username " + comment.getUser());
                }
                adapter.clear();
                adapter.addAll(comments);
            }
        });
    }

    @Override
    public void onFinishEditDialog(Comment comment) {
       queryComments();
    }
}