package com.rr.recyclerally.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rr.recyclerally.R;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private TextInputEditText tietUsername;
    private TextInputEditText tietPassword;

    private AppCompatButton btnLogin;
    private TextView clickableTvSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        initComponents();
    }

    private void initComponents() {
        btnLogin = findViewById(R.id.login_btn_login);
        btnLogin.setOnClickListener(getLoginListener());

        clickableTvSignup = findViewById(R.id.login_tv_sign_up);
        clickableTvSignup.setOnClickListener(getSignupListener());

        tietUsername = findViewById(R.id.login_tiet_username);
        tietPassword = findViewById(R.id.login_tiet_password);
    }

    private View.OnClickListener getLoginListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = tietUsername.getText().toString().trim();
                String password = tietPassword.getText().toString().trim();
                String emailRegex = "^(.+)@(.+)$";

                if (!username.isEmpty() && patternMatches(username, emailRegex)) {
                    if (!password.isEmpty()) {
                        auth.signInWithEmailAndPassword(username, password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(getApplicationContext(),
                                                R.string.toast_login_successful, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),
                                                R.string.toast_login_failed, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        tietPassword.setError(getString(R.string.toast_login_invalid_password));
                    }
                } else if (username.isEmpty()) {
                    tietUsername.setError(getString(R.string.toast_login_invalid_email));
                } else {
                    tietUsername.setError(getString(R.string.toast_login_please_enter_valid_email));
                }
            }
        };
    }





    private View.OnClickListener getSignupListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        };
    }

    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

}