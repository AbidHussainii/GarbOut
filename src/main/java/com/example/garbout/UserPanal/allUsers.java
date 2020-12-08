package com.example.garbout.UserPanal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.example.garbout.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Collections;

public class allUsers extends AppCompatActivity {
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter<upload, allUsers.DataViewHolder> adapter;

    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    String userId;
    TextView userName;
    ImageView profileImage;

    StorageReference storagereference;
    private ProgressBar mProgressCircle;
    ImageView showImage;
    String imgUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        recyclerView = findViewById(R.id.recyclerview);

        userName = findViewById(R.id.f_name);
        profileImage = findViewById(R.id.userImage);
        retrieveFromfirestore();

//        fAuth = FirebaseAuth.getInstance();
//        fStore = FirebaseFirestore.getInstance();
//        userID = fAuth.getCurrentUser().getUid();
//        storageReference = FirebaseStorage.getInstance().getReference();

//        final DocumentReference documentReference = fStore.collection("users").document(userID);
//        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                userName.setText(value.getString("name"));
//
//            }
//        });


//        String key = getIntent().getStringExtra("key");
//        final DocumentReference documentReference = firebaseFirestore.collection("users").document(key);
//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//                    if (documentSnapshot.exists()) {
//
////                        date.setText((String) documentSnapshot.get("Date"));
////                        time.setText((String) documentSnapshot.get("time"));
////                        complainDetail.setText((String) documentSnapshot.get("Detail"));
////                        complainAddress.setText((String) documentSnapshot.get("complainAddress"));
//                      userName.setText((String)documentSnapshot.get("UserName"));
//                        Picasso.get().load((String) documentSnapshot.get("Url")).into(profileImage);
//                        // complainAddress.setText(documentSnapshot.get(""));
//                    }
//                }
//            }
//        });
//    }
    }

    private void retrieveFromfirestore() {
        Query query = firestore.collection("users").whereEqualTo("isUser", "1");
        //.whereIn("UserID", Collections.singletonList(userId));

        //RecyclerOptions
        FirestoreRecyclerOptions<upload> options = new FirestoreRecyclerOptions.Builder<upload>()
                .setQuery(query, upload.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<upload, DataViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DataViewHolder holder, int position, @NonNull upload model) {
                final String DocId = getSnapshots().getSnapshot(position).getId();
                holder.nameOfUser_f.setText(model.getName());
                Picasso.get().load(String.valueOf(model.getUrl())).into(holder.userProfilePicture);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toast.makeText(UserComplainsActivity.this, "key"+DocId, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(allUsers.this, UserProfile.class);
                        intent.putExtra("key", DocId);
                        intent.putExtra("role", "admin");
                        startActivity(intent);


                    }
                });
            }


            @NonNull
            @Override
            public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_model, parent, false);
                return new DataViewHolder(v);
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //View Holder Class
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView nameOfUser_f;
        ImageView userProfilePicture;
        Button deleteUser;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            nameOfUser_f = itemView.findViewById(R.id.username);
            userProfilePicture = itemView.findViewById(R.id.userImage);
            deleteUser = itemView.findViewById(R.id.deleteUser);
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
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}