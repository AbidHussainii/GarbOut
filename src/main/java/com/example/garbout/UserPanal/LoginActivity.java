package com.example.garbout.UserPanal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbout.Admin.Admin;
import com.example.garbout.DriverPanal.DriverDashboard;
import com.example.garbout.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    TextView textView;
    TextInputLayout userMail, userPassword;
    Button login_btn;
    TextView tv_Forgetpassword;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    LinearLayout signUpContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userMail = findViewById(R.id.mail_user);
        userPassword = findViewById(R.id.password_user);
        login_btn = findViewById(R.id.btn_login);
        tv_Forgetpassword = findViewById(R.id.forgetPassword);
        progressBar = findViewById(R.id.progressBar);
        signUpContainer = findViewById(R.id.signUpContainer);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#000000"));
                pDialog.setTitle("Please Wait");
                pDialog.setTitleText("Loading ...");
                pDialog.setCancelable(true);
                pDialog.show();


//                if (!validateEmail() | !validatePassword()) {
//                    return;
//
//                }
                //  progressBar.setVisibility(View.VISIBLE);
//                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
//                progressDialog.setTitle("please Wait...");
//                progressDialog.show();

                final String Email = userMail.getEditText().getText().toString().trim();
                final String password = userPassword.getEditText().getText().toString().trim();
                firebaseAuth.signInWithEmailAndPassword(Email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "SuccessFull", Toast.LENGTH_SHORT).show();
                            checkUser();
                            pDialog.dismiss();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this, "Inavlid Password", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }
                        }
                        // progressDialog.dismiss();
                    }
                });
                // signUpContainer.setVisibility(View.GONE);

            }
        });

    }

    private void checkUser() {

        final DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getString("isAdmin") != null) {
                    Toast.makeText(LoginActivity.this, "Admin", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), Admin.class));
                    finish();
                } else if (documentSnapshot.getString("isDriver") != null) {
                    startActivity(new Intent(getApplicationContext(), DriverDashboard.class));
                    Toast.makeText(LoginActivity.this, "Driver", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (documentSnapshot.getString("isUser") != null) {
                    Toast.makeText(LoginActivity.this, "User", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }

        });
    }

    public void moveToSignUp(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        String var="user";
        intent.putExtra("role",var);
        startActivity(intent);
        finish();
    }

    public void forgetPassword(View view) {
        final EditText resetMail = new EditText(view.getContext());
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
        passwordResetDialog.setTitle("Reset Password..?");
        passwordResetDialog.setMessage("Enter your Mail to Received reset Link ");
        passwordResetDialog.setView(resetMail);
        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail = resetMail.getText().toString();
                firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(LoginActivity.this, "Reset lint sent to your GMail", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Reset lint sent to your Mail" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        passwordResetDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        passwordResetDialog.create().show();
    }

    public void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
            Toast.makeText(this, " WIFI Enabled", Toast.LENGTH_SHORT).show();
        } else {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(this, " Data Network Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sorry no Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }


    }

    public void admin() {
        Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
        final DocumentReference documentReference = firebaseFirestore.collection("Admins").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getString("isAdmin") != null) {
                    Toast.makeText(LoginActivity.this, "Admin", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), Admin.class));
                }
            }
        });
    }
    private Boolean validateEmail() {
        String val = userMail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";
        if (val.isEmpty()) {
            userMail.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            userMail.setError("Invalid email address");
            return false;
        } else {
            userMail.setError(null);
            userMail.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validatePassword() {
        String val = userPassword.getEditText().getText().toString();
        if (val.isEmpty()) {
            userPassword.setError("Field cannot be empty");
            return false;
        }
        else {
            userPassword.setError(null);
            userPassword.setErrorEnabled(false);
            return true;
        }
    }
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }
}

