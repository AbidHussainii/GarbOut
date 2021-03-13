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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbout.Admin.Admin;
import com.example.garbout.R;
import com.example.garbout.UserPanal.UserProfile;
import com.example.garbout.UserPanal.modelClass;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;

public class allDriver extends AppCompatActivity {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private FirestoreRecyclerAdapter<modelClass, allDriver.DataViewHolder> adapter;

    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    String userId;
    TextView driverName, driverPhone;
    ImageView profileImage;

    StorageReference storagereference;
    private ProgressBar mProgressCircle;
    ImageView showImage;
    String imgUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        recyclerView = findViewById(R.id.recyclerview);
        retrieveFromfirestore();
    }

    private void retrieveFromfirestore() {
        Query query = firestore.collection("users").whereEqualTo("isDriver", "2");

        //RecyclerOptions
        FirestoreRecyclerOptions<modelClass> options = new FirestoreRecyclerOptions.Builder<modelClass>()
                .setQuery(query, modelClass.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<modelClass, allDriver.DataViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull allDriver.DataViewHolder holder, int position, @NonNull modelClass model) {
                final String DocId = getSnapshots().getSnapshot(position).getId();
                holder.driverName.setText(model.getName());
                holder.driverPhone.setText(model.getPhoneNumber());
                holder.checkProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String var = "admin";
                        Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                        intent.putExtra("role", var);
                        intent.putExtra("key", DocId);

                        startActivity(intent);


                    }
                });
                holder.addRoute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(allDriver.this, "Route Added ", Toast.LENGTH_SHORT).show();
                    }
                });

                //Picasso.get().load(String.valueOf(model.getUrl())).into(holder.userProfilePicture);
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // Toast.makeText(UserComplainsActivity.this, "key"+DocId, Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(allDriver.this, UserProfile.class);
//                        intent.putExtra("key", DocId);
//                        intent.putExtra("role", "admin");
//                        startActivity(intent);
//
//
//                    }
//                });
            }


            @NonNull
            @Override
            public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_model, parent, false);
                return new DataViewHolder(v);
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //View Holder Class
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView driverName, driverPhone;
//        ImageView userProfilePicture;
        Button checkProfile,addRoute;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            driverName = itemView.findViewById(R.id.drivername);
            driverPhone = itemView.findViewById(R.id.driverNo);
            addRoute=itemView.findViewById(R.id.addRoute);
            checkProfile=itemView.findViewById(R.id.checkProfile);
            // deleteUser = itemView.findViewById(R.id.deleteUser);
            // itemView.setOnClickListener((View.OnClickListener) this);
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

    public void complainBackArrow(View view) {
        Intent intent = new Intent(this, Admin.class);
        startActivity(intent);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
}