package com.MwandoJrTechnologies.the_smart_parent.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSettingsActivity extends AppCompatActivity {

    final static int galleryPick = 1;

    private ProgressDialog progressDialog;

    private CircleImageView profileSettingImage;
    private EditText profileSettingsStatus;
    private EditText profileSettingsUserName;
    private EditText profileSettingsFullName;
    private EditText profileSettingsPhoneNumber;
    private EditText profileSettingsDateOfBirth;
    private EditText profileSettingsGender;
    private EditText profileSettingsNumberOfChildren;
    private Button profileSettingsSaveButton;

    private DatabaseReference profileSettingsReference;
    private StorageReference userProfileImageRef;

    String currentUserID;
    private String downloadImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        profileSettingsReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("ProfilePictures");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Update account");


        profileSettingImage = findViewById(R.id.settings_profile_image);
        profileSettingsStatus = findViewById(R.id.settings_status);
        profileSettingsUserName = findViewById(R.id.settings_user_name);
        profileSettingsFullName = findViewById(R.id.settings_user_full_name);
        profileSettingsPhoneNumber = findViewById(R.id.settings_user_phone_number);
        profileSettingsDateOfBirth = findViewById(R.id.settings_user_date_of_birth);
        profileSettingsGender = findViewById(R.id.settings_user_gender);
        profileSettingsNumberOfChildren = findViewById(R.id.settings_user_number_of_children);
        profileSettingsSaveButton = findViewById(R.id.button_profile_settings);

        progressDialog = new ProgressDialog(this);

        profileSettingsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String myProfileImage = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();
                    String myStatus = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                    String myUserName = Objects.requireNonNull(dataSnapshot.child("userName").getValue()).toString();
                    String myFullName = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                    String myPhoneNumber = Objects.requireNonNull(dataSnapshot.child("phoneNumber").getValue()).toString();
                    String myDateOfBirth = Objects.requireNonNull(dataSnapshot.child("dob").getValue()).toString();
                    String myGender = Objects.requireNonNull(dataSnapshot.child("gender").getValue()).toString();
                    String myNumberOfChildren = Objects.requireNonNull(dataSnapshot.child("numberOfChildren").getValue()).toString();

                    Picasso.get().load(myProfileImage).placeholder(R.drawable.profile_image_placeholder).into(profileSettingImage);
                    profileSettingsStatus.setText(myStatus);
                    profileSettingsUserName.setText(myUserName);
                    profileSettingsFullName.setText(myFullName);
                    profileSettingsPhoneNumber.setText(myPhoneNumber);
                    profileSettingsDateOfBirth.setText(myDateOfBirth);
                    profileSettingsGender.setText(myGender);
                    profileSettingsNumberOfChildren.setText(myNumberOfChildren);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profileSettingsSaveButton.setOnClickListener(v -> ValidateAccountsInformation());

        profileSettingImage.setOnClickListener(v -> {
//opening gallery to choose image
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, galleryPick);
        });
    }


    //adding a profile image to fireBase storage
    //method for picking the chosen image from my gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            //adding crop image functionality using arthurHub library on github
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                //show progress dialog
                progressDialog.setTitle("Profile Image");
                progressDialog.setMessage("Updating profile image, Please wait...");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                Uri resultUri = result.getUri();

                //creating a filepath for pushing cropped image to fireBase storage by unique user id
                final StorageReference filePath = userProfileImageRef.child(resultUri.getLastPathSegment() + currentUserID + ".jpg");

                //now store in fireBase storage
                final UploadTask uploadTask = filePath.putFile(resultUri);

                uploadTask.addOnSuccessListener(taskSnapshot -> {

                    Task<Uri> UriTask = uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        //get the url...INITIALISE downloadImageUrl at the most to ie....String downloadImageUrl
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();


                    }).addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            //get the link
                            downloadImageUrl = task.getResult().toString();
                            addLinkToFireBaseDatabase();
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Good", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    });
                });
            }
        }
    }

    private void addLinkToFireBaseDatabase() {
        profileSettingsReference.child("profileImage").setValue(downloadImageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "profile image uploaded successfully uploaded...", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    progressDialog.dismiss();
                    String message = task.getException().getMessage();
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Error occurred  " + message, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });
    }



    private void ValidateAccountsInformation() {

        String username = profileSettingsUserName.getText().toString();
        String status =  profileSettingsStatus.getText().toString();
        String fullName =  profileSettingsFullName.getText().toString();
        String phoneNumber = profileSettingsPhoneNumber.getText().toString();
        String dob = profileSettingsDateOfBirth.getText().toString();
        String gender = profileSettingsGender.getText().toString();
        String numberOfChildren = profileSettingsNumberOfChildren.getText().toString();

        if (TextUtils.isEmpty(username)){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please write your Username", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else if (TextUtils.isEmpty(status)){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please update your status", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (TextUtils.isEmpty(fullName)){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please twrite your full name", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (TextUtils.isEmpty(phoneNumber)){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please type your phone number", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (TextUtils.isEmpty(dob)){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please enter your date of birth DDMMYYYY", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (TextUtils.isEmpty(gender)){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please enter your gender (MALE/FEMALE)", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (TextUtils.isEmpty(numberOfChildren)){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Enter number of children you have if none enter 0", Snackbar.LENGTH_LONG);
            snackbar.show();
        }else {

            //show progress dialog
            progressDialog.setTitle("Profile Details");
            progressDialog.setMessage("Saving details, Please wait...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            UpdateAccountInformation(username, status, fullName, phoneNumber, dob, gender, numberOfChildren);
        }

    }

    private void UpdateAccountInformation(String username, String status, String fullName, String phoneNumber, String dob, String gender, String numberOfChildren) {

        HashMap userMap = new HashMap();
        userMap.put("status", status);
        userMap.put("userName", username);
        userMap.put("fullName", fullName);
        userMap.put("phoneNumber", phoneNumber);
        userMap.put("dob", dob);
        userMap.put("gender", gender);
        userMap.put("numberOfChildren", numberOfChildren);
        profileSettingsReference.updateChildren(userMap).addOnCompleteListener(task -> {

            if (task.isSuccessful()){

                SendUserToMainActivity();
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Account Updated Successfully", Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();

            }else {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "An error occurred,Please try again", Snackbar.LENGTH_LONG);
                snackbar.show();
                progressDialog.dismiss();

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
        Intent mainActivityIntent = new Intent(ProfileSettingsActivity.this, MainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }

}
