package com.MwandoJrTechnologies.the_smart_parent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class FeedbackActivity extends AppCompatActivity {

    private EditText textInputFeedbackMessage;
    private Button buttonFeedbackSend;

    private FirebaseAuth mAuth;
    private DatabaseReference feedbackReference;
    private DatabaseReference usersReference;

    private String currentUserID;
    private String saveCurrentDate;
    private String saveCurrentTime;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getUid();

        //set path in fireBase database
        feedbackReference = FirebaseDatabase.getInstance().getReference().child("Feedback");
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Write your feedback");

        progressDialog = new ProgressDialog(this);

        textInputFeedbackMessage = findViewById(R.id.feedback_edit_text_enter_message);
        buttonFeedbackSend = findViewById(R.id.button_send_feedback);

        //setting current date and time to generate random keys for the users images posted
        //setting current date
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        //setting current date
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(callForTime.getTime());

        buttonFeedbackSend.setOnClickListener(v -> {

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            usersReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        String feedbackName = dataSnapshot.child("fullName").getValue().toString();
                        String feedbackEmail = firebaseUser.getEmail();
                        String feedbackMessage = textInputFeedbackMessage.getText().toString();

                        if  (TextUtils.isEmpty(feedbackMessage)) {
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please input your feedback", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {

                            //validations okay then we show a progress bar
                            progressDialog.setTitle("Sending Feedback");
                            progressDialog.setMessage("Please wait,Sending your feedback..." + "\n\n Thank You For your feedback");
                            progressDialog.show();
                            progressDialog.setCanceledOnTouchOutside(true);

                            final HashMap<String, Object> feedbackMap = new HashMap<String, Object>();
                            feedbackMap.put("uid", currentUserID);
                            feedbackMap.put("userFullName", feedbackName);
                            feedbackMap.put("userEmail", feedbackEmail);
                            feedbackMap.put("feedbackEmail", feedbackMessage);
                            feedbackMap.put("feedBackDate", saveCurrentDate);
                            feedbackMap.put("feedBackTime", saveCurrentTime);
                            feedbackReference.child(currentUserID + saveCurrentDate + saveCurrentTime).updateChildren(feedbackMap).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Thank you for your Feedback", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                    SendUserToMainActivity();
                                } else {
                                    String message = task.getException().getMessage();
                                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "An error occurred,please try again " + message, Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                }
                            });
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

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


    //open main activity
    private void SendUserToMainActivity() {
        Intent mainActivityIntent = new Intent(FeedbackActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }

}
