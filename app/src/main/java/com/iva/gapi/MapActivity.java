package com.iva.gapi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity  {
    double lat,lon;
    FusedLocationProviderClient fused;
    SupportMapFragment supportMapFragment;
    Location lastLocation;
    GoogleMap gmap;
    Boolean isPermissionGranted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
         supportMapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.f1);

        fused = LocationServices.getFusedLocationProviderClient(this);


    }

    void getLoc()
    {
        if(ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MapActivity.this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else
        {
            Toast.makeText(this, ""+"Permission Granted", Toast.LENGTH_SHORT).show();
            isPermissionGranted= true;
            fused.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null)
                        lastLocation=location;
                    lat=lastLocation.getLatitude();
                    lon=lastLocation.getLongitude();


                    initMap();

                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case 1:
                if(grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    getLoc();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //PlaceAutocompleteFragment placeAutocompleteFragment=


    void initMap()
    {
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {

                gmap = googleMap;
                if(isPermissionGranted)
                {
                    LatLng myLatLon=new LatLng(lat,lon);
                    gmap.moveCamera(CameraUpdateFactory.newLatLng(myLatLon));
                    gmap.setMyLocationEnabled(true);
                    MarkerOptions markerOptions=new MarkerOptions();
                    markerOptions.position(myLatLon);
                    markerOptions.title("You are here");
                    gmap.addMarker(markerOptions);
                   // gmap.getUiSettings()
                }
            }
        });

    }
}
