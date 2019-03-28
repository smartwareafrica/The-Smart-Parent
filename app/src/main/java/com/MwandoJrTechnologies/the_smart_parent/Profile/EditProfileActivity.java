package com.MwandoJrTechnologies.the_smart_parent.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    final static int galleryPick = 1;
    String currentUserID;
    private EditText editTextName;
    private EditText editTextUsername;
    private CircleImageView profileImage;
    private Button buttonSave;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    private StorageReference userProfileImageRef;
    private ProgressDialog progressDialog;
    private String downloadImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit profile");

        //getting the user who is logged in as CurrentUser
        mAuth = FirebaseAuth.getInstance();
        //get unique user id
        currentUserID = mAuth.getCurrentUser().getUid();

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        //specify path in fireBase storage
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("ProfilePictures");

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        profileImage = findViewById(R.id.profile_image_view);

        editTextUsername = findViewById(R.id.edit_text_username);
        editTextName = findViewById(R.id.edit_text_name);

        buttonSave = findViewById(R.id.buttonSave);

        progressDialog = new ProgressDialog(this);


        buttonSave.setOnClickListener(v -> {

            //show progress dialog
            progressDialog.setTitle("Profile Details");
            progressDialog.setMessage("Updating profile details, Please wait...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();


            checkIfUserNameExists();

            saveUserInformation();
            //start main activity
            finish();
            SendUserToMainActivity();
        });
        profileImage.setOnClickListener(v -> {
            //opening gallery to choose image
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, galleryPick);
        });
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //load image from fireBase
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("profileImage")) {
                        String image = dataSnapshot.child("profileImage").getValue().toString();
                        Picasso.get()
                                .load(image)
                                .placeholder(R.drawable.profile_image_placeholder)
                                .into(profileImage);

                    } else {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please select a profile picture first", Snackbar.LENGTH_LONG);
                        snackbar.show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
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
                            progressDialog.dismiss();
                        }
                    });
                });
            }
        }
    }

    private void addLinkToFireBaseDatabase() {
        usersReference.child("profileImage").setValue(downloadImageUrl).addOnCompleteListener(task -> {
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
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    //Validation where both details must be filled and saved to fireBase database
    private void saveUserInformation() {
        final String username = editTextUsername.getText().toString().trim();
        String fullName = editTextName.getText().toString().trim();

        //creating a username and checking if any other exists
        if (username.isEmpty()) {
            editTextUsername.setError("You must select a username");
            editTextUsername.requestFocus();
        }
        if (fullName.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
        } else {

            progressDialog.setTitle("Uploading Details...");
            progressDialog.setMessage("Saving your information, Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);


            final HashMap userMap = new HashMap();
            userMap.put("status", "Hey there, I am using this informative SMART PARENT App!!!");
            userMap.put("userName", username);
            userMap.put("fullName", fullName);
            userMap.put("phoneNumber", "none");
            userMap.put("dob", "none");
            userMap.put("gender", "none");
            userMap.put("numberOfChildren", "none");
            usersReference.updateChildren(userMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    SendUserToMainActivity();
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Your details saved successfully", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    progressDialog.dismiss();

                } else {
                    String message = task.getException().getMessage();
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "An error occurred,please try again " + message, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    //open main activity
    private void SendUserToMainActivity() {
        Intent registerIntent = new Intent(EditProfileActivity.this, MainActivity.class);
        finish();
        startActivity(registerIntent);
    }

    //checking of the username exists in the database
    private boolean checkIfUserNameExists() {

        String username = editTextUsername.getText().toString().trim();
        Query usernameQuery = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .orderByChild("userName")
                .equalTo(username);
        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {
                    //should stop further execution
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Username Taken. Please choose a different Username", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //display error
            }
        });
        return true;
    }

}
