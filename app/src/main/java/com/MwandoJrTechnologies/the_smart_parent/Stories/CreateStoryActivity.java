package com.MwandoJrTechnologies.the_smart_parent.Stories;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CreateStoryActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ImageButton imageBtn;
    private static final int GALLERY_REQUEST_CODE = 2;
    private Uri uri = null;
    private EditText textTitle;
    private EditText textDesc;
    private EditText storyAuthorName;
    private Button postBtn;
    private StorageReference storage;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Post a story");

        postBtn = findViewById(R.id.postBtn);
        textDesc = findViewById(R.id.textDesc);
        textTitle = findViewById(R.id.textTitle);
        storyAuthorName = findViewById(R.id.text_author_name);

        storage = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Stories");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        imageBtn = findViewById(R.id.imageBtn);
        //picking image from gallery
        imageBtn.setOnClickListener(view -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
        });
        // posting to Firebase
        postBtn.setOnClickListener(view -> {

            progressDialog = new ProgressDialog(CreateStoryActivity.this);
            progressDialog.setTitle("Adding Story");
            progressDialog.setMessage("Uploading, Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);

            final String PostTitle = textTitle.getText().toString().trim();
            final String PostDesc = textDesc.getText().toString().trim();
            final String authorName = storyAuthorName.getText().toString().trim();
            // do a check for empty fields
            if (PostTitle.isEmpty()) {
                textTitle.setError("Write title");
            } else if (PostDesc.isEmpty()) {
                textDesc.setError("Add a story please");
            } else if (authorName.isEmpty()) {
                storyAuthorName.setError("Please name your author");
            } else {



                StorageReference filepath = storage.child("StoriesImages").child(uri.getLastPathSegment());
                filepath.putFile(uri).addOnSuccessListener(taskSnapshot -> {

                    //getting the post image download url
                    filepath.getDownloadUrl().addOnSuccessListener(uri -> {
                        final Uri downloadUrl = uri;

                        final DatabaseReference newPost = databaseRef.push();
                        //adding post contents to database reference
                        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                newPost.child("title").setValue(PostTitle);
                                newPost.child("contents").setValue(PostDesc);
                                newPost.child("authorName").setValue(authorName);
                                newPost.child("imageUrl").setValue(downloadUrl.toString());
                                newPost.child("uid").setValue(mCurrentUser.getUid())
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                SendUserToStoriesActivity();
                                            }
                                        });
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    });
                });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //image from gallery result
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            uri = data.getData();
            imageBtn.setImageURI(uri);
        }
    }

    //activate back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            SendUserToStoriesActivity();
        }
        return super.onOptionsItemSelected(item);
    }


    // Opens the stories activity
    private void SendUserToStoriesActivity() {
        Intent intent = new Intent(CreateStoryActivity.this, StoriesActivity.class);
        startActivity(intent);
    }
}