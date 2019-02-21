package com.MwandoJrTechnologies.the_smart_parent.NewsFeed;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.MwandoJrTechnologies.the_smart_parent.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.Profile.LoginFragment;
import com.MwandoJrTechnologies.the_smart_parent.R;

public class WriteQueryActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private EditText editTextWriteQuery;
    private Button buttonPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_query);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Write query");

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginFragment.class));
        }


        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        editTextWriteQuery = (EditText) findViewById(R.id.editTextWriteQuery);
        buttonPost = (Button) findViewById(R.id.buttonPost);


        buttonPost.setOnClickListener(this);
    }

    private void savePost() {
        String query = editTextWriteQuery.getText().toString().trim();
        if (query.isEmpty()) {
            editTextWriteQuery.setError("Please write your question");
            editTextWriteQuery.requestFocus();
            return;
        }

        //create user post in database
        ListItem createPost = new ListItem(query);

        String uploadComment = databaseReference.push().getKey();
        databaseReference.child(uploadComment).setValue(createPost);
    }


    @Override
    public void onClick(View v) {
        savePost();
        Toast.makeText(this, "Post written successfully", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));

    }

}
