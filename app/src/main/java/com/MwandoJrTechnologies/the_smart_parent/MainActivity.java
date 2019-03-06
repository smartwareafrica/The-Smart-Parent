package com.MwandoJrTechnologies.the_smart_parent;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.WriteQueryActivity;
import com.MwandoJrTechnologies.the_smart_parent.Profile.LoginActivity;
import com.MwandoJrTechnologies.the_smart_parent.Profile.ProfileActivity;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {

    //initializing
    private DrawerLayout mDrawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;

    private CircleImageView navProfileImage;
    private TextView navProfileName;
    private ImageButton addNewPostButton;


    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        //inflate
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //navigation drawer toggle
        drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        // Find our navigation drawer view
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View navView = navigationView.getHeaderView(0);

        navProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        navProfileName = (TextView) navView.findViewById(R.id.nav_user_full_name);
        //display current logged in user details only
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    //check for the profile image and name
                    if (dataSnapshot.hasChild("fullName")) {
                        //only display name if it exists
                        String fullName = dataSnapshot.child("fullName").getValue().toString();
                        //code to display
                        navProfileName.setText(fullName);
                    }
                    if (dataSnapshot.hasChild("profileImage")) {
                        //display only if there is an image
                        String image = dataSnapshot.child("profileImage").getValue().toString();
                        //code to display
                        Picasso.get()
                                .load(image)
                                .placeholder(R.drawable.profile_image_placeholder)
                                .into(navProfileImage);
                    }else {
                        Toast.makeText(getApplicationContext(), "You need to update your profile", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                UserMenuSelector(menuItem);

                return false;
            }
        });

        addNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToWriteQueryActivity();
            }
        });

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
                // toolbar.setTitle("Home");
                break;
            case R.id.nav_stories:
                //  toolbar.setTitle("Stories");
                break;
            case R.id.nav_chats:
                // toolbar.setTitle("Chats");
                break;
            case R.id.nav_profile:
                SendUserToProfileActivity();
                //toolbar.setTitle("Profile");
                break;
            case R.id.nav_growthAnalysis:
                // toolbar.setTitle("Monitor");
                break;
            case R.id.nav_reminders:
                toolbar.setTitle("Reminders");
                Toast.makeText(getApplicationContext(), "Reminders", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_baby_products:
                toolbar.setTitle("Rate");
                break;
            case R.id.nav_share:
                toolbar.setTitle("Share");
                break;
            case R.id.nav_feedback:
                toolbar.setTitle("Feedback");
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
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
                    SendUserToProfileActivity();
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

    //opens profile activity
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
}