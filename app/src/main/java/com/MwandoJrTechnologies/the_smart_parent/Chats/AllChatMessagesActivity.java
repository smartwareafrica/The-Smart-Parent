package com.MwandoJrTechnologies.the_smart_parent.Chats;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class AllChatMessagesActivity extends AppCompatActivity {

    private RecyclerView allChatsList;
    private DatabaseReference chatsReference;
    private DatabaseReference usersReference;
    private FirebaseAuth mAuth;
    private String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chat_messages);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("My Chats");


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        chatsReference = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserID);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");


        allChatsList = findViewById(R.id.all_chats_layout);

    }


    @Override
    protected void onStart() {
        super.onStart();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AllChatMessagesActivity.this);

        allChatsList.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        FirebaseRecyclerOptions<FindParents> options = new FirebaseRecyclerOptions.Builder<FindParents>()
                .setQuery(chatsReference, FindParents.class)
                .build();

        FirebaseRecyclerAdapter<FindParents, allChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<FindParents, allChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull allChatsViewHolder allChatsViewHolder, int position, @NonNull FindParents findParents) {
                        //get the user ID of each of the users message in the messages node
                        final String usersIDs = getRef(position).getKey();

                        usersReference.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChild("profileImage")) {
                                    final String retrieveProfileImage = dataSnapshot.child("profileImage").getValue().toString();

                                    //display profile picture
                                    Picasso.get()
                                            .load(retrieveProfileImage)
                                            .placeholder(R.drawable.profile_image_placeholder)
                                            .into(allChatsViewHolder.allChatsProfileImage);
                                }

                                final String retrieveUserFullName = dataSnapshot.child("fullName").getValue().toString();
                                final String retrieveUserStatus = dataSnapshot.child("status").getValue().toString();

                                //now display the values
                                allChatsViewHolder.allChatsUserName.setText(retrieveUserFullName);
                                allChatsViewHolder.allChatsUserStatus.setText(retrieveUserStatus);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public allChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.all_chats_display_layout, parent, false);

                        return new allChatsViewHolder(view);
                    }

                };

        allChatsList.setLayoutManager(linearLayoutManager);
        allChatsList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

        allChatsList.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });

    }


    private static class allChatsViewHolder extends RecyclerView.ViewHolder {

        CircleImageView allChatsProfileImage;
        TextView allChatsUserStatus;
        TextView allChatsUserName;

        public allChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            allChatsProfileImage = itemView.findViewById(R.id.all_chats_profile_image);
            allChatsUserName = itemView.findViewById(R.id.all_chats_profile_full_name);
            allChatsUserStatus = itemView.findViewById(R.id.all_chats_profile_status);
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
        Intent mainActivityIntent = new Intent(AllChatMessagesActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }
}
