package com.example.laurenpicado.epiwatch;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

;

/**
 * Created by laurenpicado on 4/7/17.
 */


public class AddToDataBase extends AppCompatActivity {

    private static final String TAG = "AddToDataBase";
    private Button mAddToDB;
    private EditText mNewContact1;
    private EditText mNewContact2;
    private EditText mNewContact3;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothDevice mBTDevice;
    BluetoothConnection mBluetoothConnection;
    StringBuilder messages;


    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    BluetoothConnection InputStream;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_to_database_layout);

        //declare varaibles

        mAddToDB = (Button) findViewById(R.id.btnaddnewcontact);
        mNewContact1 = (EditText) findViewById(R.id.emer_contacts1);
        mNewContact2 = (EditText) findViewById(R.id.emer_contacts2);
        mNewContact3 = (EditText) findViewById(R.id.emer_contacts3);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //databaseStress = FirebaseDatabase.getInstance().getReference("Stress");
        myRef = mFirebaseDatabase.getReference();
        messages = new StringBuilder();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        mAddToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to add object to database.");
                String newContact1 = mNewContact1.getText().toString();
                String newContact2 = mNewContact2.getText().toString();
                String newContact3 = mNewContact3.getText().toString();

                if (!newContact1.equals("")) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();
                    //databaseStress.push().getKey();
                    myRef.child(userID).child("Emergency_Contacts").child("Contact_1").setValue(newContact1);
                    toastMessage("Adding " + newContact1 + " to database...");
                    //reset the text
                    mNewContact1.setText("");
                }

                if (!newContact2.equals("")) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();
                    //databaseStress.push().getKey();
                    myRef.child(userID).child("Emergency_Contacts").child("Contact_2").setValue(newContact2);
                    toastMessage("Adding " + newContact2 + " to database...");
                    //reset the text
                    mNewContact2.setText("");
                }
                if (!newContact3.equals("")) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();
                    //databaseStress.push().getKey();
                    myRef.child(userID).child("Emergency_Contacts").child("Contact_3").setValue(newContact3);
                    toastMessage("Adding " + newContact3 + " to database...");
                    //reset the text
                    mNewContact3.setText("");
                }
            }
        });
    }

        @Override
        public void onStart(){
            super.onStart();
            mAuth.addAuthStateListener(mAuthListener);
        }
        @Override
        public void onStop(){
            super.onStop();
            if(mAuthListener != null){
                mAuth.removeAuthStateListener(mAuthListener);
            }
        }



        //add a toast to show when successfully signed in
        /**
         * customizable toast
         * @param message
         */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

    }



}



