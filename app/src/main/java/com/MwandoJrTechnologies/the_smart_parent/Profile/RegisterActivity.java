package com.MwandoJrTechnologies.the_smart_parent.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
            startActivity(new Intent(this, ProfileActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        editTextEmail = (EditText) findViewById(R.id.edit_text_email);
        editTextConfirmEmail = (EditText) findViewById(R.id.edit_text_confirm_email);
        editTextPassword = (EditText) findViewById(R.id.edit_text_password);
        editTextConfirmPassword = (EditText) findViewById(R.id.edit_text_confirm_password);

        buttonRegister = (Button) findViewById(R.id.button_register);

        textViewSignIn = (TextView) findViewById(R.id.text_view_sign_in);

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
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmEmail)) {
            //ConfirmEmail is empty
            Toast.makeText(this, "Please Confirm your email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            //Password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmPassword)) {
            //ConfirmPassword is empty
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();

            //emails and confirm email match
        } else if (!email.equals(confirmEmail)) {
            Toast.makeText(this, "Make sure emails match", Toast.LENGTH_SHORT).show();
            //password and confirm password match
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Make sure Passwords match", Toast.LENGTH_SHORT).show();

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
                                Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "An Error Occurred: " + message, Toast.LENGTH_SHORT).show();
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
        Intent editProfileIntent = new Intent(RegisterActivity.this, ProfileActivity.class);
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