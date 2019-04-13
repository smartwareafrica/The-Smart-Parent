package com.MwandoJrTechnologies.the_smart_parent.BabyProducts;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ViewProductsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("View Product rating");
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
        Intent mainActivityIntent = new Intent(ViewProductsActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }

}
