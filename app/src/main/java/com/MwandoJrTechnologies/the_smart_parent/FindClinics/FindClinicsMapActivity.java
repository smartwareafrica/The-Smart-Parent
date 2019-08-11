package com.MwandoJrTechnologies.the_smart_parent.FindClinics;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class FindClinicsMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_REQUEST_CODE = 101;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_clinics_map);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.maps_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Find Clinics");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkAccessToLocation();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(FindClinicsMapActivity.this,
                android
                        .Manifest
                        .permission
                        .ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(FindClinicsMapActivity.this,
                        android.Manifest.permission
                                .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(this,
                            new String[]{android
                                    .Manifest
                                    .permission
                                    .ACCESS_FINE_LOCATION},
                            LOCATION_REQUEST_CODE);
            return;
        }
        fetchLastLocation();

    }

    private void checkAccessToLocation() {
        //Checking if the user has granted location permission for this app
        if (ActivityCompat
                .checkSelfPermission(this,
                        android.Manifest
                                .permission
                                .ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this,
                        android.Manifest
                                .permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
    /*
    Requesting the Location permission
    1st Param - Activity
    2nd Param - String Array of permissions requested
    3rd Param -Unique Request code. Used to identify these set of requested permission
    */
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_REQUEST_CODE);
            return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    //Permission Granted
                    fetchLastLocation();
                } else
                    Toast.makeText(this, "Location Permission Denied",
                            Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        /*
        LatLng latLng = new
                LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        //MarkerOptions are used to create a new Marker
        // .You can specify location, title etc with MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are Here");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        //Adding the created the marker on the map
        googleMap.addMarker(markerOptions);
        */

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fetchLastLocation() {
        if (checkSelfPermission
                (Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED && checkSelfPermission
                (Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
                .PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                Toast.makeText(FindClinicsMapActivity.this,
                        currentLocation.getLatitude() + " " + currentLocation
                                .getLongitude(), Toast.LENGTH_SHORT).show();
                SupportMapFragment supportMapFragment = (SupportMapFragment)
                        getSupportFragmentManager().findFragmentById(R.id.map);
                supportMapFragment.getMapAsync(FindClinicsMapActivity.this);
            } else {
                Toast.makeText(FindClinicsMapActivity.this,
                        "No Location recorded", Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public void onBackPressed() {
        SendUserToMainActivity();
    }

    private void SendUserToMainActivity() {
        Intent mainActivityIntent = new
                Intent(FindClinicsMapActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }
}
