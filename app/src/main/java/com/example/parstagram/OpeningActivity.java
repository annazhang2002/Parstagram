package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

public class OpeningActivity extends AppCompatActivity {

    private static final String TAG = "OpeningActivity";
    Button btnLogin;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Log.i(TAG, "User is already signed in");
            // navigate to the main activity
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        }

        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLogin("login");
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLogin("signup");
            }
        });
    }

    private void goLogin(String btnName) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("btnName", btnName);
        this.startActivity(intent);
    }
}