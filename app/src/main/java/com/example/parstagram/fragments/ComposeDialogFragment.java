 package com.example.parstagram.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.parstagram.R;
import com.example.parstagram.activities.MainActivity;
import com.example.parstagram.activities.PostDetailsActivity;
import com.example.parstagram.models.Comment;
import com.example.parstagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeDialogFragment extends DialogFragment {

    public static final String TAG = "ComposeDialogFragment";
    private static Context context1;

    EditText etCompose;
    Button btnSubmit;
    ImageView ivClose;
    Post post;

    public ComposeDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeDialogFragment newInstance(Context context) {
        ComposeDialogFragment frag = new ComposeDialogFragment();
        context1 = context;
        return frag;
    }

    public interface EditNameDialogListener {
        void onFinishEditDialog(Comment comment);
    }

    // Call this method to send the data back to the parent fragment
    public void sendBackResult(Comment comment) {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        EditNameDialogListener listener = (EditNameDialogListener) getTargetFragment();
        listener.onFinishEditDialog(comment);
        dismiss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        post = Parcels.unwrap(getArguments().getParcelable(Post.class.getSimpleName()));
        return inflater.inflate(R.layout.fragment_compose_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etCompose = view.findViewById(R.id.etCompose);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        ivClose = view.findViewById(R.id.ivClose);
        // Show soft keyboard automatically and request focus to field
        etCompose.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etCompose.getText().toString();
                if (message != null && !message.isEmpty()) {
                    final Comment comment = new Comment();
                    comment.setMessage(message);
                    comment.setUser(ParseUser.getCurrentUser());
                    comment.setPost(post);
                    comment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Issue saving the comment" , e);
                                Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_LONG).show();
                                return;
                            }

                            Log.i(TAG, "Comment was saved!!");
                            PostDetailsActivity.queryComments();
                            dismiss();
                        }
                    });
                }
            }
        });
    }

}
