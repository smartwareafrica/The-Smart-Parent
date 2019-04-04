package com.MwandoJrTechnologies.the_smart_parent.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView userProfilePicture;
    private TextView userStatus;
    private TextView userName;
    private TextView fullName;
    private TextView phoneNumber;
    private TextView dateOfBirth;
    private TextView userGender;
    private TextView numberOfChildren;
    private TextView editProfile;

    private DatabaseReference profileUserReference;
    private FirebaseAuth mAuth;

    private String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        profileUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("User Profile");

        userProfilePicture = findViewById(R.id.image_view_profile_picture);
        userStatus = findViewById(R.id.text_view_status);
        userName = findViewById(R.id.text_view_username);
        fullName = findViewById(R.id.text_view_full_name);
        phoneNumber = findViewById(R.id.text_view_phone_number);
        dateOfBirth = findViewById(R.id.text_view_DOB);
        userGender = findViewById(R.id.text_view_gender);
        numberOfChildren = findViewById(R.id.text_view_number_of_children);
        editProfile = findViewById(R.id.edit_profile_button);

        editProfile.setOnClickListener(v -> SendUserToProfileSettingsActivity());

        profileUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) try {
                    String myProfileImage = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();
                    String myStatus = dataSnapshot.child("status").getValue().toString();
                    String myUserName = dataSnapshot.child("userName").getValue().toString();
                    String myFullName = dataSnapshot.child("fullName").getValue().toString();
                    String myPhoneNumber = dataSnapshot.child("phoneNumber").getValue().toString();
                    String myDateOfBirth = dataSnapshot.child("dob").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myNumberOfChildren = dataSnapshot.child("numberOfChildren").getValue().toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile_image_placeholder).into(userProfilePicture);
                    userStatus.setText(myStatus);
                    userName.setText("@" + myUserName);
                    fullName.setText("NAME: " + myFullName);
                    phoneNumber.setText("Phone Number: " + myPhoneNumber);
                    dateOfBirth.setText("DOB: " + myDateOfBirth);
                    userGender.setText("Gender: " + myGender);
                    numberOfChildren.setText("Number of children: " + myNumberOfChildren);
                }catch (RuntimeException e){
                    Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), "Please update your profile", Snackbar.LENGTH_INDEFINITE);
                    snackBar.show();
                    SendUserToProfileSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //activate back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    //open main activity
    private void SendUserToMainActivity() {
        Intent mainActivityIntent = new Intent(ProfileActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }

    //opens activity to edit profile
    private void SendUserToProfileSettingsActivity() {
        Intent editProfileIntent = new Intent(ProfileActivity.this, ProfileSettingsActivity.class);
        finish();
        startActivity(editProfileIntent);
    }
}
