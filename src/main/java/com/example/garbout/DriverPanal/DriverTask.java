package com.example.garbout.DriverPanal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.garbout.Admin.Admin;
import com.example.garbout.R;
import com.example.garbout.UserPanal.MainActivity;
import com.example.garbout.UserPanal.UserComplainDetail;
import com.example.garbout.UserPanal.upload;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.Collections;

public class DriverTask extends AppCompatActivity {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter<upload, DataViewHolder> adapter;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String userId,status;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_task);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        recyclerView = findViewById(R.id.recyclerview);
        status=getIntent().getStringExtra("status");

        retrieveFromfirestore();
    }

    private void retrieveFromfirestore() {
        if(status.equals("accept")){
            query = firestore.collection("Complains").whereIn("status", Collections.singletonList("accept"));
        }
        if(status.equals("reject")) {
            query = firestore.collection("Complains").whereIn("status", Collections.singletonList("reject"));
        }

        FirestoreRecyclerOptions<upload> options = new FirestoreRecyclerOptions.Builder<upload>()
                .setQuery(query, upload.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<upload, DataViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final DriverTask.DataViewHolder holder, int position, @NonNull upload model) {
                final String DocId = getSnapshots().getSnapshot(position).getId();
                holder.rName.setText(model.getUserName());
                holder.rTime.setText(model.getTime());
                Picasso.get().load(model.getUrl()).into(holder.rImage);
                //getting  user profile  image from storage
//                StorageReference storageReference= FirebaseStorage.getInstance().getReference();
//                StorageReference profileRef = storageReference.child("Complain Pic").child(userId).child("image.jpg");
//                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        Picasso.get().load(uri).into(holder.rImage);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(DriverTask.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toast.makeText(UserComplainsActivity.this, "key"+DocId, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DriverTask.this, DetailedRecievedRequest.class);
                        intent.putExtra("key", DocId);
                        startActivity(intent);


                    }
                });
            }

            @NonNull
            @Override
            public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_request_model, parent, false);
                return new DataViewHolder(v);
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView rName, rAddress, rTime;
        ImageView rImage;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            rName = itemView.findViewById(R.id.name);
            rAddress = itemView.findViewById(R.id.address);
            rTime = itemView.findViewById(R.id.time);
            rImage = itemView.findViewById(R.id.userImage);
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void arrowBack(View view) {
       onBackPressed();
    }

    private void checkUser() {
        final DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getString("isAdmin") != null) {
                    startActivity(new Intent(getApplicationContext(), Admin.class));

                }
                if (documentSnapshot.getString("isDriver") != null) {
                    startActivity(new Intent(getApplicationContext(), DriverDashboard.class));

                }
                if (documentSnapshot.getString("isUser") != null) {

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }

        });
    }
}