package com.MwandoJrTechnologies.the_smart_parent.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewRegister;
    private TextView textViewResetPassword;
    private TextView googleSignInButton;

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    //for sign in with google
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            //start View profile fragment here
            // OpenViewProfileFragment();
        }

        editTextEmail = findViewById(R.id.login_email);
        editTextPassword = findViewById(R.id.login_password);
        buttonSignIn = findViewById(R.id.button_sign_in);
        textViewRegister = findViewById(R.id.text_view_register);
        textViewResetPassword = findViewById(R.id.text_view_forgot_password);
        googleSignInButton = findViewById(R.id.google_sign_in_button);

        progressDialog = new ProgressDialog(this);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });
        textViewResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToResetPasswordActivity();
            }
        });


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //set google api
        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Google SignIn Failed...,Please Try Again", Snackbar.LENGTH_LONG);
                        snackbar.show();

                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show a progress bar
                progressDialog.setTitle("Opening Google SignIn");
                progressDialog.setMessage("Please wait as we open google SignIn...");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();
                signIn();
            }
        });
    }


    // open Sign In with Google to select accounts
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            //show a progress bar
            progressDialog.setTitle("Google SignIn");
            progressDialog.setMessage("Please wait as Google is Logging you in...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                fireBaseAuthWithGoogle(account);

                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please wait while we are getting your results...", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Sorry! Cannot get your Account results", Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();
            }

        }
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "fireBaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCredential:success");
                            SendUserToMainActivity();
                            progressDialog.dismiss();

                        } else {

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String message = task.getException().toString();
                            SendUserToLoginActivity();
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "An error Occurred: " + message, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            progressDialog.dismiss();
                        }
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

    private void AllowUserToLogin() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //checking if email and password is empty
        if (TextUtils.isEmpty(email)) {
            //email is empty
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please enter email", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (TextUtils.isEmpty(password)) {
            //password is empty
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please enter password", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {

            //validations okay we show a progress bar
            progressDialog.setTitle("Logging in User...");
            progressDialog.setMessage("Logging in, Please wait...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                //  OpenViewProfileFragment();
                                //start the profile activity
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Login Successful", Snackbar.LENGTH_SHORT);
                                snackbar.show();
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

    //open login activity
    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);

    }

    //open main activity
    private void SendUserToMainActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, MainActivity.class);
        finish();
        startActivity(registerIntent);
    }

    //opens login activity
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(LoginActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);

    }
    private void SendUserToResetPasswordActivity() {
        Intent resetPasswordIntent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        resetPasswordIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(resetPasswordIntent);
    }
}
