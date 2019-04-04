package com.MwandoJrTechnologies.the_smart_parent.NewsFeed;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.MwandoJrTechnologies.the_smart_parent.Chats.SearchOtherParentsActivity;
import com.MwandoJrTechnologies.the_smart_parent.ConnectionChecker;
import com.MwandoJrTechnologies.the_smart_parent.FeedbackActivity;
import com.MwandoJrTechnologies.the_smart_parent.Profile.EditProfileActivity;
import com.MwandoJrTechnologies.the_smart_parent.Profile.LoginActivity;
import com.MwandoJrTechnologies.the_smart_parent.Profile.OtherParentsProfileActivity;
import com.MwandoJrTechnologies.the_smart_parent.Profile.ProfileActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.MwandoJrTechnologies.the_smart_parent.Reminders.RemindersActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    //initializing
    private DrawerLayout mDrawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView postList;
    private Toolbar toolbar;

    private CircleImageView navProfileImage;
    private TextView navProfileName;
    private TextView navUsername;
    private ImageButton addNewQueryButton;
    private FloatingActionButton fab;
    private ProgressBar newsFeedProgressBar;

    private SwipeRefreshLayout swipeRefreshLayout;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private DatabaseReference postsReference;
 //   private DatabaseReference commentsReference;
    String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsReference = FirebaseDatabase.getInstance().getReference().child("Posts");
       // commentsReference = FireBaseDatabase.getInstance().getReference().child("Posts");

        //inflate
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addNewQueryButton = findViewById(R.id.add_new_query_button);

        mDrawer = findViewById(R.id.drawer_layout);
        //navigation drawer toggle
        drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        // Find our navigation drawer view
        navigationView = findViewById(R.id.navigation_view);
        View navView = navigationView.getHeaderView(0);

        postList = findViewById(R.id.all_users_query_list);
        //give it a fixed size
        postList.setHasFixedSize(true);


        fab = findViewById(R.id.fab);
        newsFeedProgressBar = findViewById(R.id.news_feed_progress_bar);
        swipeRefreshLayout = findViewById(R.id.swipeToRefresh);

        navProfileImage = navView.findViewById(R.id.nav_profile_image);
        navProfileName = navView.findViewById(R.id.nav_user_full_name);
        navUsername = navView.findViewById(R.id.nav_username);

        fab.setOnClickListener(v -> SendUserToWriteQueryActivity());

        checkConnectionStatus();
        //refresh on swipe
        swipeRefreshLayout.setOnRefreshListener(() -> {

            final Handler handler = new Handler();
            handler.postDelayed(() -> checkConnectionStatus(), 500);

            DisplayAllUsersQueries();
            swipeRefreshLayout.setRefreshing(false);
        });

        //for navigation drawer
        //display current logged in user details only
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //check for the profile image, username and name
                    if (dataSnapshot.hasChild("fullName")) {
                        //only display name if it exists
                        String fullName = dataSnapshot.child("fullName").getValue().toString();
                        //code to display
                        navProfileName.setText(fullName);
                    }
                    if (dataSnapshot.hasChild("userName")) {
                        //only display name if it exists
                        String userName = dataSnapshot.child("userName").getValue().toString();
                        //code to display
                        navUsername.setText(userName);
                    }
                    if (dataSnapshot.hasChild("profileImage")) {
                        //display only if there is an image
                        String image = dataSnapshot.child("profileImage").getValue().toString();

                        Picasso.get()
                                .load(image)
                                .placeholder(R.drawable.profile_image_placeholder)
                                .into(navProfileImage);

                    } else {
                        Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), "You need to update your profile", Snackbar.LENGTH_LONG);
                        snackBar.show();


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(menuItem -> {

            UserMenuSelector(menuItem);
            return false;
        });

        addNewQueryButton.setOnClickListener(v -> SendUserToWriteQueryActivity());

        DisplayAllUsersQueries();

        newsFeedProgressBar.setVisibility(View.INVISIBLE);


    }


    //checking internet state
    private void checkConnectionStatus() {

        if (ConnectionChecker.isConnectedToNetwork(this)) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Internet Connection", Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "NETWORK ERROR! Please check your Internet connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }


    private void DisplayAllUsersQueries() {

        Query sortPostsInDescendingOrder = postsReference.orderByChild("counter");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        //new posts at top and old to bottom
        postList.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        final FirebaseRecyclerOptions<Posts> options =
                new FirebaseRecyclerOptions.Builder<Posts>()
                        .setQuery(sortPostsInDescendingOrder, Posts.class)
                        .build();

        FirebaseRecyclerAdapter<Posts, PostsViewHolder> fireBaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PostsViewHolder viewHolder, final int position, @NonNull Posts posts) {

                        posts = getItem(position);
                        final String PostKey = getRef(position).getKey();

                        final String userID = posts.getUid();

                        Picasso.get().load(posts.getProfileImage()).placeholder(R.drawable.profile_image_placeholder).into(viewHolder.profileImg);
                        viewHolder.usersName.setText(posts.getFullName());
                        viewHolder.postTime.setText(posts.getTime());
                        viewHolder.postDate.setText(posts.getDate());
                        viewHolder.postDescription.setText(posts.getDescription());


                        //view the specific users profile
                        viewHolder.profileImg.setOnClickListener(v -> {

                            Intent profileIntent = new Intent(MainActivity.this, OtherParentsProfileActivity.class);
                            profileIntent.putExtra("visit_user_id", userID);
                            startActivity(profileIntent);
                        });

                        //view the specific users profile
                        viewHolder.usersName.setOnClickListener(v -> {
                            Intent profileIntent = new Intent(MainActivity.this, OtherParentsProfileActivity.class);
                            profileIntent.putExtra("visit_user_id", userID);
                            startActivity(profileIntent);
                        });

                        //send post key to click post activity
                        viewHolder.itemView.setOnClickListener(v -> {
                            Intent clickPostIntent = new Intent(MainActivity.this, ClickPostActivity.class);
                            clickPostIntent.putExtra("PostKey", PostKey);
                            startActivity(clickPostIntent);
                        });

                        //open comments activity
                        viewHolder.commentOnPost.setOnClickListener(v -> {
                            Intent commentsIntent = new Intent(MainActivity.this, CommentsActivity.class);
                            commentsIntent.putExtra("PostKey", PostKey);
                            startActivity(commentsIntent);
                        });
                        viewHolder.postResponses.setOnClickListener(v -> {
                            Intent commentsIntent = new Intent(MainActivity.this, CommentsActivity.class);
                            commentsIntent.putExtra("PostKey", PostKey);
                            startActivity(commentsIntent);
                        });

                        //for counting number of comments
                        viewHolder.setCommentsCounterStatus(PostKey);
                    }

                    @NonNull
                    @Override
                    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.all_post_items_layout, parent, false);
                        PostsViewHolder viewHolder = new PostsViewHolder(view);
                        return viewHolder;
                    }
                };

        postList.setLayoutManager(linearLayoutManager);
        postList.setAdapter(fireBaseRecyclerAdapter);
        fireBaseRecyclerAdapter.startListening();

    }

    //creating a static class for displaying  queries from all users
    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        TextView usersName;
        CircleImageView profileImg;
        TextView postTime;
        TextView postDate;
        TextView postDescription;

        //for comments
        TextView commentOnPost;
        TextView postResponses;

        //variable for number of comments
        int countComments;
        DatabaseReference commentsRef;


        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);

            usersName = itemView.findViewById(R.id.post_user_name);
            profileImg = itemView.findViewById(R.id.post_profile_image);
            postTime = itemView.findViewById(R.id.post_time);
            postDate = itemView.findViewById(R.id.post_date);
            postDescription = itemView.findViewById(R.id.post_query);

            commentOnPost = itemView.findViewById(R.id.text_view_comment);
            postResponses = itemView.findViewById(R.id.tex_view_number_of_responses);

            commentsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        }


        //counting number of comments
        public void setCommentsCounterStatus(final String PostKey){
            commentsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(PostKey).hasChild("Comments")){
                        countComments = (int) dataSnapshot.child(PostKey).child("Comments").getChildrenCount();
                        postResponses.setText(Integer.toString(countComments) + " Responses");
                    }else {
                      //  countComments = (int) dataSnapshot.child(PostKey).getChildrenCount();
                      //  postResponses.setText(Integer.toString(countComments));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    //check user authentication
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //check for current user
        if (currentUser == null) {
            SendUserToLoginActivity();
        } else {
            // if user has edited and provided profile details
            CheckUserExistence();
        }

    }

    // when user selects navigation drawer items
    private void UserMenuSelector(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                SendUserToMainActivity();
                break;
            case R.id.nav_stories:
                Snackbar snackBar2 = Snackbar.make(findViewById(android.R.id.content), "STORIES FROM PARENTS", Snackbar.LENGTH_SHORT);
                snackBar2.show();
                break;
            case R.id.nav_chats:
                // SendUserToChatActivity();
                Snackbar snackBar3 = Snackbar.make(findViewById(android.R.id.content), "COMING SOON YOUR CHATS", Snackbar.LENGTH_SHORT);
                snackBar3.show();
                break;

            case R.id.nav_search_other_parents_name:
                SendUserToSearchOtherParentsActivity();
                break;

            case R.id.nav_profile:
                SendUserToProfileActivity();
                break;
            case R.id.nav_growthAnalysis:
                Snackbar snackBar4 = Snackbar.make(findViewById(android.R.id.content), "COMING SOON !!!", Snackbar.LENGTH_SHORT);
                snackBar4.show();
                break;
            case R.id.nav_reminders:
                SendUserToAlarmRemindersActivity();

                break;
            case R.id.nav_baby_products:
                Snackbar snackBar6 = Snackbar.make(findViewById(android.R.id.content), "Rate baby products", Snackbar.LENGTH_SHORT);
                snackBar6.show();
                break;
            case R.id.nav_feedback:
                SendUserToFeedbackActivity();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
        }
    }


    // There are 2 signatures and only `onPostCreate(Bundle state) shows the hamburger icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    // Pass any configuration change to the drawer toggles
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    //navigation drawer toggle
    private ActionBarDrawerToggle setupDrawerToggle() {
        // for Hamburger icon
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    // The action bar home/up action should open or close the drawer.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void CheckUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();

        //create reference to fireBase db
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check if user has not completed filling up profile
                if (!dataSnapshot.hasChild(current_user_id)) {
                    SendUserToEditProfileActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //opening the login activity
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    //opens edit profile activity
    private void SendUserToEditProfileActivity() {
        Intent profileIntent = new Intent(MainActivity.this, EditProfileActivity.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
        finish();
    }

    //opens edit profile activity
    private void SendUserToProfileActivity() {
        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
        finish();
    }

    //opens activity for users to write their questions
    private void SendUserToWriteQueryActivity() {
        Intent writeQueryIntent = new Intent(MainActivity.this, WriteQueryActivity.class);
        startActivity(writeQueryIntent);
    }

    //open main activity
    private void SendUserToMainActivity() {
        Intent mainActivityIntent = new Intent(MainActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }
    //opens the Feedback activity
    private void SendUserToFeedbackActivity() {
        Intent feedbackActivityIntent = new Intent(MainActivity.this, FeedbackActivity.class);
        finish();
        startActivity(feedbackActivityIntent);
    }

    //opens search other parents activity
    private void SendUserToSearchOtherParentsActivity() {

        Intent searchOtherParentsActivityIntent = new Intent(MainActivity.this, SearchOtherParentsActivity.class);
        finish();
        startActivity(searchOtherParentsActivityIntent);
    }

    private void SendUserToAlarmRemindersActivity() {

        Intent remindersActivityIntent = new Intent(MainActivity.this, RemindersActivity.class);
        finish();
        startActivity(remindersActivityIntent);
    }

}
