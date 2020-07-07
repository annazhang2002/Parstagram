package com.example.parstagram.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.parstagram.OpeningActivity;
import com.example.parstagram.Post;
import com.example.parstagram.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    ParseUser user;

    ImageView ivProfile;
    TextView tvName;
    TextView tvUsername;
    TextView tvBio;
    Button btnEdit;
    Button btnLogout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(ParseUser user) {
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfile = view.findViewById(R.id.ivProfile);
        tvName = view.findViewById(R.id.tvName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvBio = view.findViewById(R.id.tvBio);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnLogout = view.findViewById(R.id.btnLogout);

        ParseFile profile = user.getParseFile(Post.KEY_PROFILEPIC);
        if (profile != null) {
            Glide.with(getContext()).load(profile.getUrl()).circleCrop().into(ivProfile);
        } else {
            Glide.with(getContext()).load(R.drawable.default_pic).circleCrop().into(ivProfile);
        }
        tvName.setText(user.getString(Post.KEY_NAME));
        tvUsername.setText("@" + user.getUsername());
        tvBio.setText(user.getString(Post.KEY_BIO));
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                goOpening();
            }
        });
    }

    private void goOpening() {
        Intent intent = new Intent(getContext(), OpeningActivity.class);
        startActivity(intent);
    }
}