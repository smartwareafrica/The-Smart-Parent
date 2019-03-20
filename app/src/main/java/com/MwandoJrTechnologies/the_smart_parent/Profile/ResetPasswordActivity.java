package com.MwandoJrTechnologies.the_smart_parent.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button resetPasswordSendEmailButton;
    private EditText resetEmailInput;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_reset_password);

      mAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Reset Your Password");

        resetPasswordSendEmailButton = (Button) findViewById(R.id.button_send_email);
        resetEmailInput = (EditText) findViewById(R.id.reset_pwd_email);

        resetPasswordSendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = resetEmailInput.getText().toString();

                if (TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ResetPasswordActivity.this, "Please enter a valid email first", Toast.LENGTH_SHORT).show();
                }else {
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                          if (task.isSuccessful()){
                              Toast.makeText(ResetPasswordActivity.this, "Email sent. Please check your mail inbox to reset your password", Toast.LENGTH_SHORT).show();
                              startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));

                          }else {
                              String message = task.getException().getMessage();
                              Toast.makeText(ResetPasswordActivity.this, "An error occurred" + message, Toast.LENGTH_SHORT).show();
                          }
                        }
                    });
                }
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
