package com.example.parstagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.parstagram.MainActivity;
import com.example.parstagram.OpeningActivity;
import com.example.parstagram.Post;
import com.example.parstagram.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 43;
    private static final String TAG = "ProfileFragment";

    ParseUser user;
    ImageView ivProfile;
    TextView tvName;
    TextView tvUsername;
    TextView tvBio;
    Button btnEdit;
    Button btnLogout;
    public static FragmentManager fragmentManager;
    File photoFile;
    String photoFileName = "photo.jpg";

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

        fragmentManager = getChildFragmentManager();
        ivProfile = view.findViewById(R.id.ivProfile);
        tvName = view.findViewById(R.id.tvName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvBio = view.findViewById(R.id.tvBio);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnLogout = view.findViewById(R.id.btnLogout);
        fragmentManager.beginTransaction().replace(R.id.flProfilePosts, new ProfilePostsGridFragment(user)).commit();

        if (!user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
            btnEdit.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
        }

        ParseFile profile = user.getParseFile(Post.KEY_PROFILEPIC);
        if (profile != null) {
            Glide.with(getContext()).load(profile.getUrl()).circleCrop().into(ivProfile);
        } else {
            Glide.with(getContext()).load(R.drawable.default_pic).circleCrop().into(ivProfile);
        }
        tvName.setText(user.getString(Post.KEY_NAME));
        tvUsername.setText("@" + user.getUsername());
        if (user.getString(Post.KEY_BIO) == null) {
            tvBio.setVisibility(View.GONE);
        } else {
            tvBio.setText(user.getString(Post.KEY_BIO));
        }
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                goOpening();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
    }

    private void goOpening() {
        Intent intent = new Intent(getContext(), OpeningActivity.class);
        startActivity(intent);
    }

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.parstagram.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // TODO resize the bitmap
//                ivProfile.setImageBitmap(takenImage);
                user.put(Post.KEY_PROFILEPIC, new ParseFile(photoFile));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue saving the profile picure" , e);
                            Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Log.i(TAG, "User image was saved!!");
//                        etDescription.setText("");
//                        pd.dismiss();
                        Glide.with(getContext()).load(takenImage).circleCrop().into(ivProfile);

                    }
                });
            } else { // Result was a failure
                Log.i(TAG, "Picture wasn't taken");
//                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }
}