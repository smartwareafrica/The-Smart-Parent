package com.MwandoJrTechnologies.the_smart_parent;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.HomeFragment;
import com.MwandoJrTechnologies.the_smart_parent.Profile.LoginFragment;
import com.MwandoJrTechnologies.the_smart_parent.Profile.ProfileFragment;
import com.MwandoJrTechnologies.the_smart_parent.Profile.RegisterFragment;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    //bottom navigation variables
    protected BottomNavigationView navigationView;
    private FrameLayout mainFragmentScreen;
    //fragments
    private HomeFragment homeFragment;
    private StoriesFragment storiesFragment;
    private ChatsFragment chatsFragment;
    private ProfileFragment profileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_main);

        //inflate
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        // Find our drawer view

        drawerToggle = setupDrawerToggle();

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);


        // for views on bottom navigation
        mainFragmentScreen = (FrameLayout) findViewById(R.id.fragment_container);
        navigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        homeFragment = new HomeFragment();
        storiesFragment = new StoriesFragment();
        chatsFragment = new ChatsFragment();
        profileFragment = new ProfileFragment();
        setFragment(homeFragment);


        // For the bottom navigation
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // Create a new fragment and specify the fragment to show based on nav item clicked
                switch (item.getItemId()) {
                    case R.id.bottom_nav_home:
                        setFragment(homeFragment);
                        //Toast.makeText(getApplicationContext(), "home", Toast.LENGTH_SHORT).show();
                        toolbar.setTitle("Home");
                        return true;

                    case R.id.bottom_nav_stories:
                        setFragment(storiesFragment);
                        Toast.makeText(getApplicationContext(), "Stories", Toast.LENGTH_SHORT).show();
                        toolbar.setTitle("Stories");
                        return true;

                    case R.id.bottom_nav_chats:
                        setFragment(chatsFragment);
                        Toast.makeText(MainActivity.this, "My chats", Toast.LENGTH_SHORT).show();
                        toolbar.setTitle("Chats");
                        return true;

                    case R.id.bottom_nav_profile:
                        setFragment(profileFragment);
                        // Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
                        toolbar.setTitle("Profile");
                        return true;


                    default:
                        return false;
                }

            }
        });

    }

    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
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

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();


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

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                //for navigation drawer
                // Create a new fragment and specify the fragment to show based on nav item clicked
                Fragment fragment = null;
                Class fragmentClass;
                switch (menuItem.getItemId()) {
                    case R.id.nav_login_fragment:
                        //Toast.makeText(getApplicationContext(), "Please login", Toast.LENGTH_SHORT).show();
                        fragmentClass = LoginFragment.class;
                        toolbar.setTitle("Log In");
                        break;
                    case R.id.nav_register_fragment:
                        // Toast.makeText(getApplicationContext(), "Please register", Toast.LENGTH_SHORT).show();
                        fragmentClass = RegisterFragment.class;
                        toolbar.setTitle("Register");

                        break;
                    case R.id.nav_growth_fragment:
                        Toast.makeText(getApplicationContext(), "Monitor the growth of your baby", Toast.LENGTH_SHORT).show();
                        fragmentClass = GrowthAnalysisFragment.class;
                        toolbar.setTitle("Monitor");

                        break;
                    case R.id.nav_reminders_fragment:
                        Toast.makeText(getApplicationContext(), "Reminders", Toast.LENGTH_SHORT).show();
                        fragmentClass = RemindersFragment.class;
                        toolbar.setTitle("Reminders");

                        break;
                    case R.id.nav_baby_products_fragment:
                        Toast.makeText(getApplicationContext(), "Rate baby products", Toast.LENGTH_SHORT).show();
                        fragmentClass = BabyProductsFragment.class;
                        toolbar.setTitle("Rate");

                        break;
                    case R.id.nav_share_fragment:
                        Toast.makeText(getApplicationContext(), "Please share THE SMART PARENT", Toast.LENGTH_SHORT).show();
                        fragmentClass = ShareFragment.class;
                        toolbar.setTitle("Share");
                        break;
                    case R.id.nav_feedback_fragment:
                        Toast.makeText(getApplicationContext(), "Please give feedback", Toast.LENGTH_SHORT).show();
                        fragmentClass = FeedbackFragment.class;
                        toolbar.setTitle("Feedback");

                        break;


                    default:
                        fragmentClass = HomeFragment.class;
                }

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

                // Highlight the selected item has been done by NavigationView
                menuItem.setChecked(true);
                // Set action bar title
                setTitle(menuItem.getTitle());
                // Close the navigation drawer
                mDrawer.closeDrawers();


                return false;
            }

        });
    }

}