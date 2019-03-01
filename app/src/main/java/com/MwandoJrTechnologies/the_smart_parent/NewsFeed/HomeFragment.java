package com.MwandoJrTechnologies.the_smart_parent.NewsFeed;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.MwandoJrTechnologies.the_smart_parent.ConnectionChecker;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<ListItem> listItems;

    private FloatingActionButton fab;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;
    private ProgressBar newsFeedProgressBar;

    private SwipeRefreshLayout swipeRefreshLayout;
    private int position = 0;

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

        newsFeedProgressBar = rootView.findViewById(R.id.news_feed_progress_bar);
        listItems = new ArrayList<>();


        //inflate swipe layout
        swipeRefreshLayout = rootView.findViewById(R.id.swipeToRefresh);


        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Posts");
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ListItem listItem = snapshot.getValue(ListItem.class);

                    listItems.add(position, listItem);

                }
                //gets the recycle view
                adapter = new RecyclerViewAdapter(listItems, getContext());

                //maps the recycle view to adapter
                recyclerView.setAdapter(adapter);
                newsFeedProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), "Error" + databaseError, Toast.LENGTH_LONG).show();
            }
        });

        //checking internet state
        if (ConnectionChecker.isConnectedToNetwork(getContext())) {
            Toast.makeText(getActivity(), "Internet Connection", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "NETWORK ERROR! Please check your Internet connection", Toast.LENGTH_LONG).show();
            newsFeedProgressBar.setVisibility(View.GONE);
        }

        //refresh on swipe
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        return rootView;
    }

    @Override
    public void onClick(View view) {

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null)
            startActivity(new Intent(getActivity(), WriteQueryActivity.class));
        else {
            Toast.makeText(getActivity(), "Please login", Toast.LENGTH_SHORT).show();
            Snackbar.make(view, "Please login", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }
    }
}
