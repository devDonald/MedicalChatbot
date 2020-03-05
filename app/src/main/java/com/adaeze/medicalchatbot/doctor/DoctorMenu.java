package com.adaeze.medicalchatbot.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.adaeze.medicalchatbot.LoginMenu;
import com.adaeze.medicalchatbot.R;
import com.adaeze.medicalchatbot.user.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

public class DoctorMenu extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUsers, mChats;
    private FirebaseUser cUsers;
    private String uid, position, name, location, availability ;
    private TextView tv_welcome;
    private Handler handler;
    private Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_menu);


        tv_welcome = findViewById(R.id.doc_welcome_text);


        try {
            mAuth = FirebaseAuth.getInstance();
            cUsers = mAuth.getCurrentUser();
            uid = cUsers.getUid();

            mUsers = FirebaseDatabase.getInstance().getReference().child("Doctors");
            mChats = FirebaseDatabase.getInstance().getReference().child("Messages");

            mUsers.child(uid
            ).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DoctorModel profileModel = dataSnapshot.getValue(DoctorModel.class);
                    name =profileModel.getName();
                    position =profileModel.getPosition();
                    location =profileModel.getLocation();
                    availability =profileModel.getAvailability();

                    tv_welcome.setText("Welcome Doctor "+name+ " \nbellow are your Chat Appointments. Pls Chat them Up");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


        handler = new Handler();
        r = new Runnable() {

            @Override
            public void run() {

                Intent intent = new Intent(getApplicationContext(), LoginMenu.class);
                startActivity(intent);
                Log.d("Doctor Menu", "Logged out after 1 minute on inactivity.");
                finish();

                MDToast.makeText(DoctorMenu.this, "Logged out after 1 minute on inactivity.",MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show();
            }
        };

        startHandler();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.doc_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent delete = new Intent(DoctorMenu.this, DoctorLogin.class);
            delete.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            delete.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            delete.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(delete);

        } else if (id==R.id.action_profile){


            Intent my_profile = new Intent(DoctorMenu.this, DoctorProfile.class);
            my_profile.putExtra("name",name);
            my_profile.putExtra("position",position);
            my_profile.putExtra("avail",availability);
            my_profile.putExtra("location",location);

            startActivity(my_profile);
        }

        return super.onOptionsItemSelected(item);
    }

    public void stopHandler() {
        handler.removeCallbacks(r);
        Log.d("HandlerRun", "stopHandlerMain");
    }

    public void startHandler() {
        handler.postDelayed(r, 60 * 1000);
        Log.d("HandlerRun", "startHandlerMain");
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        stopHandler();
        startHandler();
    }

    @Override
    protected void onPause() {

        stopHandler();
        Log.d("onPause", "onPauseActivity change");
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        startHandler();

        Log.d("onResume", "onResume_restartActivity");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopHandler();
        Log.d("onDestroy", "onDestroyActivity change");

    }

}
