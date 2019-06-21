package com.MwandoJrTechnologies.the_smart_parent.BabyProducts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.MwandoJrTechnologies.the_smart_parent.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RateBabyProductsActivity extends AppCompatActivity {

    private RecyclerView productsRecyclerView;
    private AppCompatButton goToViewProductRatingsButton;

    private DatabaseReference productsReference;
    private FirebaseAuth mAuth;
    String currentUserID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_baby_products);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Rate the products");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        productsReference = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRecyclerView = findViewById(R.id.rate_products_recycler_view);
        goToViewProductRatingsButton = findViewById(R.id.button_go_to_rate_products);


        goToViewProductRatingsButton.setOnClickListener(v -> SendUserToViewProductsActivity());

        DisplayAllProductsLayouts();
    }

    private void DisplayAllProductsLayouts() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RateBabyProductsActivity.this);

        productsRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);


        final FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(productsReference, Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Products, ProductsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductsViewHolder productsViewHolder, int position, @NonNull Products products) {

                        final String productID = getRef(position).getKey();


                        Picasso.get()
                                .load(products.getProductImage())
                                .placeholder(R.mipmap.project_logo)
                                .into(productsViewHolder.productRatingImage);

                        productsViewHolder.productRatingDisplayName.setText(products.getProductName());
                        productsViewHolder.productRatingManufacturerName.setText(products.getProductManufactureCompany());
                        productsViewHolder.productRatingDisplayDescription.setText(products.getProductDescription());

                        //perform rating
                        productsViewHolder.productRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {

                            productsReference.child(productID).child("rating").child(currentUserID).setValue(rating);


                            //assign user who rates it

                        });
                    }

                    @NonNull
                    @Override
                    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.all_baby_product_rating_layout, parent, false);
                        ProductsViewHolder viewHolder = new ProductsViewHolder(view);

                        return viewHolder;
                    }
                };

        productsRecyclerView.setLayoutManager(linearLayoutManager);
        productsRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    private static class ProductsViewHolder extends RecyclerView.ViewHolder{

        private ImageView productRatingImage;
        private TextView productRatingDisplayName;
        private TextView productRatingManufacturerName;
        private TextView productRatingDisplayDescription;
        private RatingBar productRatingBar;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            productRatingImage = itemView.findViewById(R.id.product_rate_image);
            productRatingDisplayName = itemView.findViewById(R.id.product_rate_name);
            productRatingDisplayDescription = itemView.findViewById(R.id.product_rate_description);
            productRatingManufacturerName = itemView.findViewById(R.id.product_rate_manufacturer);
            productRatingBar = itemView.findViewById(R.id.product_rating_bar);

        }
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
        Intent viewProductsActivityIntent = new Intent(RateBabyProductsActivity.this, ViewProductsActivity.class);
        finish();
        startActivity(viewProductsActivityIntent);
    }

}
