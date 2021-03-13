package com.example.garbout.UserPanal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbout.Admin.Admin;
import com.example.garbout.Admin.allUsers;
import com.example.garbout.DriverPanal.DriverDashboard;
import com.example.garbout.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserProfile extends AppCompatActivity {
    EditText username, usermail, usernumber, userpassword, user_CNIC;
    Button updateProfile, deleteUser;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, key, role;
    TextView userProfileName, editProfile;
    ImageView profileImage, selectPic;
    String userProfile;
    // private Uri imageUri;
    StorageReference storageReference;
    ProgressBar progressBar;
    DocumentReference documentReference;
    boolean isImageFitToScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        username = findViewById(R.id.f_name);
        usermail = findViewById(R.id.f_email);
        usernumber = findViewById(R.id.f_phone);
//        userpassword = findViewById(R.id.f_password);
        userProfileName = findViewById(R.id.userProfileName);
        profileImage = findViewById(R.id.profile_pic);
        updateProfile = findViewById(R.id.update);
        editProfile = findViewById(R.id.editProfile);
//        selectPic = findViewById(R.id.choosePicture);
        deleteUser = findViewById(R.id.deleteUser);
        // ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        key = getIntent().getStringExtra("key");
        role = getIntent().getStringExtra("role");
        //retrieveImageFromFirebase();
        if (role.equals("admin")) {
            retreiveData(key);
            updateProfile.setVisibility(View.GONE);
           // selectPic.setVisibility(View.INVISIBLE);
            deleteUser.setVisibility(View.VISIBLE);
            updateProfile.setVisibility(View.GONE);


        }


        if (role.equals("user")) {
            retreiveData(userID);
            updateProfile.setVisibility(View.GONE);
            deleteUser.setVisibility(View.GONE);
//            updateProfile.setVisibility(View.VISIBLE);
            //updateProfile.setVisibility(View.GONE);
        }

        //-----------delete user.........//
        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteUser();

            }
        });


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile.setVisibility(View.VISIBLE);

            }
        });

    }


    public void DeleteUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setTitle("Delete Account?");
        builder.setMessage("Deleting this account will result in completely removing your account?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                fStore.collection("users").document(key).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UserProfile.this, "User successfully deleted!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), allUsers.class));
                            }
                        });
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

    public void retreiveData(String role) {
        documentReference = fStore.collection("users").document(role);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                username.setText(value.getString("name"));
                usermail.setText(value.getString("mail"));
                usernumber.setText(value.getString("phoneNumber"));
//                userpassword.setText(value.getString("password"));
                userProfileName.setText(value.getString("name"));
//                user_CNIC.setText(value.getString("CNIC"));
                userProfile = value.getString("Url");
                Picasso.get().load(userProfile).into(profileImage);

            }
        });

    }

    public void selectProfile(View view) {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, 1000);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                profileImage.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(final Uri imageUri) {
        //upload image to firebase
        final StorageReference fileRef = storageReference.child("Complain Pic").child(userID).child("image.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Toast.makeText(MyAccountActivity.this, "success", Toast.LENGTH_SHORT).show();
                        //Picasso.get().load(uri).into(profileImage);
                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(userID);
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("Url", String.valueOf(uri));
                        documentReference.set(hashMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(UserProfile.this, "Url Saved Successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfile.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //    private void retrieveImageFromFirebase() {
//        DocumentReference documentReference = fStore.collection("users").document(userID);
//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//
//
//
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(UserProfile.this, "Something Wents Wrong...", Toast.LENGTH_SHORT).show();
//            }
//        });
////        String newUrl;
////        //Picasso.get().load(model.getUrl()).into(profileImage);
////        storageReference= FirebaseStorage.getInstance().getReference();
////        StorageReference profileRef = storageReference.child("Complain Pic").child(userID).child("image.jpg");
////        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
////            @Override
////            public void onSuccess(Uri uri) {
////                Picasso.get().load(uri).into(profileImage);
////            }
////        }).addOnFailureListener(new OnFailureListener() {
////            @Override
////            public void onFailure(@NonNull Exception e) {
////                Toast.makeText(MyAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
////            }
////        });
//
//    }
    private void checkUser() {
        final DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getString("isAdmin") != null) {

                    startActivity(new Intent(getApplicationContext(), Admin.class));
                } else if (documentSnapshot.getString("isDriver") != null) {
                    startActivity(new Intent(getApplicationContext(), DriverDashboard.class));

                } else if (documentSnapshot.getString("isUser") != null) {

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }

        });
    }


    public void updateProfile(View view) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(UserProfile.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#000000"));
        pDialog.setTitle("Please Wait");
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();

        String uName = username.getText().toString().trim();
        String uMail = usermail.getText().toString().trim();
        String uNumber = usernumber.getText().toString().trim();
        String uPassword = userpassword.getText().toString().trim();
        final DocumentReference documentReference = fStore.collection("users").document(userID);
        Map<String, Object> user = new HashMap<>();
        user.put("name", uName);
        user.put("mail", uMail);
        user.put("phoneNumber", uNumber);
        user.put("password", uPassword);
        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UserProfile.this, "Profile has been Updated ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserProfile.this, UserProfile.class);
                pDialog.dismiss();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void backArrow(View view) {
        onBackPressed();
    }


}

