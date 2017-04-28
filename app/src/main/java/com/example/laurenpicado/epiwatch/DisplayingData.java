package com.example.laurenpicado.epiwatch;

import android.content.*;
import android.content.pm.InstrumentationInfo;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import android.support.v4.widget.SwipeRefreshLayout;




import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;


import static android.R.attr.data;
import static android.R.attr.value;
import static com.example.laurenpicado.epiwatch.R.layout.activity_displaying_data;


public class DisplayingData extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {


    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private static final String TAG = "DisplayingData";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private static final Random RANDOM = new Random();

    private final Handler mHandler = new Handler();
    private Runnable mTimer;
    private double graphLastXValue = 5d;
    private LineGraphSeries<DataPoint> mSeries;
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private List<Person> persons;
    private RecyclerView rv;



    public Integer st;
    private Integer mo;
    private Integer em;



    String one;
    String two;
    String three;


    TextView Stress;
    TextView Motion;
    TextView EMG;

    StringBuilder messages;
    private  String userID;
    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());



    //GraphView GraphView;
    //GraphView graph;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_displaying_data);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        Stress = (TextView) findViewById(R.id.Stress);
        Motion = (TextView) findViewById(R.id.Motion);
        EMG = (TextView) findViewById(R.id.EMG);

        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();


        

        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        initializeData();
        initializeAdapter();



        GraphView graph = (GraphView) findViewById(R.id.graph);
        //initGraph(graph);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(4);

        graph.getGridLabelRenderer().setLabelVerticalWidth(100);

        mSeries = new LineGraphSeries<>();
        mSeries.setDrawDataPoints(false);
        mSeries.setDrawBackground(false);
        mSeries1 = new LineGraphSeries<>();
        mSeries1.setDrawDataPoints(false);
        mSeries1.setDrawBackground(false);
        mSeries1.setColor(Color.RED);
        mSeries2 = new LineGraphSeries<>();
        mSeries2.setDrawDataPoints(false);
        mSeries2.setDrawBackground(false);
        mSeries2.setColor(Color.parseColor("#9400D3"));
        graph.addSeries(mSeries);
        graph.addSeries(mSeries1);
        graph.addSeries(mSeries2);


        messages = new StringBuilder();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //toastMessage("Successfully signed in with: " + user.getEmail());
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


                showData(dataSnapshot);







            }



            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });





    }

    private void initializeData(){
        persons = new ArrayList<>();
        //persons.add(new Person("Stress:"+"                                         "+timeStamp, "Motion:", "EMG:"));



    }


    private void initializeAdapter(){

        RVAdapter adapter = new RVAdapter(persons);
        rv.setAdapter(adapter);
        //adapter.notifyItemInserted(0);


    }

    private void showData(DataSnapshot dataSnapshot) {

        for(DataSnapshot ds :dataSnapshot.getChildren()){



                UserInformation uInfo = new UserInformation();

                uInfo.setContact_1(ds.child("Emergency_Contacts").getValue(UserInformation.class).getContact_1());
                uInfo.setContact_2(ds.child("Emergency_Contacts").getValue(UserInformation.class).getContact_2());
                uInfo.setContact_3(ds.child("Emergency_Contacts").getValue(UserInformation.class).getContact_3());


                Log.d(TAG, "showData" + uInfo.getContact_1());
                Log.d(TAG, "showData" + uInfo.getContact_2());
                Log.d(TAG, "showData" + uInfo.getContact_3());

                //one.append(uInfo.getContact_1());
                //two.append(uInfo.getContact_2());
                //three.append(uInfo.getContact_3());
                 one = uInfo.getContact_1().toString();
                 two = uInfo.getContact_2().toString();
                 three = uInfo.getContact_3().toString();


        }
    }



    private void sms() {



            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(one, null, "I am having a seizure" + " " + "at Lat:" + currentLatitude + " " + "Long:" + currentLongitude, null, null);
            smsManager.sendTextMessage(two, null, "I am having a seizure" + " " + "at Lat:" + currentLatitude + " " + "Long:" + currentLongitude, null, null);
            smsManager.sendTextMessage(three, null, "I am having a seizure" + " " + "at Lat:" + currentLatitude + " " + "Long:" + currentLongitude, null, null);



    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");
            text = text.replaceAll("\\r\\n", "");


            try {


                StringTokenizer tokens = new StringTokenizer(text, ",");
                String first = tokens.nextToken();
                String second = tokens.nextToken();
                String third = tokens.nextToken();


                st = Integer.valueOf(first);
                mo = Integer.valueOf(second);
                em = Integer.valueOf(third);

                if (!text.equals("")) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();
                    //messages.append(text+"\n");

                    myRef.child(userID).child("Stress").setValue(first);
                    myRef.child(userID).child("Motion").setValue(second);
                    myRef.child(userID).child("EMG").setValue(third);

                    //toastMessage("Adding " + text + " to database...");

                    Stress.setText(first);
                    Motion.setText(second);
                    EMG.setText(third);



                }

                if (st >= 89 && mo >= 69 && em >= 70) {
                    //Log.d(TAG, "show st: " + st);
                    //Log.d(TAG, "show mo: "+ mo);
                    //Log.d(TAG, "show em: "+ em);
                    sms();
                    persons.add(new Person("Stress:"+ st+"                                     "+timeStamp, "Motion:" + mo, "EMG:" + em));
                }else{
                    return;
                }
            } catch (NoSuchElementException e){
                return;
            }



            //Intent i = new Intent(BluetoothActivity.this, DisplayingData.class); //transfering Data
            //startActivity(i);

            //reset the text

        }


        //incomingmessages.setText(messages);
    };






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

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
        mTimer = new Runnable() {
            @Override
            public void run() {

                try {
                    graphLastXValue += 0.25d;
                    mSeries.appendData(new DataPoint(graphLastXValue, st), true, 22);
                    mSeries1.appendData(new DataPoint(graphLastXValue, mo), true, 22);
                    mSeries2.appendData(new DataPoint(graphLastXValue, em), true, 22);
                    mHandler.postDelayed(this, 330);


                }catch(NoSuchElementException e){

                }
            }
        };
        mHandler.postDelayed(mTimer, 1500);


    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mTimer);

        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            Toast.makeText(this,"Lat:"+ currentLatitude + " Long:" + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        Toast.makeText(this,"Lat:"+ currentLatitude + " Long:" + currentLongitude + "", Toast.LENGTH_LONG).show();
    }








    double mLastRandom = 56;
    Random mRand = new Random();
    private double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
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