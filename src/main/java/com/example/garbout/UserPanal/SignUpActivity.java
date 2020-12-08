package com.example.garbout.UserPanal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    TextInputLayout user_name, user_mail, user_number, user_password, user_confirm_password;
    Button registerBtn, loginBtn;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String user_id,role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseFirestore = FirebaseFirestore.getInstance();
        user_name = findViewById(R.id.userName);
        user_mail = findViewById(R.id.userEmail);
        user_number = findViewById(R.id.userPhoneNo);
        user_password = findViewById(R.id.userPassword);
        user_confirm_password = findViewById(R.id.userConfirmPassword);
        registerBtn = findViewById(R.id.btn_register);
        loginBtn = findViewById(R.id.btn_Login);
        firebaseAuth = FirebaseAuth.getInstance();
        role=getIntent().getStringExtra("role");
        Toast.makeText(this, ""+role, Toast.LENGTH_SHORT).show();



        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignUpActivity.this, "Please Wait", Toast.LENGTH_SHORT).show();
/*
        if (!validateName() | !validateEmail() | !validatePhoneNo() | !validatePassword() | !validateConfirmPassword()) {
                    return;
                }

 */

                //getting values
                final String Name = user_name.getEditText().getText().toString();
                final String Email = user_mail.getEditText().getText().toString();
                final String PhoneNo = user_number.getEditText().getText().toString();
                final String password = user_password.getEditText().getText().toString();
                firebaseAuth.createUserWithEmailAndPassword(Email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user_id = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = firebaseFirestore.collection("users").document(user_id);
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", Name);
                            user.put("mail", Email);
                            user.put("phoneNumber", PhoneNo);
                            user.put("password", password);
                            if (role.equals("admin")) {
                                user.put("isDriver", "2");
                            }
                            if (role.equals("user")){
                                user.put("isUser", "1");
                            }
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if (role.equals("admin")) {
                                        Toast.makeText(SignUpActivity.this, "Driver has been created", Toast.LENGTH_SHORT).show();
                                    }
                                        if(role.equals("user")) {
                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
//                                    checkUser();



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity.this, "Failed Account Creation", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(SignUpActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }

                    }

                });


            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    /*
         private Boolean validateName() {
            String val = user_name.getEditText().getText().toString();

            if (val.isEmpty()) {
                user_name.setError("Field cannot be empty");
                return false;
            } else {
                user_name.setError(null);
                user_name.setErrorEnabled(false);
                return true;
            }
        }

        private Boolean validateEmail() {
            String val = user_mail.getEditText().getText().toString();
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";
            if (val.isEmpty()) {
                user_mail.setError("Field cannot be empty");
                return false;
            } else if (!val.matches(emailPattern)) {
                user_mail.setError("Invalid email address");
                return false;
            } else {
                user_mail.setError(null);
                user_mail.setErrorEnabled(false);
                return true;
            }
        }

        private Boolean validatePhoneNo() {
            String val = user_number.getEditText().getText().toString();

            if (val.isEmpty()) {
                user_number.setError("Field cannot be empty");
                return false;
            } else {
                user_number.setError(null);
                user_number.setErrorEnabled(false);
                return true;
            }
        }

        private Boolean validatePassword() {
            String val = user_password.getEditText().getText().toString();
                  if (val.isEmpty()) {
                user_password.setError("Field cannot be empty");
                return false;
            }
            else {
                user_password.setError(null);
                      user_password.setErrorEnabled(false);
                return true;
            }
        }

        private Boolean validateConfirmPassword() {
            String val = user_password.getEditText().getText().toString();
            String passwordVal = "^" +
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=/S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    '$';

            if (val.isEmpty()) {
                user_password.setError("Field cannot be empty");
                return false;
            } else if (!val.matches(passwordVal)) {
                user_password.setError("Password is too weak");
                return false;
            } else {
                user_password.setError(null);
                user_confirm_password.setErrorEnabled(false);
                return true;
            }
        }

     */
    private void checkUser() {

        final DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getString("isAdmin") != null) {
                      Toast.makeText(SignUpActivity.this, "User added", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), Admin.class));
                }
                if (documentSnapshot.getString("isUser") != null) {

                    //  Toast.makeText(LoginActivity.this, "User", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}