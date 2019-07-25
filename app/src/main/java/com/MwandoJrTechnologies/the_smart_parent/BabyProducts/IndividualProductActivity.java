package com.MwandoJrTechnologies.the_smart_parent.BabyProducts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class IndividualProductActivity extends AppCompatActivity {
    private String productCategory;
    private String productKey;

    private ImageView singleProductImage;
    private TextView singleProductName;
    private TextView singleProductManufacturer;
    private TextView singleProductDescription;
    private RatingBar singleProductRatedRatingBar;
    private RecyclerView singleProductReviews;
    private EditText singleProductComment;
    private ImageButton singleProductSendComment;

    private DatabaseReference productReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_product);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Firebase product name");

        //receive Product Key from previous activity and use it to fetch from db
        productKey = Objects
                .requireNonNull
                        (Objects.requireNonNull(getIntent().getExtras()).get("product_key"))
                .toString();
        productCategory = Objects
                .requireNonNull
                        (Objects.requireNonNull(getIntent().getExtras()).get("product_category"))
                .toString();

        productReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Products")
                .child(productKey);

        singleProductImage = findViewById(R.id.individual_product_image_view);
        singleProductName = findViewById(R.id.individual_product_name);
        singleProductManufacturer = findViewById(R.id.individual_product_manufacturer);
        singleProductDescription = findViewById(R.id.individual_product_description);
        singleProductRatedRatingBar =findViewById(R.id.individual_product_rated_rating_bar);
        singleProductReviews = findViewById(R.id.individual_product_reviews_list);
        singleProductComment = findViewById(R.id.individual_product_comment_input);
        singleProductSendComment = findViewById(R.id.individual_product_comment_send);

        productReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("productImage")){
                        String myProfileImage = Objects
                                .requireNonNull(dataSnapshot.child("productImage").getValue())
                                .toString();

                        String productName = dataSnapshot
                                .child("productName")
                                .getValue()
                                .toString();
                        String productManufactureCompany = dataSnapshot
                                .child("productManufactureCompany")
                                .getValue()
                                .toString();
                        String productDescription = dataSnapshot
                                .child("productDescription")
                                .getValue()
                                .toString();
                        float productRating = Float
                                .parseFloat(dataSnapshot.child("productRating")
                                        .getValue()
                                        .toString());

                        Picasso.get()
                                .load(myProfileImage)
                                .placeholder(R.drawable.ic_phonelink_lock_black_24dp)
                                .into(singleProductImage);

                        singleProductName.setText(productName);
                        singleProductManufacturer
                                .setText("Manufactured by: " + productManufactureCompany);
                        singleProductDescription.setText(productDescription);
                        singleProductRatedRatingBar.setRating(productRating);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //activate back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            SendUserToViewProducts();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        SendUserToViewProducts();
    }

    //open main activity
    private void SendUserToViewProducts() {
        Intent viewProductsIntent = new
                Intent(IndividualProductActivity.this, ViewProductsActivity.class);
        finish();
        viewProductsIntent.putExtra
                ("product_category", productCategory);
        startActivity(viewProductsIntent);
    }
}
