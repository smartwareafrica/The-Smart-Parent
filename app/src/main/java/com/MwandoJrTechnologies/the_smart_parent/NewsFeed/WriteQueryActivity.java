package com.MwandoJrTechnologies.the_smart_parent.NewsFeed;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class WriteQueryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    private DatabaseReference postsReference;
    private StorageReference postImagesReference;

    private EditText editTextWriteQuery;
    private ImageButton selectPostImage;
    private Button buttonPost;

    final static int galleryPick = 1;

    private Uri imageUri;

    //all strings
    private String post;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postRandomName;
    private String downloadUrl;
    private String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_query);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();

        postImagesReference = FirebaseStorage.getInstance().getReference();
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        postsReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        //cast to views
        editTextWriteQuery = (EditText) findViewById(R.id.edit_text_write_query);
        selectPostImage = (ImageButton) findViewById(R.id.image_button_post);
        buttonPost = (Button) findViewById(R.id.button_post);
        progressDialog = new ProgressDialog(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Write query");

        //when clicked or touched
        selectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInformation();
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

    //opens gallery for upload
    private void OpenGallery() {
        //opening gallery to choose image
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPick);
    }

    //after selecting the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();
            //display image on link
            selectPostImage.setImageURI(imageUri);
        }
    }

    //check that a query must be written
    private void ValidatePostInformation() {
        post = editTextWriteQuery.getText().toString();
        if (TextUtils.isEmpty(post)) {
            Toast.makeText(this, "Please ask a question", Toast.LENGTH_SHORT).show();
        } else {

            //show progress dialog
            progressDialog.setTitle("Adding new Post");
            progressDialog.setMessage("Updating post, Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);

            StoreImageToFireBaseStorage();
        }
    }

    private void StoreImageToFireBaseStorage() {
        //setting current date and time to generate random keys for the users images posted
        //setting current date
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        //setting current date
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = postImagesReference.child("PostsImages").child(imageUri.getLastPathSegment() + postRandomName + ".jpg");

       filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {
                    downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();

                    Toast.makeText(WriteQueryActivity.this, "Image upload successfully", Toast.LENGTH_SHORT).show();

                    SavingPostInformationToDatabase();
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(WriteQueryActivity.this, "An Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //saves details of image to fireBase storage
    private void SavingPostInformationToDatabase() {
        usersReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //validation if child exists then we execute
                if (dataSnapshot.exists()) {
                    String userFullName = dataSnapshot.child("fullName").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileImage").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", currentUserID);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", post);
                    postsMap.put("postImage", downloadUrl);
                    postsMap.put("profileImage", userProfileImage);
                    postsMap.put("fullName", userFullName);
                    //now save inside fireBase database
                    postsReference.child(currentUserID + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        SendUserToMainActivity();
                                        Toast.makeText(WriteQueryActivity.this, "New Question Updated Successfully.", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    } else {
                                        Toast.makeText(WriteQueryActivity.this, "Please try again. An error occurred", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //open main activity
    private void SendUserToMainActivity() {
        Intent mainActivityIntent = new Intent(WriteQueryActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }

    /*
    //convert bitmap to string
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    */
}
