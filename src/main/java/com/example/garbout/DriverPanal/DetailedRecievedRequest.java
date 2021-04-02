package com.example.garbout.DriverPanal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbout.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class DetailedRecievedRequest extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    CollectionReference collectionReference;
    String key;
    TextView date, time, complainAddress, complainDetail;
    ImageView complainPicture;
    String status, uid;
    LinearLayout linearLayout, linearLayout1;
    Button deleteComplain;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_recieved_request);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        linearLayout = findViewById(R.id.btnLayout);
        linearLayout1 = findViewById(R.id.btnDelLayout);
        complainAddress = findViewById(R.id.userComplainAddress);
        complainDetail = findViewById(R.id.userComplainDetail);
        complainPicture = findViewById(R.id.complainPic);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        key = getIntent().getStringExtra("key");

        uid = firebaseAuth.getCurrentUser().getUid();


        final DocumentReference documentReference = firebaseFirestore.collection("Complains").document(key);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        date.setText((String) documentSnapshot.get("Date"));
                        time.setText((String) documentSnapshot.get("time"));
                        complainDetail.setText((String) documentSnapshot.get("Detail"));
                        complainAddress.setText((String) documentSnapshot.get("complainAddress"));
                        status= (String) documentSnapshot.get("status");
                        Picasso.get().load((String) documentSnapshot.get("Url1")).into(complainPicture);
                        Toast.makeText(DetailedRecievedRequest.this, ""+status, Toast.LENGTH_SHORT).show();
                        if (status.equals("accept")){
                            linearLayout.setVisibility(View.GONE);
                            linearLayout1.setVisibility(View.GONE);
                        }
                                            if (status.equals("Pending")){
                        linearLayout.setVisibility(View.VISIBLE);
                        linearLayout1.setVisibility(View.GONE);
                    }

                        // complainAddress.setText(documentSnapshot.get(""));
                    }
                }
            }
        });

        checkUser();
    }

//    public void adminCheck() {
//        final DocumentReference documentReference = firebaseFirestore.collection("users").document(uid);
//        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.getString("isAdmin") != null) {
//                    linearLayout1.setVisibility(View.VISIBLE);
//                    linearLayout.setVisibility(View.GONE);
//
//                }
//                if (documentSnapshot.getString("isDriver") != null) {
//                    if (status.equals("accept")){
//                        linearLayout.setVisibility(View.GONE);
//                        linearLayout1.setVisibility(View.GONE);
//                    }
//                    if (status.equals("Pending")){
//                        linearLayout.setVisibility(View.VISIBLE);
//                        linearLayout1.setVisibility(View.GONE);
//                    }
//                }
//            }
//
//        });
//    }

    public void acceptRequest(View view) {
        DocumentReference documentReference = firebaseFirestore.collection("Complains").document(key);
        status = "accept";
        Map<String, Object> complain = new HashMap<>();
        complain.put("status", status);
        documentReference.update(complain).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DetailedRecievedRequest.this, "Done", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), UserReceivedRequest.class));
                //linearLayout.setVisibility(View.GONE);
            }
        });


        Intent intent = new Intent(this, UserReceivedRequest.class);
        startActivity(intent);
        //linearLayout1.setVisibility(View.GONE);
        Intent i = new Intent(this, UserReceivedRequest.class);
        startActivity(i);


    }

    public void cancelRequest(View view) {

        DocumentReference documentReference = firebaseFirestore.collection("Complains").document(key);
        status = "reject";
        Map<String, Object> complain = new HashMap<>();
        complain.put("status", status);
        documentReference.update(complain).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DetailedRecievedRequest.this, "Done", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), UserReceivedRequest.class));
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }

    //
    private void checkUser() {
        final DocumentReference documentReference = firebaseFirestore.collection("users").document(uid);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getString("isAdmin") != null) {
                    linearLayout.setVisibility(View.GONE);
                    linearLayout1.setVisibility(View.VISIBLE);

                }


            }
        });
    }


    public void bacckArrow(View view) {
        onBackPressed();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}