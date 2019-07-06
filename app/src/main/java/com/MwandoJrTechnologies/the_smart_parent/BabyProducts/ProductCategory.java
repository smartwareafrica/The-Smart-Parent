package com.MwandoJrTechnologies.the_smart_parent.BabyProducts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import com.MwandoJrTechnologies.the_smart_parent.Chats.ChatActivity;
import com.MwandoJrTechnologies.the_smart_parent.Chats.SearchOtherParentsActivity;
import com.MwandoJrTechnologies.the_smart_parent.Profile.OtherParentsProfileActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ProductCategory extends AppCompatActivity {

    String currentUserID;
    private DatabaseReference allParentsReference;
    private FirebaseAuth mAuth;
    private FloatingActionButton addProductsFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Categories");

        allParentsReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        addProductsFAB = findViewById(R.id.fab_to_add_products_activity);

        addProductsFAB.setOnClickListener(v -> SendUserToAddProductActivity());

        if (!currentUserID.equals("K7Ng2Q3dXiQIGo56hjfsvkAvFgB2")) {
            addProductsFAB.hide();
        }
    }


    private void SendUserToAddProductActivity() {
        Intent addProductIntent = new Intent(ProductCategory.this, AddProductsActivity.class);
        startActivity(addProductIntent);
    }
}
