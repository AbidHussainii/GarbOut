package com.example.garbout.UserPanal;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.garbout.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Collections;

public class UserComplains extends AppCompatActivity {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firestore.collection("Complains");
    private FirestoreRecyclerAdapter<upload, DataViewHolder> adapter;
    private ProgressBar mProgressCircle;
    RecyclerView recyclerView;
    StorageReference storagereference;
    FirebaseAuth firebaseAuth;
    ImageView showImage;
    String imgUpload;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_complains);
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        recyclerView = findViewById(R.id.recyclerview);
        retrieveFromfirestore();


    }

    private void retrieveFromfirestore() {
        Query query = firestore.collection("Complains").whereIn("UserID", Collections.singletonList(userId));

        //RecyclerOptions
        FirestoreRecyclerOptions<upload> options = new FirestoreRecyclerOptions.Builder<upload>()
                .setQuery(query, upload.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<upload, DataViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DataViewHolder holder, int position, @NonNull upload model) {
                final String DocId = getSnapshots().getSnapshot(position).getId();
                holder.nameOfUser_f.setText(model.getUserName());
                holder.showdate.setText(model.getDate());
                holder.showtime.setText(model.getTime());
                Picasso.get().load(String.valueOf(model.getUrl1())).into(holder.complainPicture_f);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toast.makeText(UserComplainsActivity.this, "key"+DocId, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserComplains.this, UserComplainDetail.class);
                        intent.putExtra("key", DocId);
                        startActivity(intent);


                    }
                });

            }


            @NonNull
            @Override
            public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_item, parent, false);
                return new DataViewHolder(v);
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //View Holder Class
    }


    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView nameOfUser_f, showdate, showtime;
        ImageView complainPicture_f, imgCalender;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            nameOfUser_f = itemView.findViewById(R.id.text_view_name);
            showdate = itemView.findViewById(R.id.text_view_date);
            showtime = itemView.findViewById(R.id.text_view_time);
            complainPicture_f = itemView.findViewById(R.id.image_view_Retrieve);
            imgCalender = itemView.findViewById(R.id.calender);
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
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }
}



