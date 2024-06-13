package com.rr.recyclerally.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.rr.recyclerally.R;
import com.rr.recyclerally.database.FirebaseService;
import com.rr.recyclerally.database.UserSession;

public class StartActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "RecycleRallyPrefs";
    private static final String PREFS_REMEMBER_ME = "rememberMe";
    private static final String PREF_USER_ID = "userId";

    private AppCompatButton btnLogin;
    private AppCompatButton btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkUserLoggedIn();
        setContentView(R.layout.activity_start);
        initComponents();
    }

    private void checkUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean(PREFS_REMEMBER_ME, false);
        if (rememberMe) {
            String userId = sharedPreferences.getString(PREF_USER_ID, null);
            if (userId != null) {
                FirebaseService firebaseService = new FirebaseService();
                firebaseService.getUser(userId, user -> {
                    if (user != null) {
                        UserSession.getInstance().setUser(user);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }
    }

    private void initComponents() {
        btnLogin = findViewById(R.id.start_btn_login);
        btnSignup = findViewById(R.id.start_btn_signup);
        btnLogin.setOnClickListener(getLoginListener());
        btnSignup.setOnClickListener(getSignupListener());
    }

    private View.OnClickListener getLoginListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener getSignupListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        };
    }

}