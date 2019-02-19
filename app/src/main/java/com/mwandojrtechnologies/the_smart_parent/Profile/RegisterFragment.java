package com.mwandojrtechnologies.the_smart_parent.Profile;


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
import com.mwandojrtechnologies.the_smart_parent.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener{

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignin;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

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


        firebaseAuth  = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //start profile activity here
            startActivity(new Intent(getActivity(), ProfileActivity.class));
        }

        progressDialog = new ProgressDialog(getContext());

        buttonRegister = (Button) view.findViewById(R.id.buttonRegister);

        editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);

        textViewSignin = (TextView) view.findViewById(R.id.textViewSignin);

        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        //checking if email and password is empty
        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(getActivity(), "Please enter email", Toast.LENGTH_SHORT).show();
            //stop function from executing further
            return;
        }

        if(TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_SHORT).show();
            //stopping execution further
            return;
        }

        //validations okay
        //we show a progress bar
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //check if successful
                            Toast.makeText(getActivity(), "Successfully registered", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), ProfileActivity.class));
                        }else{
                            Toast.makeText(getActivity(), "Could not register. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if(v == buttonRegister){
            registerUser();
        }

        if(v == textViewSignin){
            //will open login fragment
            startActivity(new Intent(getActivity(), LoginFragment.class));

        }

    }
}
