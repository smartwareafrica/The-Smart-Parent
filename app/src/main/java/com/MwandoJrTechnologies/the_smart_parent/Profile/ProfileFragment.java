package com.MwandoJrTechnologies.the_smart_parent.Profile;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.MwandoJrTechnologies.the_smart_parent.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;

/**
 * Displays user profile
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private TextView textViewName;
    private TextView textViewContact;
    private TextView textViewUsername;
    private Button buttonLogout;

    ImageView imageView;

    private DatabaseReference databaseReference;
    private StorageReference ProfilePics;

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
            Toast.makeText(getContext(), "Please Log into your account", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), MainActivity.class));
        }


        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        textViewName = (TextView) rootView.findViewById(R.id.textViewName);
        textViewContact = (TextView) rootView.findViewById(R.id.textViewContact);
        textViewUsername = (TextView) rootView.findViewById(R.id.textViewUsername);

        buttonLogout = (Button) rootView.findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);

        LoadUserInformation();

        return rootView;
    }

    private void LoadUserInformation() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        // String photoUrl = user.getPhotoUrl().toString();
        // String email = user.getEmail();
        // String displayName = user.getDisplayName();

        databaseReference = FirebaseDatabase.getInstance().getReference("server/path/to/the-smart-parent-598c2");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProfilePics");

        if (user != null) {
            if (user.getPhotoUrl() != null) {

                String photoUrl = user.getPhotoUrl().toString();

                Glide.with(getContext())
                        .load(user
                                .getPhotoUrl()
                                .toString())
                        .centerCrop()
                        .placeholder(R.drawable.ic_menu_camera)
                        .into(imageView);

            }
            if (user.getEmail() != null) {
                textViewName.setText(user.getEmail());
            }
            if (user.getDisplayName() != null) {
                textViewContact.setText(user.getDisplayName());
            }
            if (user.getUid() != null) {
                textViewUsername.setText(user.getUid());
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



