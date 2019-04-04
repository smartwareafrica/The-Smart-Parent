package com.MwandoJrTechnologies.the_smart_parent.Chats;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.Profile.OtherParentsProfileActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchOtherParentsActivity extends AppCompatActivity {

    private AppCompatButton searchButton;
    private SearchView searchInputText;

    private RecyclerView searchResultList;

    private DatabaseReference allParentsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_other_parents);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Find Parent");


        allParentsReference = FirebaseDatabase.getInstance().getReference().child("Users");


        searchButton = findViewById(R.id.search_parents_button);
        searchInputText = findViewById(R.id.search_box_input);

        searchResultList = findViewById(R.id.search_result_list);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));

        searchButton.setOnClickListener(v -> {

            String searchBoxInput = searchInputText.getQuery().toString();

            SearchOtherParentsByName(searchBoxInput);
        });
    }


    private void SearchOtherParentsByName(String searchBoxInput) {

        Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), "Searching", Snackbar.LENGTH_LONG);
        snackBar.show();

        //the search query
        Query searchParentsQuery = allParentsReference.orderByChild("fullName")
                .startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");


        final FirebaseRecyclerOptions<FindParents> options = new FirebaseRecyclerOptions.Builder<FindParents>()
                .setQuery(searchParentsQuery, FindParents.class)
                .build();

        FirebaseRecyclerAdapter<FindParents, FindParentsViewHolder>
                fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<FindParents, FindParentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindParentsViewHolder findParentsViewHolder, int i, @NonNull FindParents findParents) {
                findParents = getItem(i);

                final String userName = findParents.getFullName();
                final String userID = findParents.getUid();

                findParentsViewHolder.setProfileImage(findParents.getProfileImage());
                findParentsViewHolder.setFullName(findParents.getFullName());
                findParentsViewHolder.setStatus(findParents.getStatus());


                findParentsViewHolder.itemView.setOnClickListener(v -> {

                    //create an alert builder box
                    CharSequence options[] = new CharSequence[]{

                          "View " + userName + "'s Profile",
                            "Send Message"

                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchOtherParentsActivity.this);
                    builder.setTitle("Profile Options");

                    //now make it selectable
                    builder.setItems(options, (dialog, which) -> {

                        if (which == 0) {

                            Intent profileIntent = new Intent(SearchOtherParentsActivity.this, OtherParentsProfileActivity.class);
                            profileIntent.putExtra("visit_user_id", userID);
                            startActivity(profileIntent);

                        }
                        if (which == 1) {

                            Intent chatIntent = new Intent(SearchOtherParentsActivity.this, ChatActivity.class);
                            chatIntent.putExtra("visit_user_id", userID);
                            startActivity(chatIntent);
                        }

                    });

                    builder.show();

                });
            }

            @NonNull
            @Override
            public FindParentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.all_parents_search_result_layout, parent, false);
                FindParentsViewHolder findParentsViewHolder = new FindParentsViewHolder(view);

                return findParentsViewHolder;
            }
        };

        searchResultList.setAdapter(fireBaseRecyclerAdapter);
        fireBaseRecyclerAdapter.startListening();

    }

    //create a static class for fireBase recycler adapter
    public static class FindParentsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FindParentsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileImage(String profileImage) {

            CircleImageView myProfileImage = mView.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(profileImage).placeholder(R.drawable.profile_image_placeholder).into(myProfileImage);

        }

        public void setFullName(String fullName) {
            TextView myFullName = mView.findViewById(R.id.all_users_profile_full_name);
            myFullName.setText(fullName);
        }

        public void setStatus(String status) {
            TextView myStatus = mView.findViewById(R.id.all_users_profile_status);
            myStatus.setText(status);
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
        Intent mainActivityIntent = new Intent(SearchOtherParentsActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }
}
