package com.mwandojrtechnologies.the_smart_parent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnRegister;

    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;

    // Make sure to be using android.support.v7.app.ActionBarDrawerToggle version.
    // The android.support.v4.app.ActionBarDrawerToggle has been deprecated.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        btnRegister = (Button) findViewById(R.id.btnRegister);
         btnRegister.setOnClickListener(this);
    }
    @Override
    public  void onClick(View v){
        switch (v.getId()){
            case R.id.btnRegister:
                startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
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
                break;
            case R.id.nav_register_fragment:
                Toast.makeText(this, "Please register", Toast.LENGTH_SHORT).show();
                fragmentClass = RegisterFragment.class;
                break;
            case R.id.nav_growth_fragment:
                Toast.makeText(this, "Monitor the growth of your baby", Toast.LENGTH_SHORT).show();
                fragmentClass = GrowthAnalysisFragment.class;
                break;
            case R.id.nav_reminders_fragment:
                Toast.makeText(this, "Remind me to", Toast.LENGTH_SHORT).show();
                fragmentClass = RemindersFragment.class;
                break;
            case R.id.nav_baby_products_fragment:
                Toast.makeText(this, "Rate baby products", Toast.LENGTH_SHORT).show();
                fragmentClass = BabyProductsFragment.class;
                break;
            case R.id.nav_share_fragment:
                Toast.makeText(this, "Please share app", Toast.LENGTH_SHORT).show();
                fragmentClass = ShareFragment.class;
                break;
            case R.id.nav_feedback_fragment:
                Toast.makeText(this, "please give feedback", Toast.LENGTH_SHORT).show();
                fragmentClass = FeedbackFragment.class;
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
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }



}


