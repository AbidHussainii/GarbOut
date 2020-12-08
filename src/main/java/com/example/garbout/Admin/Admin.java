package com.example.garbout.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.garbout.DriverPanal.DriverDashboard;
import com.example.garbout.DriverPanal.DriverTask;
import com.example.garbout.DriverPanal.UserReceivedRequest;
import com.example.garbout.R;
import com.example.garbout.UserPanal.LoginActivity;
import com.example.garbout.UserPanal.MainActivity;
import com.example.garbout.UserPanal.SignUpActivity;
import com.example.garbout.UserPanal.UserComplains;
import com.example.garbout.UserPanal.allUsers;
import com.example.garbout.UserPanal.upload;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Admin extends AppCompatActivity {
    LinearLayout userLayout, user1Layout, driverLayout, driver1Layout;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    String userID, var;
    ImageView addDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        userLayout = findViewById(R.id.userLayout);
        user1Layout = findViewById(R.id.user1Layout);
        driverLayout = findViewById(R.id.driverLayout);
        driver1Layout = findViewById(R.id.driver1Layout);
        addDriver = findViewById(R.id.addDriver);
        //------------------- setting layouts------------------//
        userLayout.setVisibility(View.VISIBLE);
        user1Layout.setVisibility(View.VISIBLE);
        addDriver.setVisibility(View.GONE);
        driverLayout.setVisibility(View.GONE);
        driver1Layout.setVisibility(View.GONE);


        //--------------------------------------------------//
        fStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

//        final DocumentReference documentReference = fStore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
//        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.getString("isAdmin") != null) {
//                    var="isAdmin";
//
//
//                } else if (documentSnapshot.getString("isDriver") != null) {
//                    var="isDriver";
//                }
//            }
//
//        });

    }



    public void allusers(View view) {
        userLayout.setVisibility(View.VISIBLE);
        user1Layout.setVisibility(View.VISIBLE);

        driverLayout.setVisibility(View.GONE);
        driver1Layout.setVisibility(View.GONE);
        addDriver.setVisibility(View.GONE);
    }

    public void alldrivers(View view) {
        userLayout.setVisibility(View.GONE);
        user1Layout.setVisibility(View.GONE);

        driverLayout.setVisibility(View.VISIBLE);
        driver1Layout.setVisibility(View.VISIBLE);

        addDriver.setVisibility(View.VISIBLE);


    }

    public void userList(View view) {
        startActivity(new Intent(getApplicationContext(), allUsers.class));
    }

    public void userRequests(View view) {
        startActivity(new Intent(getApplicationContext(), UserReceivedRequest.class));
//        Intent intent=new Intent(getApplicationContext(),UserReceivedRequest.class);
//        intent.putExtra("status",var);
//        startActivity(intent);

    }

    public void acceptedRequest(View view) {
        Intent intent = new Intent(getApplicationContext(), DriverTask.class);
        intent.putExtra("status", "accept");
        startActivity(intent);
    }

    public void rejectedJobs(View view) {

        Intent intent = new Intent(getApplicationContext(), DriverTask.class);
        intent.putExtra("status", "reject");
        startActivity(intent);
    }

    public void addUser(View view) {
        String var = "admin";
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        intent.putExtra("role", var);
        startActivity(intent);


    }


    public void driverList(View view) {

        startActivity(new Intent(getApplicationContext(), allDriver.class));
    }

    public void driverJobs(View view) {
        Intent intent = new Intent(getApplicationContext(), DriverTask.class);
        intent.putExtra("status", "accept");
        startActivity(intent);
    }

    public void driverRejectedJobs(View view) {
        Intent intent = new Intent(getApplicationContext(), DriverTask.class);
        intent.putExtra("status", "reject");
        startActivity(intent);

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

    public void adminOut(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Really Exit?");
        builder.setMessage("Are you sure you  want to logout?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }


}