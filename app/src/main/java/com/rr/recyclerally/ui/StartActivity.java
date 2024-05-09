package com.rr.recyclerally.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.rr.recyclerally.R;

public class StartActivity extends AppCompatActivity {

    private AppCompatButton btnLogin;
    private AppCompatButton btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initComponents();
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