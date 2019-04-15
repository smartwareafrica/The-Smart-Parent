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

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewProductsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference productsReference;
    String currentUserID;

    private AppCompatButton goToRateProductsButton;
    private FloatingActionButton addProductsFAB;
    private RecyclerView allProductsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("View Product rating");

        productsReference = FirebaseDatabase.getInstance().getReference().child("Products");

        goToRateProductsButton = findViewById(R.id.button_go_to_rate_products);
        addProductsFAB = findViewById(R.id.fab_to_add_products_activity);
        allProductsRecyclerView = findViewById(R.id.all_baby_products_layout);

        goToRateProductsButton.setOnClickListener(v -> SendUserToRateBabyProductsActivity());
        addProductsFAB.setOnClickListener(v -> SendUserToAddProductsActivity());

        if (!currentUserID.equals("K7Ng2Q3dXiQIGo56hjfsvkAvFgB2") ){
            addProductsFAB.hide();
        }

        DisplayAllProductsLayouts();
    }

    private void DisplayAllProductsLayouts() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewProductsActivity.this);

        allProductsRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);


        final FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(productsReference, Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Products, ProductsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductsViewHolder productsViewHolder, int position, @NonNull Products products) {


                        productsReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                int ratingSum = 0;
                                float ratingTotal = 0;
                                float ratingAverage = 0;

                                for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){

                                    ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                                    ratingTotal++;
                                }
                                if (ratingTotal != 0){
                                    ratingAverage = ratingSum/ratingTotal;

                                    productsViewHolder.productRatedRatingBar.setRating(ratingAverage);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        Picasso.get()
                                .load(products.getProductImage())
                                .placeholder(R.drawable.project_logo)
                                .into(productsViewHolder.productRatingViewImage);

                        productsViewHolder.productRatingName.setText(products.getProductName());
                        productsViewHolder.productRatingManufacturer.setText(products.getProductManufactureCompany());
                        productsViewHolder.productRatingDescription.setText(products.getProductDescription());


                    }

                    @NonNull
                    @Override
                    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.all_baby_products_display_layout, parent, false);
                        ProductsViewHolder viewHolder = new ProductsViewHolder(view);

                        return viewHolder;
                    }
                };
        allProductsRecyclerView.setLayoutManager(linearLayoutManager);
        allProductsRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    private static class ProductsViewHolder extends RecyclerView.ViewHolder{

        private ImageView productRatingViewImage;
        private TextView productRatingName;
        private TextView productRatingManufacturer;
        private TextView productRatingDescription;
        private RatingBar productRatedRatingBar;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            productRatingViewImage = itemView.findViewById(R.id.view_product_rating_image);
            productRatingName = itemView.findViewById(R.id.view_product_rating_product_name);
            productRatingManufacturer = itemView.findViewById(R.id.view_product_rating_manufacturer);
            productRatingDescription = itemView.findViewById(R.id.view_product_rating_description);
            productRatedRatingBar = itemView.findViewById(R.id.view_product_rating_bar);
        }
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

    //open rate products activity
    private void SendUserToRateBabyProductsActivity() {
        Intent rateProductsActivityIntent = new Intent(ViewProductsActivity.this, RateBabyProductsActivity.class);
        finish();
        startActivity(rateProductsActivityIntent);
    }

    //open add products activity
    private void SendUserToAddProductsActivity() {
        Intent addProductsActivityIntent = new Intent(ViewProductsActivity.this, AddProductsActivity.class);
        finish();
        startActivity(addProductsActivityIntent);
    }

}
