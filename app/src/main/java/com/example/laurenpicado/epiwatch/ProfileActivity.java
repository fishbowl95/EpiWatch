package com.example.laurenpicado.epiwatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private TextView textViewUserEmail;
    private Button buttonLogout;
    private Button buttonConnectEpiWatch;
    private Button buttonEmergencyContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        buttonEmergencyContacts = (Button) findViewById(R.id.EmergencyContacts);



        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);


        textViewUserEmail.setText("Welcome " +user.getEmail());


        buttonLogout.setOnClickListener(this);
        buttonConnectEpiWatch = (Button) findViewById(R.id.buttonConnectEpiWatch);

        buttonConnectEpiWatch.setOnClickListener(this);
        buttonEmergencyContacts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
               Intent intent = new Intent(ProfileActivity.this, AddToDataBase.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onClick(View view){

        if(view == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if(view == buttonConnectEpiWatch){
            startActivity(new Intent(this,BluetoothActivity.class));
            //startActivity(new Intent(getApplicationContext(),SMS.class));
        }



    }



}
