package com.example.garbout.DriverPanal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.garbout.R;
import com.example.garbout.UserPanal.LoginActivity;
import com.example.garbout.UserPanal.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;
import java.util.Collections;

public class DriverDashboard extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    String userID;
    TextView dName,dPhone,dRoute;

    LatLng ali = new LatLng(30.2072136,71.3928869);

    ArrayList<LatLng> latLonList = new ArrayList<LatLng>();
    ArrayList<String> lonList = new ArrayList<String>();
    ArrayList<String> latList = new ArrayList<String>();
     Button button;

     String id , lat , lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);

        fStore = FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        dName=findViewById(R.id.dName);
        dPhone=findViewById(R.id.dPhone);
        dRoute=findViewById(R.id.dRoute);
        driverData();





    }

    public void user_requests(View view) {
        Intent intent=new Intent(this, UserReceivedRequest.class);
        startActivity(intent);
    }

    public void myTask(View view) {
        Intent intent=new Intent(this, DriverTask.class).putExtra("status", "accept");
        startActivity(intent);
    }

    public void myRoute(View view) {

        Query query=fStore.collection("Complains").whereIn("status", Collections.singletonList("accept"));
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    int i = 0;


                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        id=documentSnapshot.getId();
                        DocumentReference documentReference = fStore.collection("Complains").document(id);
                        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                //Toast.makeText(mapBox.this, ""+id, Toast.LENGTH_SHORT).show();
//                lat="30.2072136";
//                lan="71.3928869";
                                lat =value.getString("userLat");
                              lon =value.getString("userLan");


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

        Intent intent=new Intent(this, mapBox.class)
//                .putExtra("list",latLonList);
                .putStringArrayListExtra("latList",latList).putStringArrayListExtra("lonList",lonList);
        startActivity(intent);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    public  void driverData(){
        final DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                dName.setText(value.getString("name"));
                dPhone.setText(value.getString("driverRoute"));
                dRoute.setText(value.getString("phoneNumber"));

            }
        });
    }
}

