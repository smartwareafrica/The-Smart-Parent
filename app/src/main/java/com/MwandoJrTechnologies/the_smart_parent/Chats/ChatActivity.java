package com.MwandoJrTechnologies.the_smart_parent.Chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private ImageButton sendMessageButton;
    private ImageButton sendImageFileButton;
    private EditText userMessageInput;
    private RecyclerView userMessagesList;
    private final List<Messages> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messageAdapter;

    private TextView receiverName;
    private CircleImageView receiverProfileImage;

    private String messageReceiverID;
    private String messageReceiverName;
    private String messageSenderID;
    private String saveCurrentDate;
    private String saveCurrentTime;

    private DatabaseReference rootReference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();

        rootReference = FirebaseDatabase.getInstance().getReference();

        //receive user id and name from other parents profile
        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();

        InitializeFields();

        DisplayReceiverInformation();

        sendMessageButton.setOnClickListener(v -> SendMessage());

        FetchMessages();
    }


        //retrieve messages
    private void FetchMessages() {
    rootReference.child("Messages")
            .child(messageSenderID)
            .child(messageReceiverID)
            .addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.exists()){
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messageList.add(messages);
                        messageAdapter.notifyDataSetChanged(); //whenever new message is added it will be displayed
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }


    private void SendMessage() {

        String messageText = userMessageInput.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please type message email", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            //create a node of messages to display to both the sender and receiver

            //create node of messages for the sender
            String message_sender_reference = "Messages/ " + messageSenderID + "/" + messageReceiverID;

            //create node of messages for the receiver
            String message_receiver_reference = "Messages/ " + messageReceiverID + "/" + messageSenderID;

            //create message unique key
            DatabaseReference user_message_key = rootReference
                    .child("Messages")
                    .child(messageSenderID)
                    .child(messageReceiverID)
                    .push(); //method that creates a unique key

            //now get the key created.
            String message_push_id = user_message_key.getKey();

            //setting current date and time to generate random keys for the users images posted
            //setting current date
            Calendar callForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(callForDate.getTime());

            //setting current date
            Calendar callForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            saveCurrentTime = currentTime.format(callForTime.getTime());

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);


            //all message information
            Map messageBodyDetails = new HashMap();
            //now display message for sender and receiver
            messageBodyDetails.put(message_sender_reference + "/" + message_push_id, messageTextBody);
            messageBodyDetails.put(message_receiver_reference + "/" + message_push_id, messageTextBody);

            rootReference.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Message sent successfully", Snackbar.LENGTH_SHORT);
                        snackbar.show();

                        userMessageInput.setText("");

                    }else {
                        String message = task.getException().toString();
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Error: " + message, Snackbar.LENGTH_SHORT);
                        snackbar.show();

                        userMessageInput.setText("");

                    }

                }
            });
        }

    }

    private void DisplayReceiverInformation() {

        receiverName.setText(messageReceiverName);

        rootReference.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    //retrieve from db
                    final String userName = dataSnapshot.child("fullName").getValue().toString();
                    final String profileImage = dataSnapshot.child("profileImage").getValue().toString();

                    //set values
                    receiverName.setText(userName);
                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_image_placeholder).into(receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //initialize the above variables
    private void InitializeFields() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //connect chat custom bar to chat activity
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_toolbar, null);
        actionBar.setCustomView(action_bar_view);

        receiverName = findViewById(R.id.custom_profile_name);
        receiverProfileImage = findViewById(R.id.custom_profile_image);

        sendMessageButton = findViewById(R.id.send_message_button);
       // sendImageFileButton = findViewById(R.id.send_image_file_button);
        userMessageInput = findViewById(R.id.input_message);

        messageAdapter = new MessagesAdapter(messageList);
        userMessagesList = findViewById(R.id.messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);
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
        Intent mainActivityIntent = new Intent(ChatActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }
}

