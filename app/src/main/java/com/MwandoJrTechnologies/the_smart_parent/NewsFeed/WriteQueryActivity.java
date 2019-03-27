package com.MwandoJrTechnologies.the_smart_parent.NewsFeed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WriteQueryActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference usersReference;
    private DatabaseReference postsReference;

    private EditText editTextWriteQuery;
    private Button buttonPost;

    //all strings
    private String post;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postRandomName;
    private String currentUserID;

    //variable that counts the number of posts in the database
    private long countPosts = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_query);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Write your Query");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        postsReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        //cast to views
        editTextWriteQuery = findViewById(R.id.edit_text_write_query);
        buttonPost = findViewById(R.id.button_post);
        progressDialog = new ProgressDialog(this);

        buttonPost.setOnClickListener(v -> ValidatePostInformation());

    }


    //check that a query must be written
    private void ValidatePostInformation() {
        post = editTextWriteQuery.getText().toString();
        if (TextUtils.isEmpty(post)) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please ask a question", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {

            //show progress dialog
            progressDialog.setTitle("Adding new Post");
            progressDialog.setMessage("Updating post, Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);

            SaveDateAndTimeToFireBaseStorage();
            SavingPostInformationToDatabase();
        }
    }

    private void SaveDateAndTimeToFireBaseStorage() {
        //setting current date and time to generate random keys for the users images posted
        //setting current date
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        //setting current date
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

    }

    //saves details of image to fireBase storage
    private void SavingPostInformationToDatabase() {

        //counting posts in database then store in variable countPosts and display newest at the top
        postsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    countPosts = dataSnapshot.getChildrenCount();
                } else {
                    countPosts = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        usersReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //validation if child exists then we execute
                if (dataSnapshot.exists()) {
                    final String userFullName = dataSnapshot.child("fullName").getValue().toString();
                    final String userProfileImage = dataSnapshot.child("profileImage").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", currentUserID);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", post);
                    postsMap.put("profileImage", userProfileImage);
                    postsMap.put("fullName", userFullName);
                    postsMap.put("counter", countPosts); //saves counted value in database
                    //now save inside fireBase database
                    postsReference.child(currentUserID + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                     SendUserToMainActivity();
                                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "New Question Updated Successfully.", Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                    progressDialog.dismiss();
                                } else {
                                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please try again. An error occurred", Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                    progressDialog.dismiss();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;

    }

    //open main activity
    private void SendUserToMainActivity() {
        Intent mainActivityIntent = new Intent(WriteQueryActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }
}
