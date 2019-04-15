package com.MwandoJrTechnologies.the_smart_parent.Stories;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

import androidx.appcompat.app.AppCompatActivity;

public class CreateStoryActivity extends AppCompatActivity {

    // imports
    private ImageButton imageBtn;
    private static final int GALLERY_REQUEST_CODE = 2;
    private Uri uri = null;
    private EditText textTitle;
    private EditText textDesc;
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
        // initializing objects
        postBtn = findViewById(R.id.postBtn);
        textDesc = findViewById(R.id.textDesc);
        textTitle = findViewById(R.id.textTitle);
        storage = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Blogzone");
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
            Toast.makeText(CreateStoryActivity.this, "POSTING...", Toast.LENGTH_LONG).show();
            final String PostTitle = textTitle.getText().toString().trim();
            final String PostDesc = textDesc.getText().toString().trim();
            // do a check for empty fields
            if (!TextUtils.isEmpty(PostDesc) && !TextUtils.isEmpty(PostTitle)) {
                StorageReference filepath = storage.child("post_images").child(uri.getLastPathSegment());
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
                                newPost.child("desc").setValue(PostDesc);
                                newPost.child("imageUrl").setValue(downloadUrl.toString());
                                newPost.child("uid").setValue(mCurrentUser.getUid());
                                newPost.child("username").setValue(dataSnapshot.child("name").getValue())
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(CreateStoryActivity.this, StoriesActivity.class);
                                                startActivity(intent);
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
}