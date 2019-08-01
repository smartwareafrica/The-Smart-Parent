package com.MwandoJrTechnologies.the_smart_parent.BabyProducts;

/**
 * Activity showing various product categories and how they  can be rated
 * <p>
 * Each category opens the specific products in the category for rating
 */

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;

import java.util.Objects;

public class RateProductsCategoryActivity extends AppCompatActivity {

    private CardView babyDiapersCard;
    private CardView bathingAndSkinCareCard;
    private CardView babyFoodCard;
    private CardView babyHealthCard;
    private CardView babySafetyCard;
    private CardView babyToysCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_products_category);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Rate Products");

        babyDiapersCard = findViewById(R.id.rate_category_diapers);
        bathingAndSkinCareCard = findViewById(R.id.rate_category_bathing_and_skin_care);
        babyFoodCard = findViewById(R.id.rate_category_baby_food);
        babyHealthCard = findViewById(R.id.rate_category_baby_health);
        babySafetyCard = findViewById(R.id.rate_category_baby_safety);
        babyToysCard = findViewById(R.id.rate_category_baby_toys);


        babyDiapersCard.setOnClickListener(v -> SendUserToBabyDiapers());
        bathingAndSkinCareCard.setOnClickListener(v -> SendUserToBathingAndSkinCareCard());
        babyFoodCard.setOnClickListener(v -> SendUserToBabyFood());
        babyHealthCard.setOnClickListener(v -> SendUserToBabyHealth());
        babySafetyCard.setOnClickListener(v -> SendUserToBabySafety());
        babyToysCard.setOnClickListener(v -> SendUserBabyToys());
    }

    private void SendUserToBabyDiapers() {
        Intent rateDiapersIntent = new
                Intent(RateProductsCategoryActivity.this,
                RateBabyProductsActivity.class);
        String diapers = "diapers";
        rateDiapersIntent.putExtra("product_category", diapers);
        startActivity(rateDiapersIntent);
    }

    private void SendUserToBathingAndSkinCareCard() {
        Intent rateBathingAndSkinCareIntent = new
                Intent(RateProductsCategoryActivity.this,
                RateBabyProductsActivity.class);
        String bathing = "bathingAndSkinCare";
        rateBathingAndSkinCareIntent.putExtra("product_category", bathing);
        startActivity(rateBathingAndSkinCareIntent);
    }

    private void SendUserToBabyFood() {
        Intent rateFoodIntent = new
                Intent(RateProductsCategoryActivity.this,
                RateBabyProductsActivity.class);
        String food = "food";
        rateFoodIntent.putExtra("product_category", food);
        startActivity(rateFoodIntent);
    }

    private void SendUserToBabyHealth() {
        Intent rateHealthIntent = new
                Intent(RateProductsCategoryActivity.this,
                RateBabyProductsActivity.class);
        String health = "health";
        rateHealthIntent.putExtra("product_category", health);
        startActivity(rateHealthIntent);
    }

    private void SendUserToBabySafety() {
        Intent rateSafetyIntent = new
                Intent(RateProductsCategoryActivity.this,
                RateBabyProductsActivity.class);
        String safety = "safety";
        rateSafetyIntent.putExtra("product_category", safety);
        startActivity(rateSafetyIntent);
    }

    private void SendUserBabyToys() {
        Intent rateToysIntent = new
                Intent(RateProductsCategoryActivity.this,
                RateBabyProductsActivity.class);
        String toys = "toys";
        rateToysIntent.putExtra("product_category", toys);
        startActivity(rateToysIntent);
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
        Intent mainActivityIntent = new
                Intent(RateProductsCategoryActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
    }
}
