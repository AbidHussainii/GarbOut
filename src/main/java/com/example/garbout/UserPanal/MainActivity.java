package com.example.garbout.UserPanal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbout.BuildConfig;
import com.example.garbout.DriverPanal.DriverTask;
import com.example.garbout.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ImageView accountImage;
    NavigationView navigationView;
    TextView userHeaderName, userHeaderEmail;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    String userProfile;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        accountImage = findViewById(R.id.accountImage);
        userHeaderName = findViewById(R.id.userHeadername);
        userHeaderEmail = findViewById(R.id.userHeadermail);
        navigationView = findViewById(R.id.navigation);
        drawerLayout = findViewById(R.id.drawer_layout);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        retrieveImageFromFirebase();
        //setup toolbar
        setUpToolBar();

    }


    public void setUpToolBar() {
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void post_complain(View view) {
        Intent intent = new Intent(MainActivity.this, UserMap.class);
        startActivity(intent);
        finish();

    }

    public void my_complain(View view) {
        startActivity(new Intent(getApplicationContext(),UserComplains.class));


    }

    public void about_us(View view) {


    }

    public void contactUs(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,"contact us");
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }


    }

    public void myAccount(View view) {
        Intent intent=new Intent(getApplicationContext(),UserProfile.class);
        intent.putExtra("role","user");
        startActivity(intent);
    }

    private void retrieveImageFromFirebase() {
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();

                    userProfile = documentSnapshot.getString("Url");
                    Picasso.get().load(userProfile).into(accountImage);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Something Wents Wrong...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.nav_home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_account:
                intent = new Intent(this, UserProfile.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_complainStatus:
                intent = new Intent(this, UserComplains.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_logout:
                fAuth.signOut();
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_shareApp:
                try {

                    //ApplicationInfo applicationInfo=getApplicationContext().getApplicationInfo();
                    //String apkPath=applicationInfo.sourceDir;
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "shareDemo");
                    String shareMessage = "http://play.google.com/store/apps/details?=" + BuildConfig.APPLICATION_ID + "/n/n";
                    intent.putExtra(intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(intent, "ShareVia"));
                } catch (Exception e) {
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                    break;
                }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        setResult(RESULT_OK, new Intent().putExtra("EXIT", true));
                        finish();
                    }

                }).create().show();
    }
}
