package com.adaeze.medicalchatbot.user;

import android.content.Intent;
import android.os.Bundle;

import com.adaeze.medicalchatbot.LoginMenu;
import com.adaeze.medicalchatbot.MainActivity;
import com.adaeze.medicalchatbot.R;
import com.adaeze.medicalchatbot.chatbot.Chatbot;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.valdesekamdem.library.mdtoast.MDToast;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mUsers;
    private KProgressHUD hud;
    private static String TAG = "MainActivity";
    private Handler handler;
    private Runnable r;
    private FirebaseUser cUsers;
    private String uid,email,names,phone,age;
    private TextView tv_welcome;
    private Button chat, meetDoctor;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_about, R.id.nav_services,
                R.id.nav_contact, R.id.nav_chat_history)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        try {
            mAuth=FirebaseAuth.getInstance();
            cUsers = mAuth.getCurrentUser();
            uid = cUsers.getUid();
            email=cUsers.getEmail();

            mUsers = FirebaseDatabase.getInstance().getReference().child("Users");

//            tv_welcome = findViewById(R.id.welcome_text);
//            chat = findViewById(R.id.bot_care);

            mUsers.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel profileModel = dataSnapshot.getValue(UserModel.class);
                    names = profileModel.getName();
                    phone =profileModel.getPhone();
                    age =profileModel.getAge();

                   // tv_welcome.setText("Hello, "+names);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }


        handler = new Handler();
        r = new Runnable() {

            @Override
            public void run() {

                Intent intent = new Intent(getApplicationContext(), LoginMenu.class);
                startActivity(intent);
                Log.d(TAG, "Logged out after 1 minute on inactivity.");
                finish();

                MDToast.makeText(HomePage.this, "Logged out after 1 minute on inactivity.",MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show();
            }
        };

//        chat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent medchat = new Intent(getApplicationContext(), Chatbot.class);
//                medchat.putExtra("names",names);
//
//                startActivity(medchat);
//            }
//        });

        startHandler();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
            Intent delete = new Intent(HomePage.this, UserLogin.class);
            delete.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            delete.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            delete.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(delete);

        } else if (id==R.id.action_profile){


            Intent my_profile = new Intent(HomePage.this, MyProfile.class);
            my_profile.putExtra("age",age);
            my_profile.putExtra("names",names);
            my_profile.putExtra("email",email);
            my_profile.putExtra("phone",phone);
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
