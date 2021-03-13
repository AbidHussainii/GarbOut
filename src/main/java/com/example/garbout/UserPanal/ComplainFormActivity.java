package com.example.garbout.UserPanal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.garbout.DriverPanal.DriverDashboard;
import com.example.garbout.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ComplainFormActivity extends AppCompatActivity {
    ImageView captureImage;
    Button sendForm, chose;
    TextInputLayout etYourName, etPhone, etDetailReport;

    public int REQUEST_IMAGE_CAPTURE_CODE = 103;
    public int permmissionCode = 22;
    private Bitmap capturedImage;
    private ProgressBar mProgressBar;
    String userId;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String imgDown;
    String userProfile;
    String UserName;
    private Uri imageUri, picUri;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    DocumentReference documentReference;
    String userAddress, lat, lan;
    SweetAlertDialog pDialog;
//    Double lat;
//    Double lan;lan


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_complain);
        captureImage = findViewById(R.id.imageCapture);
        sendForm = findViewById(R.id.btn_send);
        mProgressBar = findViewById(R.id.progress_bar);
        etDetailReport = findViewById(R.id.DetailReport);
        etYourName = findViewById(R.id.C_YourName);
        etPhone = findViewById(R.id.C_PhoneNo);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("Complains");
        userAddress = getIntent().getStringExtra("Address");
        lat = String.valueOf(getIntent().getDoubleExtra("lat", 0));
        lan = String.valueOf(getIntent().getDoubleExtra("lan", 0));
        //  geoPoint = new GeoPoint(lat,lan);

        Toast.makeText(this, "" + "" + lan, Toast.LENGTH_SHORT).show();
        //onClick method of send button...
        sendForm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                uploadImageToFirebase();
//                String name = etYourName.getEditText().getText().toString();
//                String phone = etYourName.getEditText().getText().toString();
//                String detail = etYourName.getEditText().getText().toString();
//                if (TextUtils.isEmpty(name)) {
//                    etYourName.setError("Please enter your name");
//                    etYourName.requestFocus();
//                }
//                if (TextUtils.isEmpty(phone)) {
//                    etPhone.setError("Please enter your phoneNo");
//                    etPhone.requestFocus();
//                }
//                if (TextUtils.isEmpty(detail)) {
//                    etDetailReport.setError("Please enter your Complain Detail");
//                    etDetailReport.requestFocus();
//                }
//                if (TextUtils.isEmpty(name) && TextUtils.isEmpty(phone)) {
//                    etYourName.setError("Please enter your name");
//                    etPhone.setError("Please enter your phoneNo");
//                    etYourName.requestFocus();
//                }
//                if (TextUtils.isEmpty(name) && TextUtils.isEmpty(detail)) {
//                    etYourName.setError("Please enter your name");
//                    etDetailReport.setError("Please enter your  Complain Detain");
//                    etYourName.requestFocus();
//                }
//                if (TextUtils.isEmpty(detail) && TextUtils.isEmpty(phone)) {
//                    etDetailReport.setError("Please enter your  Complain Detain");
//                    etPhone.setError("Please enter your phoneNo");
//                    etPhone.requestFocus();
//                }
//                if (TextUtils.isEmpty(detail) && TextUtils.isEmpty(phone) && TextUtils.isEmpty(name)){
//                    etYourName.setError("Please enter your name");
//                    etDetailReport.setError("Please enter your  Complain Detain");
//                    etPhone.setError("Please enter your phoneNo");
//                    etPhone.requestFocus();
//                }
//                if (!(TextUtils.isEmpty(detail) && TextUtils.isEmpty(phone) && TextUtils.isEmpty(name))) {
//                    uploadImageToFirebase();
//                }


            }

        });
        retreivingProfile();

    }


    //upload image to firebase
    private void uploadImageToFirebase() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please Wait...");
        progressDialog.show();
        if (captureImage.getDrawable() == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(ComplainFormActivity.this);
            builder.setTitle("Image Not Exist");
            builder.setMessage("You can not Post Complain a WithOut Complain Evidence!!");
            //builder.setMessage("Deleting this account will result in completely removing your account?");
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();

            progressDialog.dismiss();
        }
        else{
            //Image Exists!.
            final StorageReference fileRef = storageReference.child("Complain Pic").child(userId).child(String.valueOf(System.currentTimeMillis() / 1000));
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imgDown = uri.toString();
                            getDownload();
                            Toast.makeText(ComplainFormActivity.this, "Url : " + uri, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), UserComplains.class));

                            //Picasso.get().load(uri).into(captureImage);
                            // pDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ComplainFormActivity.this, "Failed to get URL", Toast.LENGTH_SHORT).show();
                            // pDialog.dismiss();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ComplainFormActivity.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("percentage" + (int) progressPercent + "%");
                   // progressDialog.dismiss();

                }

            });

        }


    }

    public void getDownload() {


        documentReference = firebaseFirestore.collection("users").document(userId);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    UserName = documentSnapshot.getString("name");
                    uploadComplain();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ComplainFormActivity.this, "Something Wents Wrong...", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void uploadComplain() {
        final String PhoneNo;
        final String Detail;

        final Calendar[] calendar = {Calendar.getInstance()};
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar[0].getTime());
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("HH:MM:aa");
        String time = format.format(calendar[0].getTime());

        //UserName = etYourName.getEditText().getText().toString();
        PhoneNo = etPhone.getEditText().getText().toString();
        Detail = etDetailReport.getEditText().getText().toString();
        String status = "Pending";
        // upload upload=new upload(UserName,Detail,currentDate,imgDown,time,userProfile,userAddress,PhoneNo,userId,lat,lan,null,status);

        Map<String, Object> complain = new HashMap<>();
        complain.put("UserName", UserName);
        complain.put("PhoneNo", PhoneNo);
        complain.put("complainAddress", userAddress);
        complain.put("Detail", Detail);
        complain.put("Url1", imgDown);
        complain.put("Date", currentDate);
        complain.put("time", time);
        complain.put("UserID", userId);
        complain.put("Url", userProfile);
        complain.put("status", status);

        // complain.put("Locations",geoPoint);
        complain.put("userLat", lat);
        complain.put("userLan", lan);
        complain.put("userProfileUrl", userProfile);
        collectionReference.add(complain).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(ComplainFormActivity.this, "Done", Toast.LENGTH_SHORT).show();
                new Intent(getApplicationContext(), UserComplains.class);

               // pDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ComplainFormActivity.this, "Failed", Toast.LENGTH_SHORT).show();
               // pDialog.dismiss();
            }
        });


    }


    public void accessCamera(View view) {
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



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permmissionCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE_CODE);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap picture = (Bitmap) data.getExtras().get("data");
                captureImage.setImageBitmap(picture);

                Toast.makeText(this, "1st : " + picture, Toast.LENGTH_SHORT).show();
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                picture.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                Uri path = Uri.parse(MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), picture, "Title", null));
                imageUri = path;


            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Toast.makeText(this, "2nd : " + inImage, Toast.LENGTH_SHORT).show();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        // Toast.makeText(inContext, " " + path, Toast.LENGTH_SHORT).show();
        return Uri.parse(path);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1000) {
//            if (resultCode == Activity.RESULT_OK) {
//                imageUri = data.getData();
//                captureImage.setImageURI(imageUri);
//                // Bitmap bitmap=(Bitmap)data.getExtras().get("data");
//                //captureImage.setImageBitmap(bitmap);
//            }
//        }
//
//    }


    public void backArrow(View view) {
        Intent intent = new Intent(this, UserMap.class);
        startActivity(intent);
    }
    public void retreivingProfile() {
        documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                userProfile = value.getString("Url");
                Toast.makeText(ComplainFormActivity.this, "" + userProfile, Toast.LENGTH_SHORT).show();

            }
        });

    }
}



