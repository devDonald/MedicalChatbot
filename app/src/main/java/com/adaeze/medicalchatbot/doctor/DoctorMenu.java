package com.adaeze.medicalchatbot.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.adaeze.medicalchatbot.R;
import com.adaeze.medicalchatbot.user.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorMenu extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUsers;
    private FirebaseUser cUsers;
    private String uid, email, name;
    private TextView tv_welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_menu);


        tv_welcome = findViewById(R.id.doc_welcome_text);


        tv_welcome = findViewById(R.id.welcome_text);

        try {
            mAuth = FirebaseAuth.getInstance();
            cUsers = mAuth.getCurrentUser();
            uid = cUsers.getUid();
            email = cUsers.getEmail();

            mUsers = FirebaseDatabase.getInstance().getReference().child("Users");

            mUsers.child(uid
            ).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel profileModel = dataSnapshot.getValue(UserModel.class);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
