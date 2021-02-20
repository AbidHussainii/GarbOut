package com.example.garbout.UserPanal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.garbout.Admin.Admin;
import com.example.garbout.DriverPanal.DriverDashboard;
import com.example.garbout.DriverPanal.UserReceivedRequest;
import com.example.garbout.DriverPanal.mapBox;
import com.example.garbout.R;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        Sprite doubleBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        checkUser();
//        Handler handler=new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                Intent intent=new Intent(SplashActivity.this, DriverDashboard.class);
////                startActivity(intent);
//             checkUser();
//                finish();
//            }
//        },3000);

    }

    private void checkUser() {

        if (firebaseAuth.getCurrentUser() != null) {
            userId = firebaseAuth.getCurrentUser().getUid();
            final DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.getString("isAdmin") != null) {

                        startActivity(new Intent(getApplicationContext(), Admin.class));
                        SplashActivity.this.finish();

                    } else if (documentSnapshot.getString("isDriver") != null) {

                        startActivity(new Intent(getApplicationContext(), DriverDashboard.class));
                        SplashActivity.this.finish();

                    } else if (documentSnapshot.getString("isUser") != null) {

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        SplashActivity.this.finish();
                    }
                }
            });
        }
        else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            SplashActivity.this.finish();
        }
    }
}