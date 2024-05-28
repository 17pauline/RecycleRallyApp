package com.rr.recyclerally.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.rr.recyclerally.R;
import com.rr.recyclerally.database.FirebaseService;
import com.rr.recyclerally.database.UserSession;


public class LoginActivity extends AppCompatActivity {
    private static final String LOGIN_TAG = "Login";
    FirebaseAuth auth;
    private FirebaseService firebaseService = new FirebaseService();
    private TextInputEditText tietUsername;
    private TextInputEditText tietPassword;

    private AppCompatButton btnLogin;
    private TextView clickableTvSignup;
    private TextView clickableTvForgotPassword;

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

        clickableTvForgotPassword = findViewById(R.id.login_tv_forgot_password);
        clickableTvForgotPassword.setOnClickListener(getForgotPasswordListener());

    }

    private View.OnClickListener getLoginListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        };
    }

    private void loginUser() {
        String username = tietUsername.getText().toString().trim();
        String password = tietPassword.getText().toString().trim();
        auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if (firebaseUser != null) {
                        String firebaseUserUid = firebaseUser.getUid();
                        firebaseService.getUser(firebaseUserUid, user -> {
                            if (user != null) {
                                UserSession.getInstance().setUser(user);
                                Toast.makeText(getApplicationContext(),
                                        R.string.toast_login_successful, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                } else {
                    handleLoginError(task);
                }
            }
        });
    }

    private void handleLoginError(@NonNull Task<AuthResult> task) {
        try {
            throw task.getException();
        } catch (FirebaseAuthInvalidUserException e) {
            tietUsername.setError(getString(R.string.exception_user_does_not_exist_or_is_no_longer_valid));
            tietUsername.requestFocus();
        } catch (FirebaseAuthInvalidCredentialsException e) {
            tietPassword.setError(getString(R.string.exception_invalid_credentials));
            tietPassword.requestFocus();
        } catch (Exception e) {
            Log.e(LOGIN_TAG, e.getMessage());
            Toast.makeText(getApplicationContext(),
                    R.string.toast_login_failed, Toast.LENGTH_SHORT).show();
        }
    }


    private View.OnClickListener getSignupListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        };
    }

    private View.OnClickListener getForgotPasswordListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        };
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_forgot_password, null);
        builder.setView(dialogView);

        TextInputEditText tietEmail = dialogView.findViewById(R.id.dialog_tiet_forgot_password_email);
        AppCompatButton btnResetPw = dialogView.findViewById(R.id.dialog_btn_reset_password);

        AlertDialog dialog = builder.create();

        btnResetPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = tietEmail.getText().toString().trim();
                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    tietEmail.setError(getString(R.string.toast_login_invalid_email));
                    tietEmail.requestFocus();
                    return;
                }

                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    R.string.dialog_email_sent, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    R.string.dialog_failed_to_send_email, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

}