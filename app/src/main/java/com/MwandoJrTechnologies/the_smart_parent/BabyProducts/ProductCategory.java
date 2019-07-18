package com.MwandoJrTechnologies.the_smart_parent.BabyProducts;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ProductCategory extends AppCompatActivity {

    String currentUserID;
    private FirebaseAuth mAuth;
    private FloatingActionButton addProductsFAB;

    private CardView babyDiapersCard;
    private CardView bathingAndSkinCareCard;
    private CardView babyFoodCard;
    private CardView babyHealthCard;
    private CardView babySafetyCard;
    private CardView babyToysCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Baby Product Categories");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        addProductsFAB = findViewById(R.id.fab_to_add_products_activity);

            //hide fab while admin is being developed
        if (!currentUserID.equals("K7Ng2Q3dXiQIGo56hjfsvkAvFgB2")) {
            addProductsFAB.hide();
        }
        addProductsFAB.setOnClickListener(v -> SendUserToAddProductActivity());


        babyDiapersCard = findViewById(R.id.category_diapers);
        bathingAndSkinCareCard = findViewById(R.id.category_bathing_and_skin_care);
        babyFoodCard = findViewById(R.id.category_baby_food);
        babyHealthCard = findViewById(R.id.category_baby_health);
        babySafetyCard = findViewById(R.id.category_baby_safety);
        babyToysCard = findViewById(R.id.category_baby_toys);

        babyDiapersCard.setOnClickListener(v -> SendUserToViewProductsActivity_diapers());
        bathingAndSkinCareCard.setOnClickListener(v -> SendUserToViewProductsActivity_bathing());
        babyFoodCard.setOnClickListener(v -> SendUserToViewProductsActivity_food());
        babyHealthCard.setOnClickListener(v -> SendUserToViewProductsActivity_health());
        babySafetyCard.setOnClickListener(v -> SendUserToViewProductsActivity_safety());
        babyToysCard.setOnClickListener(v -> SendUserToViewProductsActivity_toys());

    }

    private void SendUserToViewProductsActivity_diapers() {
        Intent viewDiapersIntent = new Intent(ProductCategory.this, ViewProductsActivity.class);
        String diapers = "diapers";
        viewDiapersIntent.putExtra("product_category", diapers);
        startActivity(viewDiapersIntent);
    }
    private void SendUserToViewProductsActivity_bathing() {
        Intent viewBathingAndSkinCareIntent = new
                Intent(ProductCategory.this, ViewProductsActivity.class);
        String bathing = "bathingAndSkinCare";
        viewBathingAndSkinCareIntent.putExtra("product_category", bathing);
        startActivity(viewBathingAndSkinCareIntent);
    }
    private void SendUserToViewProductsActivity_food() {
        Intent viewFoodIntent = new
                Intent(ProductCategory.this, ViewProductsActivity.class);
        String food = "Food";
        viewFoodIntent.putExtra("product_category", food);
        startActivity(viewFoodIntent);
    }
    private void SendUserToViewProductsActivity_health() {
        Intent viewHealthIntent = new
                Intent(ProductCategory.this, ViewProductsActivity.class);
        String health = "health";
        viewHealthIntent.putExtra("product_category", health);
        startActivity(viewHealthIntent);
    }
    private void SendUserToViewProductsActivity_safety() {
        Intent viewSafetyIntent = new
                Intent(ProductCategory.this, ViewProductsActivity.class);
        String safety = "safety";
        viewSafetyIntent.putExtra("product_category", safety);
        startActivity(viewSafetyIntent);
    }
    private void SendUserToViewProductsActivity_toys() {
        Intent viewToysIntent = new
                Intent(ProductCategory.this, ViewProductsActivity.class);
        String toys = "toys";
        viewToysIntent.putExtra("product_category", toys);
        startActivity(viewToysIntent);
    }

    private void SendUserToAddProductActivity() {
        Intent addProductIntent = new
                Intent(ProductCategory.this, AddProductsActivity.class);
        startActivity(addProductIntent);
    }

    @Override
    public void onBackPressed() {
        SendUserToMainActivity();
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

    private void SendUserToMainActivity() {
        Intent mainActivityIntent = new Intent(ProductCategory.this, MainActivity.class);
        startActivity(mainActivityIntent);
    }
}
