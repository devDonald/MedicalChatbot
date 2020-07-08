package com.adaeze.medicalchatbot.user.ui.chat_history;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.adaeze.medicalchatbot.R;
import com.adaeze.medicalchatbot.user.HomePage;
import com.adaeze.medicalchatbot.user.UserLogin;
import com.adaeze.medicalchatbot.user.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

public class ChatPopUp1 extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mUsers;
    private FirebaseUser currentUser;
    private String onlineAns;
    private TextView txtclose;
    private Button submit;
    private EditText sec_answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chat_pop_up1);
        mAuth = FirebaseAuth.getInstance();
        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUser = mAuth.getCurrentUser();


        txtclose = findViewById(R.id.txtclose1);
        sec_answer =findViewById(R.id.confirm_sec_answer);
        txtclose.setText("X");

        submit = findViewById(R.id.btn_submit_answer);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String subAns = sec_answer.getText().toString().trim();
                if (!subAns.isEmpty()){
                    if (currentUser!=null){
                        mUsers.child(mAuth.getUid().toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                UserModel model = dataSnapshot.getValue(UserModel.class);
                                onlineAns = model.getSecurityAnswer();

                                if (onlineAns.equalsIgnoreCase(subAns)){
                                    Intent toDocHistory = new Intent(ChatPopUp1.this, DoctorHistory.class);
                                    toDocHistory.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    toDocHistory.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(toDocHistory);
                                } else {
                                    MDToast.makeText(ChatPopUp1.this,"Incorrect security answer, pls try again",
                                            MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                databaseError.getMessage();
                            }
                        });
                    }
                }

            }
        });

    }


}
