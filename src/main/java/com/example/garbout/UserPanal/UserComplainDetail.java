package com.example.garbout.UserPanal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.garbout.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class UserComplainDetail extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    TextView date, time, complainAddress, complainDetail;
    ImageView complainPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_complain_detail);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        complainAddress = findViewById(R.id.userComplainAddress);
        complainDetail = findViewById(R.id.userComplainDetail);
        complainPicture = findViewById(R.id.complainPic);
        firebaseFirestore = FirebaseFirestore.getInstance();
        String key = getIntent().getStringExtra("key");
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
                        Picasso.get().load((String) documentSnapshot.get("Url1")).into(complainPicture);

                        // complainAddress.setText(documentSnapshot.get(""));
                    }
                }
            }
        });

    }

    public void myComplainBackArrow(View view) {

        Intent intent = new Intent(this, UserComplains.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(UserComplainDetail.this, MainActivity.class);
        startActivity(intent);
        finish();


    }
}