package com.example.garbout.UserPanal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.garbout.DriverPanal.DriverTask;
import com.example.garbout.DriverPanal.UserReceivedRequest;
import com.example.garbout.R;

public class complainStatus extends AppCompatActivity {
    View line,line1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_status);
        line=findViewById(R.id.line);
        line1=findViewById(R.id.line1);

    }
    public void ongoing(View view) {
        Intent intent = new Intent(this, DriverTask.class).putExtra("status", "Completed");
        startActivity(intent);
        line.setVisibility(View.VISIBLE);
        line1.setVisibility(View.GONE);

    }
    public void pending(View view) {
        Intent intent = new Intent(this, UserReceivedRequest.class).putExtra("status", "Pending");
        startActivity(intent);
        line1.setVisibility(View.VISIBLE);
        line.setVisibility(View.GONE);

    }


}