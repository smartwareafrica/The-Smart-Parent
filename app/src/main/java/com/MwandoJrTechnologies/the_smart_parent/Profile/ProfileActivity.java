package com.MwandoJrTechnologies.the_smart_parent.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewUserEmail;
    private EditText editTextName;
    private EditText editTextContact;
    private EditText editTextUsername;
    private EditText editTextDOB;
    private CircleImageView profileImage;
    private Button buttonSave;
    private Toolbar toolbar;

    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    private StorageReference userProfileImageRef;
    String currentUserID;

    private ProgressDialog progressDialog;
    final static int galleryPick = 1;
    private String downloadImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        profileImage = (CircleImageView) findViewById(R.id.profile_image_view);

        editTextUsername = (EditText) findViewById(R.id.edit_text_username);
        editTextName = (EditText) findViewById(R.id.edit_text_name);
        editTextContact = (EditText) findViewById(R.id.edit_text_phone_number);
        editTextDOB = (EditText) findViewById(R.id.edit_text_dob);

        buttonSave = (Button) findViewById(R.id.buttonSave);

        progressDialog = new ProgressDialog(this);

        FirebaseUser user = mAuth.getCurrentUser();

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText(String.format("Welcome to THE SMART PARENT %s", user.getEmail()));

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfUserNameExists();
                saveUserInformation();
                //start main activity
                finish();
                SendUserToMainActivity();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opening gallery to choose image
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, galleryPick);
            }
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

                        //  Bitmap bm = StringToBitMap(image);
                        // profileImage.setImageBitmap(bm);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Please select a profile picture first", Toast.LENGTH_LONG).show();
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
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();
                /*
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);

                    profileImage.setImageBitmap(bitmap);
                    bmString = BitMapToString(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */


                //creating a filepath for pushing cropped image to fireBase storage by unique user id
                final StorageReference filePath = userProfileImageRef.child(resultUri.getLastPathSegment() + currentUserID + ".jpg");

                //now store in fireBase storage
                final UploadTask uploadTask = filePath.putFile(resultUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> UriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                //get the url...INITIALISE downloadImageUrl at the most to ie....String downloadImageUrl
                                downloadImageUrl = filePath.getDownloadUrl().toString();
                                return filePath.getDownloadUrl();


                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful()) {
                                    //get the link
                                    downloadImageUrl = task.getResult().toString();
                                    addLinkToFireBaseDatabase();
                                    Toast.makeText(ProfileActivity.this, "Good!! ", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                });
            }
        }
    }

    private void addLinkToFireBaseDatabase() {

        usersReference.child("profileImage").setValue(downloadImageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent selfIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
                    startActivity(selfIntent);
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "profile image uploaded successfully uploaded....wow... ", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    String message = task.getException().getMessage();
                    Toast.makeText(ProfileActivity.this, "Error occurred  " + message, Toast.LENGTH_SHORT).show();


                }
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

    //Validation where both Name and Contact must be filled and saved to fireBase database
    private void saveUserInformation() {
        final String username = editTextUsername.getText().toString().trim();
        String fullName = editTextName.getText().toString().trim();
        String contact = editTextContact.getText().toString().trim();
        String dob = editTextDOB.getText().toString().trim();

        //creating a username and checking if any other exists
        if (username.isEmpty()) {
            editTextUsername.setError("You must select a username");
            editTextUsername.requestFocus();
        }
        if (fullName.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
        }
        if (contact.isEmpty()) {
            editTextContact.setError("Contact is required");
            editTextContact.requestFocus();
        }
        if (dob.isEmpty()) {
            editTextDOB.setError("Date of birth is required");
            editTextDOB.requestFocus();
        } else {

            progressDialog.setTitle("Uploading Details...");
            progressDialog.setMessage("Saving your information, Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);

            final HashMap userMap = new HashMap();
            userMap.put("userName", username);
            userMap.put("fullName", fullName);
            userMap.put("contact", contact);
            userMap.put("dob", dob);
            userMap.put("gender", "none");
            userMap.put("numberOfChildren", "none");
            usersReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        SendUserToMainActivity();
                        Toast.makeText(ProfileActivity.this, "Your details saved successfully", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(ProfileActivity.this, "An error occurred,please try again " + message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    //open main activity
    private void SendUserToMainActivity() {
        Intent registerIntent = new Intent(ProfileActivity.this, MainActivity.class);
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
                    Toast.makeText(ProfileActivity.this, "Username Taken. Please choose a different Username", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //display error
            }
        });
        return true;
    }

  /*
    //convert bitmap to string

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


    //convert string to bitmap
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
    */
}
