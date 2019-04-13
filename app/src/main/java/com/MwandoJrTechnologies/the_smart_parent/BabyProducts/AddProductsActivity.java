package com.MwandoJrTechnologies.the_smart_parent.BabyProducts;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.MwandoJrTechnologies.the_smart_parent.R;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddProductsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Add Product");
    }


    //activate back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            SendUserToViewProductsActivity();
        }
        return super.onOptionsItemSelected(item);
    }


    //open view products activity
    private void SendUserToViewProductsActivity() {
        Intent viewProductsActivityIntent = new Intent(AddProductsActivity.this, ViewProductsActivity.class);
        finish();
        startActivity(viewProductsActivityIntent);
    }

}