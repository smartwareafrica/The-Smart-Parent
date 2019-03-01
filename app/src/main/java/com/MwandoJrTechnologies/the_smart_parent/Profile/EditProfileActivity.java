package com.MwandoJrTechnologies.the_smart_parent.Profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.MwandoJrTechnologies.the_smart_parent.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.MwandoJrTechnologies.the_smart_parent.UserInformation;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CHOOSE_IMAGE = 101;
    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    private EditText editTextName;
    private EditText editTextContact;
    private EditText editTextCreateUsername;
    private ImageView imageView;
    private Button buttonSave;
    private Toolbar toolbar;

    private DatabaseReference databaseReference;

    Uri uriProfileImage;
    ProgressBar progressBar;

    String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit profile");

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginFragment.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Profiles");
        editTextContact = (EditText) findViewById(R.id.editTextContact);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextCreateUsername = (EditText) findViewById(R.id.editTextCreateUsername);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        imageView = (ImageView) findViewById(R.id.imageView);

        progressBar = findViewById(R.id.progressBar);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText("Welcome to THE SMART PARENT " + user.getEmail());

        buttonSave.setOnClickListener(this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginFragment.class));
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

        final StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("ProfilePics/" + System.currentTimeMillis() + ".jpg");

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
                            Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        }
        String contact = editTextContact.getText().toString().trim();
        if (contact.isEmpty()) {
            editTextContact.setError("Contact is required");
            editTextContact.requestFocus();
        }
        //creating a username and checking if any other exists
        final String username = editTextCreateUsername.getText().toString().trim();
        if (username.isEmpty()){
            editTextCreateUsername.setError("You must select a username");
            editTextCreateUsername.requestFocus();
        }


        //create name contact and username in the database
        UserInformation userInformation = new UserInformation(name, contact, username, profileImageUrl);

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

                                Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        databaseReference.child(user.getUid()).setValue(userInformation);
        Toast.makeText(this, "Information saved successfully...", Toast.LENGTH_LONG).show();

    }


    //save user information or log out of account
    @Override
    public void onClick(View view) {
        checkIfUserNameExists();

    }

    private boolean checkIfUserNameExists() {

        String username = editTextCreateUsername.getText().toString().trim();
        Query usernameQuery = FirebaseDatabase.getInstance()
                .getReference()
                .child("Profiles")
                .orderByChild("username")
                .equalTo(username);
        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {
                    Toast.makeText(EditProfileActivity.this, "Username Taken. Please choose a different Username", Toast.LENGTH_SHORT).show();
                } else {
                    saveUserInformation();
                    //start home fragment
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
