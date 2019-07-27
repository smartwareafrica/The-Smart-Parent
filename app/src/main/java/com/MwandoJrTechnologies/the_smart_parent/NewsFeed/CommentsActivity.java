package com.MwandoJrTechnologies.the_smart_parent.NewsFeed;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.MwandoJrTechnologies.the_smart_parent.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CommentsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private RecyclerView commentsList;
    private ImageButton postCommentButton;
    private EditText commentsInputText;

    private DatabaseReference usersRef;
    private DatabaseReference postsReference;
    private FirebaseAuth mAuth;

    private String post_key;
    private String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Responses");


        post_key = getIntent().getExtras().get("PostKey").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Posts")
                .child(post_key)
                .child("Comments");


        commentsList = findViewById(R.id.comments_list);
        commentsList.setHasFixedSize(true);

        commentsInputText = findViewById(R.id.comment_input);
        postCommentButton = findViewById(R.id.post_comment_button);

        postCommentButton
                .setOnClickListener(v -> usersRef.child(currentUserID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    ValidateComment();

                                    commentsInputText.setText("");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }));

    }

    @Override
    protected void onStart() {
        super.onStart();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentsList.setLayoutManager(linearLayoutManager);

        final FirebaseRecyclerOptions<Comments> options =
                new FirebaseRecyclerOptions.Builder<Comments>()
                        .setQuery(postsReference, Comments.class)
                        .build();

        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> fireBaseRecyclerAdapter = new
                 FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
                    @Override
                    protected void
                    onBindViewHolder
                            (@NonNull CommentsViewHolder commentsViewHolder,
                             int i,
                             @NonNull Comments comments) {
                        comments = getItem(i);

                        commentsViewHolder.myFullName.setText(comments.getFullName());
                        commentsViewHolder.myComment.setText(comments.getComment());
                        commentsViewHolder.myDate.setText(comments.getDate());
                        commentsViewHolder.myTime.setText(comments.getTime());

                    }

                    @NonNull
                    @Override
                    public CommentsViewHolder onCreateViewHolder
                            (@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.all_comments_layout, parent, false);
                        CommentsViewHolder commentsViewHolder = new CommentsViewHolder(view);
                        return commentsViewHolder;
                    }
                };

        commentsList.setLayoutManager(linearLayoutManager);
        commentsList.setAdapter(fireBaseRecyclerAdapter);
        fireBaseRecyclerAdapter.startListening();

    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {

        TextView myFullName;
        TextView myComment;
        TextView myDate;
        TextView myTime;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            myFullName = itemView.findViewById(R.id.comment_user_full_name);
            myComment = itemView.findViewById(R.id.comment_text);
            myDate = itemView.findViewById(R.id.comment_date);
            myTime = itemView.findViewById(R.id.comment_time);

        }
    }

    private void ValidateComment() {

        String commentText = commentsInputText.getText().toString();

        if (TextUtils.isEmpty(commentText)) {
            Snackbar snackBar = Snackbar
                    .make(findViewById(android.R.id.content),
                            "Please write text to respond...",
                            Snackbar.LENGTH_SHORT);
            snackBar.show();
        } else {
            //setting current date and time to generate random keys for the users images posted
            //setting current date
            Calendar callForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(callForDate.getTime());

            //setting current date
            Calendar callForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            final String saveCurrentTime = currentTime.format(callForTime.getTime());

            final String randomKey = currentUserID + saveCurrentDate + saveCurrentTime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid", currentUserID);
            commentsMap.put("comment", commentText);
            commentsMap.put("date", saveCurrentDate);
            commentsMap.put("time", saveCurrentTime);

            postsReference.child(randomKey).updateChildren(commentsMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Snackbar snackBar = Snackbar
                                    .make(findViewById(android.R.id.content),
                                            "Response Added", Snackbar.LENGTH_SHORT);
                            snackBar.show();
                        } else {
                            Snackbar snackBar = Snackbar
                                    .make(findViewById(android.R.id.content),
                                            "Could not comment, Please try again",
                                            Snackbar.LENGTH_SHORT);
                            snackBar.show();
                        }

                    });

        }

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
        Intent mainActivityIntent = new
                Intent(CommentsActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }
}
