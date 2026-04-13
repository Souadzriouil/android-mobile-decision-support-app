package com.example.alomrane;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ButtonActivity extends AppCompatActivity {
ImageButton btnwelcome;
Button btnProjectsMaps,btn3;
TextView textView2;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buttonactivity);
        btnwelcome=findViewById(R.id.btnwelcome);
        btnProjectsMaps=findViewById(R.id.btnmaps);

        btn3=findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonActivity.this, FesActivity.class);
                startActivity(intent);
            }
        });

        btnProjectsMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ButtonActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });


        textView2=findViewById(R.id.textView2);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            textView2.setText("Welcome, " + name +" " + "to");
        }
        btnwelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToNextActivity();
            }
        });

    }
    private void sendUserToNextActivity() {
        Intent intent=new Intent(ButtonActivity.this,CircleDesign.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            // Rediriger l'utilisateur vers une autre activité ou afficher un message
        } else {
            super.onBackPressed();
        }
    }


}