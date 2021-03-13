package com.example.garbout.DriverPanal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbout.R;
import com.example.garbout.UserPanal.UserMap;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class mapBox extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener,
        PermissionsListener, MapboxMap.OnMapClickListener {
    private MapView mapView;
    private MapboxMap map;
    private Button startButton;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private Point originPosition, routeOrigin, routeDestination;
    private Point destinationPosition;
    private Marker marker;
    private NavigationMapRoute navigationMapRoute;
    private static final String TAG = "MainActivity";

    ArrayList<String> latList = new ArrayList<>();
    ArrayList<String> lonList = new ArrayList<>();
    ArrayList mapList = new ArrayList<LatLng>();

    ///// on marker click data array
    ArrayList<String> docIDArray = new ArrayList<>();
    int x = 0;
    int i = 0;
    TextView currentDayDate,completedTaskCounter,acceptedTaskCounter;
    FirebaseFirestore fStore;
    FirebaseAuth firebaseAuth;
    CollectionReference collectionReference;
    DocumentReference documentReference;
    String userID, id, lat, lan, popName;
    String phoneNo, address, name;
    double dlat, dlan;
//    Double lat,lan;

    Dialog myDialog;
    //.......gps Enabling
    private LocationSettingsRequest.Builder builder;
    private static final int CHECK_REQUEST_CODE = 109;

////////////////curren date and time ///////////////
    private TextView dateTimeDisplay;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date,acceptedCounter,completedCounter;
    String mapDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();

        fStore = FirebaseFirestore.getInstance();
        collectionReference = fStore.collection("Complains");
        gpsEnabling();
        Mapbox.getInstance(this, "pk.eyJ1IjoiYWJpZGh1c3NhaW5pIiwiYSI6ImNraDdmaDVkYjAzcXkydG83N216a2poaDUifQ.CKc562kHPYFXn_A08v1iRA");
        setContentView(R.layout.activity_map_box);
        mapView = findViewById(R.id.mapView);
        startButton = findViewById(R.id.startNav);
        mapView.setStyleUrl(Style.MAPBOX_STREETS);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        myDialog = new Dialog(this);

///////////getting values from previous activities/////////
        id = getIntent().getStringExtra("docId");
        acceptedCounter=getIntent().getStringExtra("acceptedCounter");
        completedCounter=getIntent().getStringExtra("completedCounter");

        ////////////////////hooks/////////////////
        currentDayDate=findViewById(R.id.currentDayDate);
        completedTaskCounter=findViewById(R.id.completedTaskCounter);
        acceptedTaskCounter=findViewById(R.id.acceptedTaskCounter);

        completedTaskCounter.setText(completedCounter);
        acceptedTaskCounter.setText(acceptedCounter);

        ///////getting date //////
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEE.dd/MM/yyyy");
        date = dateFormat.format(calendar.getTime());
        currentDayDate.setText(date);
         mapDate=String.valueOf(currentDayDate);




        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .origin(originPosition)
                        .destination(destinationPosition)
                        .shouldSimulateRoute(true)
                        .build();
                NavigationLauncher.startNavigation(mapBox.this, options);
            }
        });

    }
    //onCreate

    @Override
    public void onMapReady(MapboxMap mapboxMap) {

        map = mapboxMap;
        enableLocation();
        retrieveCordinateFromFirebase();

        latList = getIntent().getStringArrayListExtra("latList");

        lonList = getIntent().getStringArrayListExtra("lonList");


//
//        //map.addOnMapClickListener(this);
//        double dLon = Double.parseDouble(lonList.get(0));
//        double dLat =  Double.parseDouble(latList.get(0));

//        Toast.makeText(this, "Hey "+ dlat + dlan, Toast.LENGTH_SHORT).show();

//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(dlat, dlan))
//                .title("marker : "+0));

//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(latAbid))
//                .title("marker : "+1122));

//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(dLon, dLat))
//                .title("marker three"));

//        Toast.makeText(this, "Hey 0"+ latList.get(0), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Hey 1"+ latList.get(1), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Hey 2"+ latList.get(2), Toast.LENGTH_SHORT).show();


        //Toast.makeText(this, "hello"+lat+lan, Toast.LENGTH_SHORT).show();
        //Toast.makeText(mapBox.this, "Hey " + lonList.get(0), Toast.LENGTH_SHORT).show();


//        LatLng mark = new LatLng(Double.parseDouble(lonList.get(0)),Double.parseDouble(latList.get(0)));
//
//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(mark))
////                    .position(new LatLng(Double.parseDouble(lonList.get(i)), Double.parseDouble(latList.get(i))))
//                .title("marker : "+1));


//        for (int i = 0; i < latList.size(); i++) {
//
//            LatLng mark = new LatLng(Double.parseDouble(lonList.get(i)),Double.parseDouble(latList.get(i)));
//
//            map.addMarker(new MarkerOptions()
//                    .position(new LatLng(mark))
////                    .position(new LatLng(Double.parseDouble(lonList.get(i)), Double.parseDouble(latList.get(i))))
//                    .title("marker : "+i));
//
//        }


//        map.addMarker(new MarkerOptions()
//                    .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lan)))
//                    .title("marker one"));

//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lan)))
//                .title("marker two"));

//        LatLng latAbid = new LatLng(30.1720563, 71.4572936);
//


//
//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(30.1720563, 71.4572936))
//                .title("etc.."));

//        map.addMarker(new MarkerOptions()
//                .position(new LatLng(30.2049337, 71.4642307))
//                ////1st
//                .title("etc...."));
//        routeOrigin = Point.fromLngLat(30.2084639, 71.4696846);
//
//        ////2nd
//        routeDestination = Point.fromLngLat(30.2049337, 71.4642307);
//        getRoute(routeOrigin, routeDestination);
        // mapView.setStyleUrl(Style.MAPBOX_STREETS);


//        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
//        symbolLayerIconFeatureList.add(Feature.fromGeometry(
//                Point.fromLngLat(-57.225365, -33.213144)));
//        symbolLayerIconFeatureList.add(Feature.fromGeometry(
//                Point.fromLngLat(-54.14164, -33.981818)));
//        symbolLayerIconFeatureList.add(Feature.fromGeometry(
//                Point.fromLngLat(-56.990533, -30.583266)));
//        map.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")
//
//                // Add the SymbolLayer icon image to the map style
//                .withImage(ICON_ID, BitmapFactory.decodeResource(
//                        BasicSymbolLayerActivity.this.getResources(), R.drawable.mapbox_marker_icon_default))
//
//                // Adding a GeoJson source for the SymbolLayer icons.
//                .withSource(new GeoJsonSource(SOURCE_ID,
//                        FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
//
//                // Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
//                // marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
//                // the coordinate point. This is offset is not always needed and is dependent on the image
//                // that you use for the SymbolLayer icon.
//                .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
//                        .withProperties(
//                                iconImage(ICON_ID),
//                                iconAllowOverlap(true),
//                                iconIgnorePlacement(true)
//                        )
//                ));
//                ;, new Style.OnStyleLoaded() {
//            @Override
//            public void onStyleLoaded(@NonNull Style style) {
//
//                // Map is set up and the style has loaded. Now you can add additional data or make other map adjustments.
//
//
//            }
//        });
    }


    private void enableLocation() {
        if (permissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine();
            initializeLocationLayer();

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void initializeLocationEngine() {
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();
        @SuppressLint("MissingPermission") Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    @SuppressLint("MissingPermission")
    private void initializeLocationLayer() {
        locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);


    }

    public void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0));
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        if (marker != null) {
            map.removeMarker(marker);
        }
        marker = map.addMarker(new MarkerOptions().position(point));
        destinationPosition = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        originPosition = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());
        getRoute(originPosition, destinationPosition);

    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null) {
                            Log.e(TAG, "No route found,check right user and access token");
                            return;

                        } else if (response.body().routes().size() == 0) {
                            Log.e(TAG, "No route found");
                            return;
                        }
                        DirectionsRoute currentRoute = response.body().routes().get(0);
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, map);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "Error" + t.getMessage());
                    }
                });


    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected() {
        locationEngine.requestLocationUpdates();

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            originLocation = location;
            setCameraPosition(location);
        }

    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void retrieveCordinateFromFirebase() {
        Query query = fStore.collection("Complains").whereIn("status", Collections.singletonList("accept"));
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        docIDArray.add(documentSnapshot.getId());
//                        Toast.makeText(mapBox.this, "" + docIDArray, Toast.LENGTH_SHORT).show();

                        DocumentReference documentReference = fStore.collection("Complains").document(documentSnapshot.getId());
                        //Toast.makeText(mapBox.this, "" + documentSnapshot.getId(), Toast.LENGTH_SHORT).show();
                        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                lat = value.getString("userLat");
                                lan = value.getString("userLan");
                                dlat = Double.parseDouble(lat);
                                dlan = Double.parseDouble(lan);
                                Toast.makeText(mapBox.this, "" + dlat, Toast.LENGTH_SHORT).show();
                                // name.add(value.getString("UserName"));
                                map.addMarker(new MarkerOptions()
                                        .position(new LatLng(dlat, dlan))
                                        .title("Name " + name));


                                name = value.getString("UserName");
                                Toast.makeText(mapBox.this, "" + name, Toast.LENGTH_SHORT).show();
                                phoneNo = value.getString("PhoneNo");
                                address = value.getString("complainAddress");
                                final String markerID = documentSnapshot.getId();

                                map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(@NonNull Marker marker) {
//                        myDialog.setContentView(R.layout.pop_up);
//                        myDialog.show();
                                        //Toast.makeText(mapBox.this, "" + name.get(x), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MarkerInfo.class);

                                        intent.putExtra("name", name);
                                        intent.putExtra("phoneNo", phoneNo);
                                        intent.putExtra("address", address);
                                        intent.putExtra("markerID", markerID);
                                        intent.putExtra("mapDate",mapDate);
                                        startActivity(intent);

                                        return false;

                                    }
                                });

                            }
                        });
                        //   Toast.makeText(mapBox.this, "NAme:"+ name+"phone:"+ phoneNo, Toast.LENGTH_SHORT).show();


//                                 Toast.makeText(mapBox.this, "Abid   1" + lan+lat, Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });

//        Query query=fStore.collection("Complains").whereIn("")
//        String newUrl;
//        //Picasso.get().load(model.getUrl()).into(profileImage);
//        storageReference= FirebaseStorage.getInstance().getReference();
//        StorageReference profileRef = storageReference.child("Complain Pic").child(userID).child("image.jpg");
//        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Picasso.get().load(uri).into(profileImage);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MyAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    public void gettingCordinate() {
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                    documentSnapshot.getId();
                    documentReference = fStore.collection("Complains").document(documentSnapshot.getId());

                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value.getString("status").equals("accept")) {

                                latList.add(value.getString("userLat"));
                                lonList.add(value.getString("userLan"));

                                Toast.makeText(mapBox.this, "Hello " + latList.get(0), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mapBox.this, "Something Wents Wrong...!", Toast.LENGTH_SHORT).show();
            }
        });


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
                                resolvableApiException.startResolutionForResult(mapBox.this, CHECK_REQUEST_CODE);
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
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }

//    public void back(View view) {
//        Intent intent = new Intent(this, DriverDashboard.class);
//        startActivity(intent);
//    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();


        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
        mapView.onStart();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public void completedTaskList(View view) {
        Intent intent = new Intent(this, DriverTask.class).putExtra("status", "Completed");
        startActivity(intent);
    }
}