package com.MwandoJrTechnologies.the_smart_parent.BabyProducts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.MwandoJrTechnologies.the_smart_parent.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
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
    private DatabaseReference usersRef;
    private DatabaseReference reviewsReference;
    private FirebaseAuth mAuth;

    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_product);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Reviews");

        //receive Product Key from previous activity and use it to fetch from db
        productKey = Objects
                .requireNonNull
                        (Objects.requireNonNull(getIntent().getExtras()).get("product_key"))
                .toString();
        productCategory = Objects
                .requireNonNull
                        (Objects.requireNonNull(getIntent().getExtras()).get("product_category"))
                .toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        reviewsReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Products")
                .child(productKey)
                .child("Reviews");
        productReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Products")
                .child(productKey);

        singleProductImage = findViewById(R.id.individual_product_image_view);
        singleProductName = findViewById(R.id.individual_product_name);
        singleProductManufacturer = findViewById(R.id.individual_product_manufacturer);
        singleProductDescription = findViewById(R.id.individual_product_description);
        singleProductRatedRatingBar = findViewById(R.id.individual_product_rated_rating_bar);
        singleProductComment = findViewById(R.id.individual_product_comment_input);
        singleProductSendComment = findViewById(R.id.individual_product_comment_send);
        singleProductReviews = findViewById(R.id.individual_product_reviews_list);
        singleProductReviews.setHasFixedSize(true);

        singleProductSendComment.setOnClickListener(v -> usersRef.child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            String fullName = dataSnapshot.child("fullName").getValue().toString();

                            ValidateReview(fullName);

                            singleProductComment.setText("");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }));

        productReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("productImage")) {
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

    @Override
    protected void onStart() {
        super.onStart();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        singleProductReviews.setLayoutManager(linearLayoutManager);

        final FirebaseRecyclerOptions<Reviews> options = new
                FirebaseRecyclerOptions.Builder<Reviews>()
                .setQuery(reviewsReference, Reviews.class)
                .build();

        FirebaseRecyclerAdapter<Reviews, ReviewsViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<Reviews, ReviewsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder
                            (@NonNull ReviewsViewHolder reviewsViewHolder,
                             int i,
                             @NonNull Reviews reviews) {

                        reviews = getItem(i);

                        reviewsViewHolder.myReview.setText(reviews.getReview());
                        reviewsViewHolder.myDate.setText(reviews.getDate());
                        reviewsViewHolder.myTime.setText(reviews.getTime());

                    }

                    @NonNull
                    @Override
                    public ReviewsViewHolder onCreateViewHolder
                            (@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.all_product_review_layout, parent,false);

                        ReviewsViewHolder reviewsViewHolder = new ReviewsViewHolder(view);


                        return reviewsViewHolder;
                    }
                };

        singleProductReviews.setLayoutManager(linearLayoutManager);
        singleProductReviews.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class ReviewsViewHolder extends RecyclerView.ViewHolder {


        TextView myReview;
        TextView myDate;
        TextView myTime;

        public ReviewsViewHolder(@NonNull View itemView) {
            super(itemView);

            myReview = itemView.findViewById(R.id.review_text_view);
            myDate = itemView.findViewById(R.id.review_date);
            myTime = itemView.findViewById(R.id.review_time);
        }
    }

    private void ValidateReview(String fullName) {
        String reviewText = singleProductComment.getText().toString();

        //setting current date and time to generate random keys for the users images posted
        //setting current date
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(callForDate.getTime());

        //setting current date
        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        final String saveCurrentTime = currentTime.format(callForTime.getTime());

        final String randomKey = currentUserID + saveCurrentDate + saveCurrentTime;

        if (TextUtils.isEmpty(reviewText)) {
            Snackbar snackBar = Snackbar
                    .make(findViewById(android.R.id.content),
                            "Please write text to respond...",
                            Snackbar.LENGTH_SHORT);
            snackBar.show();
        } else {
            HashMap<String, Object> reviewsMap = new HashMap<>();
            reviewsMap.put("uid", currentUserID);
            reviewsMap.put("review", reviewText);
            reviewsMap.put("fullName", fullName);
            reviewsMap.put("date", saveCurrentDate);
            reviewsMap.put("time", saveCurrentTime);
            reviewsReference.child(randomKey).updateChildren(reviewsMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Snackbar snackBar = Snackbar
                                    .make(findViewById(android.R.id.content),
                                            "Review Sent", Snackbar.LENGTH_SHORT);
                            snackBar.show();
                        } else {
                            Snackbar snackBar = Snackbar
                                    .make(findViewById(android.R.id.content),
                                            "Could not send Review, Please try again",
                                            Snackbar.LENGTH_SHORT);
                            snackBar.show();
                        }
                    });
        }
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
