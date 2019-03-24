package com.MwandoJrTechnologies.the_smart_parent.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextConfirmEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private TextView textViewSignIn;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            //start profile activity here
            startActivity(new Intent(this, EditProfileActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        editTextEmail = findViewById(R.id.edit_text_email);
        editTextConfirmEmail = findViewById(R.id.edit_text_confirm_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextConfirmPassword = findViewById(R.id.edit_text_confirm_password);

        buttonRegister = findViewById(R.id.button_register);

        textViewSignIn = findViewById(R.id.text_view_sign_in);

        //when clicked both button and textView
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //check for current user
        if (currentUser != null) {
            SendUserToMainActivity();
        }
    }


    //method to register in fireBase
    private void CreateNewAccount() {

        String email = editTextEmail.getText().toString().trim();
        String confirmEmail = editTextConfirmEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        //checking if email and password is empty and match
        if (TextUtils.isEmpty(email)) {
            //email is empty
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please enter email", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (TextUtils.isEmpty(confirmEmail)) {
            //ConfirmEmail is empty
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Confirm your email", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (TextUtils.isEmpty(password)) {
            //Password is empty
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please enter password", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (TextUtils.isEmpty(confirmPassword)) {
            //ConfirmPassword is empty
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please confirm your password", Snackbar.LENGTH_SHORT);
            snackbar.show();

            //emails and confirm email match
        } else if (!email.equals(confirmEmail)) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Make sure emails match", Snackbar.LENGTH_SHORT);
            snackbar.show();
            //password and confirm password match
        } else if (!password.equals(confirmPassword)) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Make sure Passwords match", Snackbar.LENGTH_SHORT);
            snackbar.show();

        } else {

            //validations okay then we show a progress bar
            progressDialog.setTitle("Registering User...");
            progressDialog.setMessage("Please wait, Creating account...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);

            //create account to fireBase now
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SendUserToProfileActivity();
                                //check if successful
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Successfully registered", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                progressDialog.dismiss();
                            } else {
                                String message = task.getException().getMessage();
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "An Error Occurred: " + message, Snackbar.LENGTH_LONG);
                                snackbar.show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }

    }

    //opens login activity
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    private void SendUserToProfileActivity() {
        Intent editProfileIntent = new Intent(RegisterActivity.this, EditProfileActivity.class);
        editProfileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(editProfileIntent);
        finish();
    }

    //open main activity
    private void SendUserToMainActivity() {
        Intent registerIntent = new Intent(RegisterActivity.this, MainActivity.class);
        finish();
        startActivity(registerIntent);
    }
}