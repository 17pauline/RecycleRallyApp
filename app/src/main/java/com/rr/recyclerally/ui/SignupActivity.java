package com.rr.recyclerally.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rr.recyclerally.R;
import com.rr.recyclerally.database.FirebaseService;
import com.rr.recyclerally.database.UserSession;
import com.rr.recyclerally.model.user.AUser;
import com.rr.recyclerally.model.user.Admin;
import com.rr.recyclerally.model.user.EUserType;
import com.rr.recyclerally.model.user.Recycler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private static final String SIGNUP_TAG = "Signup";
    private FirebaseService firebaseService = new FirebaseService();

    private TextInputEditText tietEmail;
    private TextInputEditText tietUsername;
    private TextInputEditText tietPassword;
    private RadioGroup rgUserTypes;
    private AppCompatButton btnSignup;
    private TextView clickableTvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initComponents();
    }

    private void initComponents() {
        btnSignup = findViewById(R.id.signup_btn_signup);
        btnSignup.setOnClickListener(getSignupListener());

        clickableTvLogin = findViewById(R.id.signup_tv_login);
        clickableTvLogin.setOnClickListener(getLoginListener());

        tietEmail = findViewById(R.id.signup_tiet_email);
        tietUsername = findViewById(R.id.signup_tiet_username);
        tietPassword = findViewById(R.id.signup_tiet_password);

        rgUserTypes = findViewById(R.id.signup_rg_user_types);
    }

    private View.OnClickListener getSignupListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    signupUser();
                }
            }
        };
    }

    private void signupUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        AUser user = buildUser();
        String password = tietPassword.getText().toString().trim();
        auth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {
                                String firebaseUserUid = firebaseUser.getUid();

                                firebaseService.saveUser(user, firebaseUserUid, result -> {
                                    if (result) {
                                        UserSession.getInstance().setUser(user);
                                        Toast.makeText(getApplicationContext(),
                                                R.string.toast_signup_successful, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        try {
                                            throw task.getException();
                                        } catch (Exception e) {
                                            Log.e(SIGNUP_TAG, e.getMessage());
                                            Toast.makeText(getApplicationContext(), getString(R.string.toast_signup_failed) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            handleSignupError(task);
                        }
                    }
                });
    }

    private void handleSignupError(@NonNull Task<AuthResult> task) {
        try {
            throw task.getException();
        } catch (FirebaseAuthUserCollisionException e) {
            tietEmail.setError(getString(R.string.exception_this_email_is_already_registered));
            tietEmail.requestFocus();
        } catch (Exception e) {
            Log.e(SIGNUP_TAG, e.getMessage());
            Toast.makeText(getApplicationContext(), getString(R.string.toast_signup_failed) + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener getLoginListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        };
    }

    private AUser buildUser() {
        String email = tietEmail.getText().toString().trim();
        String username = tietUsername.getText().toString().trim();
        EUserType userType = rgUserTypes.getCheckedRadioButtonId()
                == R.id.signup_rb_recycler
                ? EUserType.RECYCLER
                : EUserType.ADMIN;
        if (userType == EUserType.RECYCLER) {
            return new Recycler(email, username, userType);
        } else {
            return new Admin(email, username, userType);
        }
    }

    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    private boolean isValid() {
        String emailRegex = "^(.+)@(.+)$";
        if (tietEmail.getText() == null || !patternMatches(tietEmail.getText().toString().trim(), emailRegex)) {
            Toast.makeText(getApplicationContext(),
                    R.string.toast_invalid_email_address, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tietUsername.getText() == null || tietUsername.getText().toString().trim().length() < 3 || tietUsername.getText().toString().trim().length() > 25) {
            Toast.makeText(getApplicationContext(),
                    R.string.toast_invalid_username, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tietPassword.getText() == null || tietPassword.getText().toString().trim().length() < 6) {
            Toast.makeText(getApplicationContext(),
                    R.string.toast_invalid_password, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}