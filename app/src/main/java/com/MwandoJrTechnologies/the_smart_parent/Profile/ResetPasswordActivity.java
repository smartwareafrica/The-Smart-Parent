package com.MwandoJrTechnologies.the_smart_parent.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ResetPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button resetPasswordSendEmailButton;
    private EditText resetEmailInput;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Reset Your Password");

        resetPasswordSendEmailButton = findViewById(R.id.button_send_email);
        resetEmailInput = findViewById(R.id.reset_pwd_email);

        resetPasswordSendEmailButton.setOnClickListener(v -> {
            String userEmail = resetEmailInput.getText().toString();

            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

            if (userEmail.isEmpty()){

                resetEmailInput.setError("Email is required");

            }if (!userEmail.matches((emailPattern))){

                resetEmailInput.setError("Please input a valid Email");
            }
            else {
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Email sent. Please check your mail inbox to reset your password", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));

                    } else {
                        String message = task.getException().getMessage();
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "An error occurred" + message, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
            }
        });

    }

    //activate back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            SendUserToLoginActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    //open login activity
    private void SendUserToLoginActivity() {
        Intent mainActivityIntent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }
}
