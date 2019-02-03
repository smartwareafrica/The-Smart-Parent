package com.mwandojrtechnologies.the_smart_parent;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // for Hamburger icon
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
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

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                // for top navigation

                        // Create a new fragment and specify the fragment to show based on nav item clicked
                        Fragment fragment = null;
                        Class fragmentClass;
                        switch (menuItem.getItemId()) {
                            case R.id.action_home:
                                Toast.makeText(getApplicationContext(), "home", Toast.LENGTH_SHORT).show();
                                toolbar.setTitle("HOME");

                                return true;
                            case R.id.action_stories:
                                Toast.makeText(getApplicationContext(), "Stories", Toast.LENGTH_SHORT).show();
                                fragmentClass = StoriesFragment.class;
                                toolbar.setTitle("Stories");

                                return true;
                            case R.id.action_chats:
                                Toast.makeText(MainActivity.this, "My chats", Toast.LENGTH_SHORT).show();
                                fragmentClass = ChatsFragment.class;
                                toolbar.setTitle("Chats");

                                return true;
                            case R.id.action_profile:
                                Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
                                fragmentClass = ProfileFragment.class;
                                toolbar.setTitle("Profile");

                                return true;


                            default:
                                fragmentClass = MainActivity.class;
                        }

                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_login_fragment:
                Toast.makeText(this, "Please Login", Toast.LENGTH_SHORT).show();
                fragmentClass = LoginFragment.class;
                toolbar.setTitle("Log In");
                break;
            case R.id.nav_register_fragment:
                Toast.makeText(this, "Please register", Toast.LENGTH_SHORT).show();
                fragmentClass = RegisterFragment.class;
                toolbar.setTitle("Register");

                break;
            case R.id.nav_growth_fragment:
                Toast.makeText(this, "Monitor the growth of your baby", Toast.LENGTH_SHORT).show();
                fragmentClass = GrowthAnalysisFragment.class;
                toolbar.setTitle("Monitor");

                break;
            case R.id.nav_reminders_fragment:
                Toast.makeText(this, "Remind me to", Toast.LENGTH_SHORT).show();
                fragmentClass = RemindersFragment.class;
                toolbar.setTitle("Reminders");

                break;
            case R.id.nav_baby_products_fragment:
                Toast.makeText(this, "Rate baby products", Toast.LENGTH_SHORT).show();
                fragmentClass = BabyProductsFragment.class;
                toolbar.setTitle("Rate");

                break;
            case R.id.nav_share_fragment:
                Toast.makeText(this, "Please share app", Toast.LENGTH_SHORT).show();
                fragmentClass = ShareFragment.class;
                toolbar.setTitle("Share");
                break;
            case R.id.nav_feedback_fragment:
                Toast.makeText(this, "Please give feedback", Toast.LENGTH_SHORT).show();
                fragmentClass = FeedbackFragment.class;
                toolbar.setTitle("Feedback");

                break;


            default:
                fragmentClass = LoginFragment.class;
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
}




