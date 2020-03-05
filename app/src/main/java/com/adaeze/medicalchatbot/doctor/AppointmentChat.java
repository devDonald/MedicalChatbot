package com.adaeze.medicalchatbot.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.adaeze.medicalchatbot.MessageModel;
import com.adaeze.medicalchatbot.R;
import com.adaeze.medicalchatbot.adapters.MessageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class AppointmentChat extends AppCompatActivity {
    private DatabaseReference rootRef, reference;
    private StorageReference filesStorage,storageReference;
    private TextView tv_receiver_name;
    private RecyclerView mUsersRecycler;
    private String receiver_id,receiver_name,chat_message,sender_id;
    private EmojiconEditText emojiconEditText;
    private ImageView emojiImageView;
    private View rootView;
    private FirebaseAuth mAuth;
    private EmojIconActions emojIcon;
    private ImageButton submitButton;
    private final List<MessageModel> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private Intent intent;
    private Uri imageUri =null;
    private Uri pdfUri;
    private boolean notify = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_chat);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        tv_receiver_name = toolbar.findViewById(R.id.nameTextView);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        receiver_id = getIntent().getExtras().getString("receiver_id");
        receiver_name = getIntent().getExtras().getString("receiver_name");
        rootView = findViewById(R.id.root_view);
        tv_receiver_name.setText(receiver_name);
        rootRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        sender_id = mAuth.getCurrentUser().getUid();

        emojiImageView = findViewById(R.id.emoji_btn);
        submitButton = findViewById(R.id.send_message_btn);
        emojiconEditText = findViewById(R.id.emojicon_edit_text);

        emojIcon = new EmojIconActions(this, rootView, emojiconEditText, emojiImageView);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard Opened", "Keyboard opened!");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard Closed", "Keyboard closed");
            }
        });

        messageAdapter = new MessageAdapter(messagesList);
        mUsersRecycler = findViewById(R.id.private_chat_messages);
        linearLayoutManager = new LinearLayoutManager(this);
        mUsersRecycler.setLayoutManager(linearLayoutManager);
        mUsersRecycler.setAdapter(messageAdapter);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    public void retrieveMessages(){
        rootRef.child("Messages").child(sender_id).child(receiver_id)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                        try {

                            MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);

                            messagesList.add(messageModel);
                            messageAdapter.notifyDataSetChanged();
                            mUsersRecycler.smoothScrollToPosition(mUsersRecycler.getAdapter().getItemCount());

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });

    }

    public void sendMessage(){
        notify=true;
        chat_message= emojiconEditText.getText().toString();
        emojiconEditText.setText(" ");
        if (chat_message.isEmpty()){

        } else if(!chat_message.isEmpty()) {
            String messageSenderRef = "Messages/" + sender_id + "/" + receiver_id;
            String messageReceiverRef = "Messages/" + receiver_id + "/" + sender_id;

            DatabaseReference userMessageKeyRef = rootRef.child("Messages")
                    .child(sender_id).child(receiver_id).push();

            String messagePushId = userMessageKeyRef.getKey();
            String saveCurrentTime, saveCurrentDate;

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            saveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
            saveCurrentTime = currentTime.format(calendar.getTime());

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", chat_message);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", sender_id);
            messageTextBody.put("to", receiver_id);
            messageTextBody.put("date", saveCurrentDate);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("seen", false);


            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messageTextBody);
            rootRef.updateChildren(messageBodyDetails)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                emojiconEditText.setText(" ");
                            }
                        }
                    });

        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveMessages();
    }
}
