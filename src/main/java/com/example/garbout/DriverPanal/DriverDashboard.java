package com.example.garbout.DriverPanal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.garbout.R;
import com.example.garbout.UserPanal.LoginActivity;
import com.example.garbout.UserPanal.MainActivity;
import com.example.garbout.UserPanal.UserProfile;
import com.example.garbout.UserPanal.modelClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.type.LatLng;
import com.google.android.gms.maps.model.LatLng;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DriverDashboard extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String userID, docId, profile;
    TextView dName;
    TextView dPhone;
    TextView dRoute;
    TextView dCNIC;
    TextView driverName;
    TextView totelRequest;
    TextView completedRequest;
    TextView remainingRequest;
    String completedC, pendingC, acceptedC;
    ImageView driverProfile;
    int j, a, c = 5, p;

    LatLng ali = new LatLng(30.2072136, 71.3928869);

    ArrayList<LatLng> latLonList = new ArrayList<LatLng>();
    ArrayList<String> lonList = new ArrayList<String>();
    ArrayList<String> latList = new ArrayList<String>();
    Button button;

    String id, lat, lon, status, key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);


        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        key = getIntent().getStringExtra("key");
        userID = firebaseAuth.getCurrentUser().getUid();


        driverProfile = findViewById(R.id.driverProfile);
        driverName = findViewById(R.id.Name);
        totelRequest = findViewById(R.id.totelRequest);
        completedRequest = findViewById(R.id.completedRequest);
        remainingRequest = findViewById(R.id.remainingRequest);

        // Toast.makeText(this, ""+c, Toast.LENGTH_SHORT).show();


//        dName = findViewById(R.id.dName);
//        dPhone = findViewById(R.id.dPhone);
//        dRoute = findViewById(R.id.dRoute);
//        dCNIC = findViewById(R.id.dCNIC);
        driverData();
        retrieveDriverProfile();
        getAllRequests();
        getAllAcceptedRequests();
        getAllCompletedRequest();


    }

    public void user_requests(View view) {
//        Intent intent = new Intent(this, UserReceivedRequest.class).putExtra("status", "Pending");
//        startActivity(intent);
        makePhoneCall();
    }

    public void myTask(View view) {
        Intent intent = new Intent(this, DriverTask.class).putExtra("status", "accept");
        startActivity(intent);
    }

    public void myRoute(View view) {

        Query query = firebaseFirestore.collection("Complains").whereIn("status", Collections.singletonList("accept"));
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int i = 0;
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        final modelClass modelClass = documentSnapshot.toObject(modelClass.class);
                        id = documentSnapshot.getId();
                        DocumentReference documentReference = firebaseFirestore.collection("Complains").document(id);
                        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                //Toast.makeText(mapBox.this, ""+id, Toast.LENGTH_SHORT).show();
//                lat="30.2072136";
//                lan="71.3928869";
                                lat = modelClass.getUserLat();
                                lon = modelClass.getUserLan();
                                docId = id;


//                                Toast.makeText(DriverDashboard.this, ""+lat + lon, Toast.LENGTH_SHORT).show();
//                                latLonList.add(new LatLng(lat,lon));
                                latList.add(lat);
                                lonList.add(lon);
//                                 Toast.makeText(DriverDashboard.this, "" + lon+lat, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

//                    Intent intent=new Intent(DriverDashboard.this, mapBox.class)
////                .putExtra("list",latLonList);
//                            .putStringArrayListExtra("latList",latList).putStringArrayListExtra("lonList",lonList);
//                    startActivity(intent);

                }
            }
        });

        /*Intent intent = new Intent(this, mapBox.class)
                .putStringArrayListExtra("latList", latList).putStringArrayListExtra("lonList", lonList);
        intent.putExtra("docId", docId);
        intent.putExtra("completedCounter", completedC);
        intent.putExtra("acceptedCounter", acceptedC);

        startActivity(intent);*/


    }

    public void completedTask(View view) {
        //startActivity(new Intent(getApplicationContext(), MarkerInfo.class));
        Intent intent = new Intent(this, DriverTask.class).putExtra("status", "Completed");
        startActivity(intent);
//        makePhoneCall();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        setResult(RESULT_OK, new Intent().putExtra("EXIT", true));
                        finish();
                    }

                }).create().show();
    }

    public void logout(View view) {
        new SweetAlertDialog(DriverDashboard.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Are you sure want to Logout?")
                .setConfirmText("LogOut")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        firebaseAuth.signOut();
                        Intent intent = new Intent(DriverDashboard.this, LoginActivity.class);
                        startActivity(intent);
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    public void driverData() {
        final DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                // dName.setText(value.getString("name"));
//                dPhone.setText(value.getString("phoneNumber"));
//                dRoute.setText(value.getString("driverRoute"));
//                dCNIC.setText(value.getString("CNIC"));
                profile = value.getString("Url");
                Picasso.get().load(profile).into(driverProfile);
                driverName.setText(value.getString("name"));

            }
        });
    }


    private void retrieveDriverProfile() {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    profile = documentSnapshot.getString("Url");
                    Picasso.get().load(profile).into(driverProfile);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DriverDashboard.this, "Something Wents Wrong...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void profileOfDriver(View view) {
        Intent intent = new Intent(getApplicationContext(), UserProfile.class);
        intent.putExtra("role", "user");
        startActivity(intent);
    }

    private void getAllRequests() {
        Query query = firebaseFirestore.collection("Complains").whereIn("status", Collections.singletonList("Pending"));
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    a = 0;
                    for (final QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        a = a + 1;

                    }
                    totelRequest.setText(String.valueOf(a));


                }

            }

        });

    }

    private void getAllCompletedRequest() {
        Query query = firebaseFirestore.collection("Complains").whereIn("status", Collections.singletonList("Completed"));
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    c = 0;
                    for (final QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        c = c + 1;
                        completedC = String.valueOf(c);

                    }
                    completedRequest.setText(String.valueOf(completedC));
                }

            }

        });


    }

    private void getAllAcceptedRequests() {
        Query query = firebaseFirestore.collection("Complains").whereIn("status", Collections.singletonList("accept"));
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    j = 0;
                    for (final QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        j = j + 1;
                        acceptedC = String.valueOf(j);

                    }
                    remainingRequest.setText(String.valueOf(acceptedC));
                }

            }

        });


    }


    private void makePhoneCall() {
        String number = "03117124299";
        if (number.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(DriverDashboard.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DriverDashboard.this,
                        new String[]{Manifest.permission.CALL_PHONE}, 1);
            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            Toast.makeText(DriverDashboard.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



