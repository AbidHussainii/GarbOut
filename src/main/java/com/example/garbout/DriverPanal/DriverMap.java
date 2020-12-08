package com.example.garbout.DriverPanal;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.garbout.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DriverMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    private LocationSettingsRequest.Builder builder;
    private static final int CHECK_REQUEST_CODE = 109;
    ArrayList<LatLng> arrayList = new ArrayList<LatLng>();
    LatLng chungiNo9 = new LatLng(30.2084639, 71.4696846);
    LatLng chungiNo6 = new LatLng(30.2072136, 71.3928869);
    LatLng vehariChock = new LatLng(30.1736589, 71.473944);
    LatLng BCGchock = new LatLng(30.1720563, 71.4572936);
    LatLng HamayunRoad = new LatLng(30.2049337, 71.4642307);
    ArrayList<String> title = new ArrayList<String>();
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        gpsEnabling();
        //-----adding items in array list

//        arrayList.add(new LatLng(30.1720563, 71.4572936));
        arrayList.add(chungiNo9);
        arrayList.add(chungiNo6);
        arrayList.add(vehariChock);
        arrayList.add(BCGchock);
        arrayList.add(HamayunRoad);
        //----- adding title markers
        title.add("chungiNo9");
        title.add("chungiNo6");
        title.add("vehariChock");
        title.add("BCGchock");
        title.add("HamayunRoad");


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for (int i = 0; i < arrayList.size(); i++) {
            for (int j = 0; j < title.size(); j++) {
                mMap.addMarker(new MarkerOptions().position(arrayList.get(i)).title((title.get(j))));

            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(arrayList.get(i)));
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String markerTitle = marker.getTitle();
                Toast.makeText(DriverMap.this, markerTitle, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
//        mMap.addPolyline(new PolylineOptions().add(chungiNo9,HamayunRoad).width(10).color(Color.GREEN).geodesic(true));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chungiNo9,18));
        //adding drive route
        PolylineOptions options = new PolylineOptions().add(chungiNo9).add(vehariChock).width(8).color(Color.GREEN).geodesic(true);
        mMap.addPolyline(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chungiNo9, 13));
    }


    public void gpsEnabling() {
        LocationRequest locationRequest = new LocationRequest().setFastestInterval(1500)
                .setInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    task.getResult(ApiException.class);
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(DriverMap.this, CHECK_REQUEST_CODE);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            } catch (ClassCastException ex) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE: {
                            break;
                        }
                    }
                }

            }
        });
    }

    private void enableUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void zoomToUserLocation() {
        @SuppressLint("MissingPermission") Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Toast.makeText(this, " You have already granted this permission!", Toast.LENGTH_SHORT).show();
        int ACCESS_LOCATION_REQUEST_CODE = 120;
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                zoomToUserLocation();

            } else {
                //We can show a dialog that permission is not granted...
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void backArrowDriverMap(View view) {
        Intent intent = new Intent(this, DriverDashboard.class);
        startActivity(intent);
    }

    public void onBackPressed() {
        Intent intent = new Intent(this, DriverDashboard.class);
        startActivity(intent);
        finish();


    }
    public  void gettingCordinate(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference stores = db.collection("stores");
        stores.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Toast.makeText(DriverMap.this, "oo", Toast.LENGTH_SHORT).show();;
                    }
                } else {
                    Toast.makeText(DriverMap.this, "err", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
