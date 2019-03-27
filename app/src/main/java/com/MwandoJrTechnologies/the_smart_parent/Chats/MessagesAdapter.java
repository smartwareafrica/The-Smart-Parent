package com.MwandoJrTechnologies.the_smart_parent.Chats;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersDatabaseReference;

    public MessagesAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView senderMessageText;
        public TextView receiverMessageText;
        public CircleImageView receiverProfileImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_message_text_view);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text_view);
            receiverProfileImage = itemView.findViewById(R.id.receiver_message_profile_image);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext())
                 .inflate(R.layout.message_layout_of_users, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        //get senders ID
        String messageSenderID = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        //get ID of the receiver
        String fromUserID = messages.getFrom();
        //noe get type of message whether text or image
        String fromMessageType = messages.getType();


        //retrieve receivers profile image
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
        usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String image = dataSnapshot.child("profileImage").getValue().toString();
                    Picasso.get().load(image).placeholder(R.drawable.profile_image_placeholder)
                            .into(holder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (fromMessageType.equals("text")){
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            holder.receiverProfileImage.setVisibility(View.INVISIBLE);

                    //display the message to the sender and to the receiver
            //for the receiver
            if (fromUserID.equals(messageSenderID)) {
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_text_message_background);
                holder.senderMessageText.setTextColor(Color.WHITE);
                holder.senderMessageText.setGravity(Gravity.START);
                holder.senderMessageText.setText(messages.getMessage());

                //for receiver
            } else {
                holder.senderMessageText.setVisibility(View.INVISIBLE);

                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverProfileImage.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_text_message_background);
                holder.receiverMessageText.setTextColor(Color.WHITE);
                holder.receiverMessageText.setGravity(Gravity.START);
                holder.receiverMessageText.setText(messages.getMessage());
            }
        }

    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }
}
