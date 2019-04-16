package com.MwandoJrTechnologies.the_smart_parent.Stories;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.Profile.RegisterActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String currentUserID;

    private FloatingActionButton addStoryFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Stories");


        recyclerView = findViewById(R.id.stories_recycler_view);
        recyclerView.setHasFixedSize(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Stories");
        mAuth = FirebaseAuth.getInstance();
        currentUserID =mAuth.getCurrentUser().getUid();

        mAuthListener = firebaseAuth -> {
            if (mAuth.getCurrentUser() == null) {
                Intent loginIntent = new Intent(StoriesActivity.this, RegisterActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
            }
        };

        addStoryFAB = findViewById(R.id.fab_to_add_story_activity);

        if (!currentUserID.equals("K7Ng2Q3dXiQIGo56hjfsvkAvFgB2") ){
            addStoryFAB.hide();
        }
        addStoryFAB.setOnClickListener(v -> SendUserToCreateStoryActivity());

        DisplayAllStoriesLayouts();
    }

    private void DisplayAllStoriesLayouts() {

        mAuth.addAuthStateListener(mAuthListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StoriesActivity.this);
        //initialize recyclerView and FIreBase objects
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        final FirebaseRecyclerOptions<StoriesModalAdapterClass> options =
                new FirebaseRecyclerOptions.Builder<StoriesModalAdapterClass>()
                        .setQuery(mDatabase, StoriesModalAdapterClass.class)
                        .build();


        FirebaseRecyclerAdapter<StoriesModalAdapterClass, StoriesModalAdapterClassViewHolder> firebaseRecyclerAdapter = new

                FirebaseRecyclerAdapter<StoriesModalAdapterClass, StoriesModalAdapterClassViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull StoriesModalAdapterClassViewHolder viewHolder, int position, @NonNull StoriesModalAdapterClass model) {

                        final String post_key = getRef(position).getKey();
                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setDesc(model.getcontents());
                        viewHolder.setImageUrl(getApplicationContext(), model.getImageUrl());
                        viewHolder.setAuthorName(model.getAuthorName());
                        viewHolder.mView.setOnClickListener(view -> {
                            Intent singleActivity = new Intent(StoriesActivity.this, SingleStoryActivity.class);
                            singleActivity.putExtra("PostID", post_key);
                            startActivity(singleActivity);
                        });
                    }

                    @NonNull
                    @Override
                    public StoriesModalAdapterClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.stories_card_items, parent, false);

                        StoriesModalAdapterClassViewHolder viewHolder = new StoriesModalAdapterClassViewHolder(view);
                        return viewHolder;
                    }
                };

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
        firebaseRecyclerAdapter.startListening();

    }

    public static class StoriesModalAdapterClassViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public StoriesModalAdapterClassViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView post_title = mView.findViewById(R.id.post_title_text_view);
            post_title.setText(title);
        }

        public void setDesc(String desc) {
            TextView post_desc = mView.findViewById(R.id.post_desc_text_view);
            post_desc.setText(desc);
        }

        public void setAuthorName(String authorName) {
            TextView post_author_name = mView.findViewById(R.id.post_author_user);
            post_author_name.setText(authorName);
        }

        public void setImageUrl(Context ctx, String imageUrl) {
            ImageView post_image = mView.findViewById(R.id.post_image);
            Picasso.get().load(imageUrl).into(post_image);
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
        Intent mainActivityIntent = new Intent(StoriesActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }

    private void SendUserToCreateStoryActivity() {

        Intent createStoryActivityIntent = new Intent(StoriesActivity.this, CreateStoryActivity.class);
        finish();
        startActivity(createStoryActivityIntent);

    }

}