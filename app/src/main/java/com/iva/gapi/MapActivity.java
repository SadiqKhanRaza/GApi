package com.iva.gapi;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlacePicker.IntentBuilder;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnPoiClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {

    SupportMapFragment supportMapFragment;
    GoogleMap gmap;

    FusedLocationProviderClient fusedLocationProviderClient;
    boolean isPermisssionGranted = false;

    Location lastlocation;
    double latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.f1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        checkLocationPermission();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.f2);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                gmap.clear();

                LatLng newlatlng = place.getLatLng();

                gmap.moveCamera(CameraUpdateFactory.newLatLng(newlatlng));

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(newlatlng);
                markerOptions.title(""+place.getName());
                gmap.addMarker(markerOptions);

            }

            @Override
            public void onError(Status status) {
            }
        });

    }


    public void initPlacePicker(View v)
    {
        PlacePicker.IntentBuilder builder = new IntentBuilder();
        try {
            startActivityForResult(builder.build(this),1);

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                Place place = PlacePicker.getPlace(this,data);

                Toast.makeText(this, place.getAddress().getClass()+"", Toast.LENGTH_LONG).show();
                gmap.clear();
                LatLng newlatlng = place.getLatLng();

                gmap.moveCamera(CameraUpdateFactory.newLatLng(newlatlng));

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(newlatlng);
                markerOptions.title(""+place.getName());

                gmap.addMarker(markerOptions);
            }

        }
    }


    void initMap()
    {
        Toast.makeText(this, "Initialize Map", Toast.LENGTH_SHORT).show();

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gmap = googleMap;

                if(isPermisssionGranted)
                {
                    LatLng mylatlng = new LatLng(latitude,longitude);
                    Log.d("Tag","latitude "+latitude);
                    Log.d("Tag","longitude "+longitude);

                    gmap.moveCamera(CameraUpdateFactory.newLatLng(mylatlng));
                    gmap.setMyLocationEnabled(true);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(mylatlng);
                    markerOptions.title("You are here");

                    gmap.addMarker(markerOptions);

                }

            }
        });

    }


    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            isPermisssionGranted = true;
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    Log.d("Tag","in on sucess ");
                    if(location != null)
                    {
                        lastlocation = location;
                        latitude = lastlocation.getLatitude();
                        longitude = lastlocation.getLongitude();

                        initMap();
                    }
                    else
                        Log.d("Tag","Location in null ");
                }
            });
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{permission.ACCESS_FINE_LOCATION},2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 2:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                    checkLocationPermission();
                else
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal_map:
                gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                gmap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
}
