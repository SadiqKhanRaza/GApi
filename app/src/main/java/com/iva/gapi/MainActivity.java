package com.iva.gapi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.security.Permission;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TextView tv1,tv2,tv3;
    Button b1,b2;
    FusedLocationProviderClient loc;
    Location lastLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1=findViewById(R.id.textView1);
        tv2=findViewById(R.id.textView2);
        tv3=findViewById(R.id.textView3);
        b1=findViewById(R.id.button);
        b2=findViewById(R.id.button2);

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });

        loc= LocationServices.getFusedLocationProviderClient(MainActivity.this);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                getLoc();

            tv1.setText(loc.getLastLocation().toString());
            }
        });
    }
    void getLoc()
    {
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        else
        {
            Toast.makeText(this, ""+"Permission Granted", Toast.LENGTH_SHORT).show();
            loc.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null)
                        lastLocation=location;
                    double lat=lastLocation.getLatitude();
                    double lon=lastLocation.getLongitude();
                    tv1.setText(""+lat);
                    tv2.setText(""+lon);
                    Geocoder geocoder= new Geocoder(MainActivity.this, Locale.getDefault());
                    try
                    {
                        List<Address> locationList=geocoder.getFromLocation(lat,lon,1);
                        if(locationList.size()>0)
                        {
                            Address address= locationList.get(0);
                            tv3.setText(""+address);
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
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
}
