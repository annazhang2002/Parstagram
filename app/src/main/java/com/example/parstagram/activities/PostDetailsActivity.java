package com.example.parstagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.parse.ParseQuery;

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


        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        allComments = new ArrayList<>();
        adapter = new CommentAdapter(this, allComments);
        rvComments.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(layoutManager);
        tvName.setText(post.getUser().getUsername());
        tvTimestamp.setText(post.getTimestamp());
        tvDescription.setText(post.getDescription());
        Glide.with(this).load(post.getImage().getUrl()).into(ivImage);

        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
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