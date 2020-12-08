package com.example.garbout.DriverPanal;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.garbout.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.squareup.picasso.Picasso;

public class pop_up extends Activity {
    Dialog myDialog;
    TextView FullName,requestLocation,phone;
    FirebaseAuth fAuth;
    FirebaseFirestore firestore;
    DocumentReference documentReference;
    String userID;
    ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_click);


        FullName = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        requestLocation = findViewById(R.id.location);
        profile=findViewById(R.id.profile);


        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

//-------------------------------Getting_values_from_Firebase--------------------------------------------------------------------------------------------



       documentReference = firestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                FullName.setText(value.getString("name"));

                phone.setText(value.getString("phoneNumber"));

                requestLocation.setText(value.getString("address"));

            }
        });


    }
}