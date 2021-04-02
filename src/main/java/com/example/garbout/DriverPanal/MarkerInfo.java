package com.example.garbout.DriverPanal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbout.R;
import com.example.garbout.UserPanal.ComplainFormActivity;
import com.example.garbout.UserPanal.UserComplains;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MarkerInfo extends AppCompatActivity {
    TextView FullName, requestLocation, phone;
    FirebaseAuth fAuth;
    // FirebaseFirestore firestore;
    DocumentReference documentReference;
    String userID, driverEvidence;
    ImageView cameraIcon, driverClickedPicture, callIcon;
    Button sendingCompletedReuest;
    TextView text;
    private Uri imageUri;
    private String imgDown;
    String name, phoneNo, address, markerID,date;
    public int REQUEST_IMAGE_CAPTURE_CODE = 103;
    public int permmissionCode = 22;
    String userId, key;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    DocumentSnapshot documentSnapshot;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_info);

        name = getIntent().getStringExtra("name");
        phoneNo = getIntent().getStringExtra("phoneNo");
        address = getIntent().getStringExtra("address");
        markerID = getIntent().getStringExtra("markerID");
       // date=getIntent().getStringExtra("mapDate");


        //documentSnapshot=firebaseFirestore.


        // userProfile = findViewById(R.id.userProfile);
        driverClickedPicture = findViewById(R.id.driverClickedPicture);
        FullName = findViewById(R.id.name_M);
        phone = findViewById(R.id.phone_M);
        requestLocation = findViewById(R.id.address_M);
        cameraIcon = findViewById(R.id.camerIcon);
        text = findViewById(R.id.text);
        sendingCompletedReuest = findViewById(R.id.sendingComplain);
        callIcon = findViewById(R.id.call_icon);
        Toast.makeText(this, "" + markerID, Toast.LENGTH_SHORT).show();
        //   profile = findViewById(R.id.profile);


        FullName.setText(name);
        phone.setText(phoneNo);
        requestLocation.setText(address);
        //  Picasso.get().load(picture).into(userProfile);

        fAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();


        key = getIntent().getStringExtra("key");
        sendingCompletedReuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageToFirebase();
            }
        });

        ///////////call icon ///////////

        callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
                //Toast.makeText(MarkerInfo.this, ""+phoneNo, Toast.LENGTH_SHORT).show();
                //String mobile=documentSnapshot.getString("phoneNumber");
//               String call=phoneNo;
//               Intent i= new Intent(Intent.ACTION_DIAL);
//               i.setData(Uri.parse("03117124299");
//              startActivity(i);
//                String number = "03117124299";
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:" + number));//change the number
//                startActivity(callIntent);


            }
        });
    }


    public void Evidence(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, permmissionCode);
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_CODE);
//                }
//            }

        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_CODE);
            }
        }

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == permmissionCode) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE_CODE);
//            } else {
//                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap picture = (Bitmap) data.getExtras().get("data");
                driverClickedPicture.setImageBitmap(picture);


                // Toast.makeText(this, "1st : " + picture, Toast.LENGTH_SHORT).show();
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                picture.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                Uri path = Uri.parse(MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), picture, "Title", null));
                imageUri = path;
                Toast.makeText(this, "" + imageUri, Toast.LENGTH_SHORT).show();
                driverClickedPicture.setVisibility(View.VISIBLE);
                cameraIcon.setVisibility(View.GONE);
                text.setVisibility(View.GONE);
//                imageUri = getImageUri(getApplicationContext(), picture);
//
//                imageUri = data.getData();
//                captureImage.setImageURI(imageUri);
////                getImageUri(bitmap,imageUri);
//                 Bitmap bitmap=(Bitmap)data.getExtras().get("data");
//                captureImage.setImageBitmap(bitmap);
//                 imageUri = data.getData();
//                captureImage.setImageURI(imageUri);


            }
        }
    }


//    public void sendCompletedRequest() {
//        String status = "Completed";
//        DocumentReference documentReference = firebaseFirestore.collection("Complains").document(markerID);
//        Map<String, Object> complain = new HashMap<>();
//        complain.put("status", status);
//        complain.put("evidence", imgDown);
//        documentReference.update(complain).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(MarkerInfo.this, "done", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(getApplicationContext(), mapBox.class));
//                finish();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//                Toast.makeText(MarkerInfo.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
//            }
//
//
//        });
//
////    public void retreiveUserProfile() {
////        documentReference = firestore.collection("users").document(picture);
////        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
////            @Override
////            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
////                String userprofile = value.getString("Url");
////                Picasso.get().load(userprofile).into(userProfile);
////
////            }
////        });
////
////    }
//
//    }

    private void uploadImageToFirebase() {
        //String status = "Completed";
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please Wait...");
        progressDialog.show();
        final StorageReference fileRef = storageReference.child("Evidence").child(String.valueOf(System.currentTimeMillis() / 1000));

        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imgDown = uri.toString();
//                        sendCompletedRequest();

                        //Picasso.get().load(uri).into(captureImage);
                        progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MarkerInfo.this, "Failed to get URL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MarkerInfo.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("percentage" + (int) progressPercent + "%");

            }
        });
    }

    private void makePhoneCall() {
        String number = "03117124299" ;
        if (number.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(MarkerInfo.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MarkerInfo.this,
                        new String[]{Manifest.permission.CALL_PHONE}, 1);
            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            Toast.makeText(MarkerInfo.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}




