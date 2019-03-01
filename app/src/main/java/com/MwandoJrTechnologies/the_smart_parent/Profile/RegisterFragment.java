package com.MwandoJrTechnologies.the_smart_parent.Profile;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.MwandoJrTechnologies.the_smart_parent.R;

// where user registration happens
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextConfirmEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private TextView textViewSignIn;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    Context context;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            //start profile activity here
            startActivity(new Intent(getActivity(), EditProfileActivity.class));
        }

        progressDialog = new ProgressDialog(getContext());

        buttonRegister = (Button) view.findViewById(R.id.buttonRegister);

        editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
        editTextConfirmEmail = (EditText) view.findViewById(R.id.editTextConfirmEmail);
        editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) view.findViewById(R.id.editTextConfirmPassword);

        textViewSignIn = (TextView) view.findViewById(R.id.textViewSignIn);

        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String confirmEmail = editTextConfirmEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();


        //checking if email and password is empty and match
        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(getActivity(), "Please enter email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmEmail)) {
            //ConfirmEmail is empty
            Toast.makeText(getActivity(), "Please Confirm your email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            //Password is empty
            Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirmPassword)) {
            //ConfirmPassword is empty
            Toast.makeText(getActivity(), "Please confirm your password", Toast.LENGTH_SHORT).show();

            //emails and confirm email match
        } else if (!email.equals(confirmEmail)) {
            Toast.makeText(getActivity(), "Make sure emails match", Toast.LENGTH_SHORT).show();
            //password and confirm password match
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(getActivity(), "Make sure Passwords match", Toast.LENGTH_SHORT).show();
        } else {
            //validations okay then we show a progress bar
            progressDialog.setTitle("Registering User...");
            progressDialog.setMessage("Please wait, Creating account...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                SenUserToProfileActivity();
                                //check if successful
                                Toast.makeText(getActivity(), "Successfully registered", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(getActivity(), "An Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }

    }

    private void SenUserToProfileActivity() {
        Intent setupIntent = new Intent(RegisterFragment.this.getActivity(), EditProfileActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
    }

    @Override
    public void onClick(View v) {
        if (v == buttonRegister) {
            registerUser();
        }
        if (v == textViewSignIn) {
            //will open login fragment
            OpenLoginFragment();
        }
    }

    //method to open fragment from another fragment
    private void OpenLoginFragment() {
        LoginFragment nextFrag = new LoginFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }
}
