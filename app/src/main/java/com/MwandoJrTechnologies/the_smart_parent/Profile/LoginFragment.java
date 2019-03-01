package com.MwandoJrTechnologies.the_smart_parent.Profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.MwandoJrTechnologies.the_smart_parent.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignUp;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        return rootView;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            //start View profile fragment here
            OpenViewProfileFragment();
        }

        editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) view.findViewById(R.id.buttonSignIn);
        textViewSignUp = (TextView) view.findViewById(R.id.textViewSignUp);

        progressDialog = new ProgressDialog(getContext());

        buttonSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //checking if email and password is empty
        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(getActivity(), "Please enter email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_SHORT).show();
        }else{
            //validations okay we show a progress bar
            progressDialog.setTitle("Logging in User...");
            progressDialog.setMessage("Logging in, Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                OpenViewProfileFragment();
                                //start the profile activity
                                // startActivity(new Intent(getActivity(), ViewProfileFragment.class));
                                Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT);
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(getActivity(), "An Error Occurred: " + message, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }
    @Override
    public void onClick(View view) {
        if (view == buttonSignIn) {
            userLogin();
        }
        if (view == textViewSignUp) {
            OpenRegisterFragment();
        }
    }

    //method to open fragment from another fragment
    private void OpenViewProfileFragment() {
        ViewProfileFragment nextFrag = new ViewProfileFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    //method to open fragment from another fragment
    private void OpenRegisterFragment() {
        RegisterFragment nextFrag = new RegisterFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }
}
