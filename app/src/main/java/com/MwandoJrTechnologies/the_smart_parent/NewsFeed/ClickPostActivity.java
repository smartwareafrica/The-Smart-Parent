package com.MwandoJrTechnologies.the_smart_parent.NewsFeed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ClickPostActivity extends AppCompatActivity {

    private ImageView postImage;
    private TextView postDescription;
    private Button editPostButton;
    private Button deletePostButton;

    private Toolbar toolbar;

    private DatabaseReference clickPostRef;
    private FirebaseAuth mAuth;

    private String description;
    private String image;
    private String postKey;
    private String currentUserID;
    private String databaseUserID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Edit your Query");


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();


        //get the unique key for your post
        postKey = getIntent().getExtras().get("PostKey").toString();
        clickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey);

        postImage = (ImageView) findViewById(R.id.post_image);
        postDescription = (TextView) findViewById(R.id.edit_post_description);
        editPostButton  = (Button) findViewById(R.id.edit_post_button);
        deletePostButton = (Button) findViewById(R.id.delete_post_button);


        //when app opens buttons are invisible
        editPostButton.setVisibility(View.INVISIBLE);
        deletePostButton.setVisibility(View.INVISIBLE);

        clickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    description = dataSnapshot.child("description").getValue().toString();
                    image = dataSnapshot.child("postImage").getValue().toString();
                    databaseUserID = dataSnapshot.child("uid").getValue().toString();


                    postDescription.setText(description);
                    Picasso.get().load(image).into(postImage);


                    if (currentUserID.equals(databaseUserID)){

                        //now make buttons visible
                        editPostButton.setVisibility(View.VISIBLE);
                        deletePostButton.setVisibility(View.VISIBLE);
                    }

                    editPostButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditCurrentPost(description);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        deletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteCurrentPost();
            }
        });
    }

    private void EditCurrentPost(String description) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Query");

        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(description);
        builder.setView(inputField);


        //create two buttons to update and cancel
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                clickPostRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this, "Post Updated Successfully.", Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.background_light);

    }


    //to delete given post
    private void DeleteCurrentPost() {

        clickPostRef.removeValue();
        SendUserToMainActivity();
        Toast.makeText(this, "Query deleted", Toast.LENGTH_SHORT).show();

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
        Intent mainActivityIntent = new Intent(ClickPostActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
}}
