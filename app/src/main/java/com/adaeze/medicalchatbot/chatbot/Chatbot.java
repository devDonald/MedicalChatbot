package com.adaeze.medicalchatbot.chatbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adaeze.medicalchatbot.MainActivity;
import com.adaeze.medicalchatbot.R;
import com.adaeze.medicalchatbot.doctor.DoctorModel;
import com.adaeze.medicalchatbot.user.HomePage;
import com.adaeze.medicalchatbot.user.Meeting;
import com.adaeze.medicalchatbot.user.UserLogin;
import com.bumptech.glide.Glide;
import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.util.ChatBot;
import com.github.bassaer.chatmessageview.view.ChatView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Random;


public class Chatbot extends AppCompatActivity {
    private ChatView mChatView;
    private User me, you;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsers;
    private FirebaseUser cUsers;
    private String uid,email,names,phone,age;
    private Dialog popDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        popDialog = new Dialog(Chatbot.this);

        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            if (bundle != null) {

                names = bundle.getString("names");

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //User id
        String myId = "0";
        //User icon
        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.medcare);
        //User name
        String myName = "Me";

        String yourId = "1";
        Bitmap yourIcon = BitmapFactory.decodeResource(getResources(), R.drawable.medcare);
        String yourName = "MedCare";

        me = new User(myId, myName, myIcon);
        you = new User(yourId, yourName, yourIcon);

        mChatView = (ChatView)findViewById(R.id.chat_view);

        //Set UI parameters if you need
        mChatView.setRightBubbleColor(ContextCompat.getColor(this, R.color.green500));
        mChatView.setLeftBubbleColor(Color.WHITE);
        mChatView.setBackgroundColor(ContextCompat.getColor(this, R.color.blueGray500));
        mChatView.setSendButtonColor(ContextCompat.getColor(this, R.color.cyan900));
        mChatView.setSendIcon(R.drawable.ic_action_send);
        mChatView.setRightMessageTextColor(Color.WHITE);
        mChatView.setLeftMessageTextColor(Color.BLACK);
        mChatView.setUsernameTextColor(Color.WHITE);
        mChatView.setSendTimeTextColor(Color.WHITE);
        mChatView.setDateSeparatorColor(Color.WHITE);
        mChatView.setInputTextHint("new message...");
        mChatView.setMessageMarginTop(5);
        mChatView.setMessageMarginBottom(5);
        botResponse("Hello "+names+" Welcome! I am MedCare, Your online Assistant.");

        //Click Send Button
        mChatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new message
                Message message = new Message.Builder()
                        .setUser(me)
                        .setRight(true)
                        .setText(mChatView.getInputText())
                        .hideIcon(true)
                        .build();
                //Set to chat view
                mChatView.send(message);
                //Reset edit text


                String res = mChatView.getInputText().toLowerCase();
                if (res.contains("ok")||res.contains("hello")||res.contains("howdy")||res.contains("how far")||
                        res.contains("whats up")||res.contains("thank you")||res.contains("thanks")){
                    botResponse("How are you doing today?");

                }
                else if (res.contains("headaches")||res.contains("sweating")||res.contains("chills")||res.contains("pain")||
                        res.contains("shivering") || res.contains("sore")||res.contains("eye") ||res.contains("itching")
                        ||res.contains("weakness")||res.contains("headache")){
                    botResponse("It Appears you had a stressful day");
                    newDelay();
                    botResponse("I suggest you take a meal, freshen up and have some rest, \n Or would you like to speak with one of our Doctors online?");
                    newDelay();
                    mChatView.setInputTextHint("Eg. yes or no");

                }

                else if (res.contains("yes")||
                        res.contains("yes i want to")||res.contains("yes i will")){
                    mChatView.setInputText("");
                    botResponse("Thank you for having you TODAY");
                    showPopup(R.layout.layout_meet_doctor);

                }
                else if (res.contains("no")||res.contains("no thank you")
                        ||res.contains("no i don't")||res.contains("no i don't")){
                    botResponse("Thank you for having you TODAY, Bye!");
                    showPopup2(R.layout.layout_exit);

                }
                else if (res.contains("fine")||res.contains("bad")||res.contains("not good")||res.contains("sick")||
                        res.contains("feeling fine")||res.contains("not fine")||res.contains("terrible")){
                    botResponse("Please can you clearly state how you feel so that i can help you");
                    mChatView.setInputTextHint("Eg. Headache, cold, fever, itching eyes, blood in urine etc.");
                } else {
                    botResponse("Sorry your response are not clear, would you like to speak with one of our DOCTORS?");
                }

                mChatView.setInputText("");


            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void botResponse(String text){
        final Message receivedMessage = new Message.Builder()
                .setUser(you)
                .setRight(false)
                .hideIcon(false)
                .setUsernameVisibility(true)
                .setUserIconVisibility(true)
                .setText(text)
                .build();

        // This is a demo bot
        // Return within 3 seconds
        int sendDelay = (new Random().nextInt(2) + 1) * 1000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mChatView.receive(receivedMessage);
            }
        }, sendDelay);
    }

    public void showPopup(int view) {
        TextView txtclose;
        Button display;
        popDialog.setContentView(view);
        txtclose =popDialog.findViewById(R.id.txtclose);
        txtclose.setText("X");
        display = popDialog.findViewById(R.id.meet_doctor);
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toReg = new Intent(Chatbot.this, Meeting.class);
                toReg.putExtra("myName", names);
                toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                toReg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toReg);
            }
        });

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDialog.dismiss();
            }
        });
        popDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popDialog.show();
    }
    public void showPopup2(int view) {
        TextView txtclose;
        Button display;
        popDialog.setContentView(view);
        txtclose =popDialog.findViewById(R.id.txtclose2);
        txtclose.setText("X");
        display = popDialog.findViewById(R.id.close_section);
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toReg = new Intent(Chatbot.this, HomePage.class);
                toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                toReg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toReg);
            }
        });

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDialog.dismiss();
            }
        });
        popDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popDialog.show();
    }
    public void newDelay(){
        int sendDelay = (new Random().nextInt(3) + 1) * 1000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, sendDelay);
    }
}
