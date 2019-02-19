package com.mwandojrtechnologies.the_smart_parent.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mwandojrtechnologies.the_smart_parent.MainActivity;
import com.mwandojrtechnologies.the_smart_parent.Newsfeed.HomeFragment;
import com.mwandojrtechnologies.the_smart_parent.R;

/**
 * Displays user profile
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private TextView textViewName;
    private TextView textViewContact;
    private Button buttonLogout;
    private ImageView imageView;


    private DatabaseReference databaseReference;

    Uri uriProfileImage;
    ProgressBar progressBar;

    String profileImageUrl;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getContext(), LoginFragment.class));
        }


        databaseReference = FirebaseDatabase.getInstance().getReference("server/path/to/the-smart-parent-598c2");


        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        progressBar =(ProgressBar) rootView.findViewById(R.id.progressBar);
        textViewName = (TextView) rootView.findViewById(R.id.textViewName);
        textViewContact = (TextView) rootView.findViewById(R.id.textViewContact);


        buttonLogout = (Button) rootView.findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);

        LoadUserInformation();

    return rootView;
    }

    private void LoadUserInformation() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }
            if (user.getDisplayName() != null) {
                textViewName.setText(user.getDisplayName());
            }
            if (user.getDisplayName() != null) {
                textViewContact.setText(user.getDisplayName());
            }
        }




    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onClick(View view) {

        firebaseAuth = FirebaseAuth.getInstance();

        //logout button pressed
        if (view == buttonLogout) {
            //logout the user
            firebaseAuth.signOut();
            //start main activity
           startActivity(new Intent(getContext(), MainActivity.class));
        }

    }
}
