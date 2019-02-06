package com.mwandojrtechnologies.the_smart_parent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CHOOSE_IMAGE = 101;
    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    private EditText editTextName;
    private EditText editTextContact;
    private Button buttonLogout;
    private ImageView imageView;

    private DatabaseReference databaseReference;
    private Button buttonSave;

    Uri uriProfileImage;
    ProgressBar progressBar;

    String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginFragment.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        editTextContact = (EditText) findViewById(R.id.editTextContact);
        editTextName = (EditText) findViewById(R.id.editTextName);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        imageView = (ImageView) findViewById(R.id.imageView);

        progressBar = findViewById(R.id.progressBar);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);

        textViewUserEmail.setText("Welcome to THE SMART PARENT " + user.getEmail());
        buttonLogout = (Button) findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(this);
        buttonSave.setOnClickListener(this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        LoadUserInformation();

    }

@Override
protected void onStart() {
    super.onStart();
    if (firebaseAuth.getCurrentUser() ==null){
        startActivity(new Intent (this, LoginFragment.class));
    }
}

    // Display user information if they already have saved it
    private void LoadUserInformation() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }
            if (user.getDisplayName() != null) {
                editTextName.setText(user.getDisplayName());
            }
            if (user.getDisplayName() != null) {
                editTextContact.setText(user.getDisplayName());
            }
        }


    }

    //confirm image has been selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);

                uploadImageToFireBaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // to send image selected to FireBase storage
    private void uploadImageToFireBaseStorage() {
        StorageReference profileImageRef =
                FirebaseStorage.getInstance()
                        .getReference("ProfilePics/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            profileImageUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //to choose an image from storage
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), CHOOSE_IMAGE);
    }

    //Validation where both Name and Contact must be filled
    private void saveUserInformation() {
        String name = editTextName.getText().toString().trim();
        if (name.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return;
        }
        String contact = editTextContact.getText().toString().trim();
        if (contact.isEmpty()) {
            editTextContact.setError("Contact is required");
            editTextContact.requestFocus();
            return;
        }
            //create username and contact in the database
        UserInformation userInformation = new UserInformation(name, contact);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null && profileImageUrl != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .setDisplayName(name)
                    .setDisplayName(contact)
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        databaseReference.child(user.getUid()).setValue(userInformation);
        Toast.makeText(this, "Information has been saved successfully...", Toast.LENGTH_LONG).show();
    }

    //save user information or log out of account
    @Override
    public void onClick(View view) {
        //logout button pressed
        if (view == buttonLogout) {
            //logout the user
            firebaseAuth.signOut();
            //start main activity
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        if (view == buttonSave) {
            saveUserInformation();
        }
    }
}
