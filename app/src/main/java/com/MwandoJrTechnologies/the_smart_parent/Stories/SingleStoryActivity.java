package com.MwandoJrTechnologies.the_smart_parent.Stories;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

public class SingleStoryActivity extends AppCompatActivity {

    private ImageView singleImage;
    private TextView singleTitle;
    private TextView singleDesc;
    private Button deleteButton;
    private Button backButton;
    private Button homeButton;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    String currentUserID;
    String post_key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_story);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        singleImage = findViewById(R.id.single_story_image_view);
        singleTitle = findViewById(R.id.single_story_title);
        singleDesc = findViewById(R.id.single_story_description);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Stories");
        post_key = getIntent().getExtras().getString("PostID");
        deleteButton = findViewById(R.id.single_story_delete);
        backButton = findViewById(R.id.single_story_back_button);
        homeButton = findViewById(R.id.single_story_home_button);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

     //   homeButton.setOnClickListener(v -> SendUserToMainActivity());

        backButton.setOnClickListener(v -> SendUserToMainActivity());

        deleteButton.setVisibility(View.INVISIBLE);
        deleteButton.setOnClickListener(view -> {
            mDatabase.child(post_key).removeValue();

            SendUserToStoriesActivity();

        });
        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_contents = (String) dataSnapshot.child("contents").getValue();
                String post_image = (String) dataSnapshot.child("imageUrl").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();
                singleTitle.setText(post_title);
                singleDesc.setText(post_contents);
                Picasso.get().load(post_image).into(singleImage);

                if (mAuth.getCurrentUser().getUid().equals(post_uid)) {
                    deleteButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void SendUserToStoriesActivity() {

        Intent storiesActivityIntent = new Intent(SingleStoryActivity.this, StoriesActivity.class);
        startActivity(storiesActivityIntent);

    }

    //open main activity
    private void SendUserToMainActivity() {
        Intent mainActivityIntent = new Intent(SingleStoryActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }
}