package com.mwandojrtechnologies.the_smart_parent.Newsfeed;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mwandojrtechnologies.the_smart_parent.MainActivity;
import com.mwandojrtechnologies.the_smart_parent.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<ListItem> listItems;

    private FloatingActionButton fab;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.homeRecyclerView);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fab.setOnClickListener(this);

        listItems = new ArrayList<>();


        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Posts");
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ListItem listItem = snapshot.getValue(ListItem.class);

                    listItems.add(listItem);

                }

                adapter = new RecyclerViewAdapter(listItems, getContext());
                recyclerView.setAdapter(adapter);

            /**    ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Loading...");
                dialog.show();

                dialog.dismiss();
             */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), "Error" + databaseError, Toast.LENGTH_LONG).show();
            }
        });


        return rootView;
    }


    @Override
    public void onClick(View view) {

        firebaseAuth = FirebaseAuth.getInstance();

        Snackbar.make(view, "Please login", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        if (firebaseAuth.getCurrentUser() != null)
            startActivity(new Intent(getActivity(), WriteQueryActivity.class));
        else {
            Toast.makeText(getActivity(), "Please login", Toast.LENGTH_SHORT).show();

        }
    }
}
