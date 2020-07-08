package com.adaeze.medicalchatbot.user.ui.chat_history;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adaeze.medicalchatbot.MessageModel;
import com.adaeze.medicalchatbot.R;
import com.adaeze.medicalchatbot.doctor.AppointmentChat;
import com.adaeze.medicalchatbot.user.HomePage;
import com.adaeze.medicalchatbot.user.UserLogin;
import com.adaeze.medicalchatbot.user.UserModel;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorHistory extends AppCompatActivity {
    private RecyclerView chatHistory;
    private DatabaseReference usersRef, usersChat;
    private FirebaseAuth mAuth;
    private String current_user_id;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_history);

        chatHistory = findViewById(R.id.doc_history_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(DoctorHistory.this);
        chatHistory.setLayoutManager(layoutManager);

        mAuth=FirebaseAuth.getInstance();
        current_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersChat = FirebaseDatabase.getInstance().getReference().child("Messages").child(current_user_id);
    }

    public static class HistoryHolder extends RecyclerView.ViewHolder{
        TextView historyName,historyChat;
        CircleImageView rvUserImage;

        public HistoryHolder(View itemView) {
            super(itemView);

            historyName = itemView.findViewById(R.id.chat_history_name);
            historyChat = itemView.findViewById(R.id.chat_history_chat);
            rvUserImage = itemView.findViewById(R.id.chat_history_image);

        }


    }

    public void myAdapter(){
        FirebaseRecyclerOptions<MessageModel> options =
                new FirebaseRecyclerOptions.Builder<MessageModel>()
                        .setQuery(usersChat,MessageModel.class)
                        .build();

        FirebaseRecyclerAdapter<MessageModel,HistoryHolder> adapter =
                new FirebaseRecyclerAdapter<MessageModel, HistoryHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final HistoryHolder holder, int position, @NonNull final MessageModel model) {

                        final String chatId = getRef(position).getKey();
                        assert chatId != null;
                        usersRef.child(chatId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final UserModel model1 = dataSnapshot.getValue(UserModel.class);

                                holder.historyName.setText(model1.getName());
                                if (dataSnapshot.hasChild("photoUrl")){
                                    Glide.with(DoctorHistory.this)

                                            .load(model1.getPhotoUrl())
                                            .into(holder.rvUserImage);
                                }

                                holder.itemView.findViewById(R.id.lay_chat_history).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent private_chat = new Intent(DoctorHistory.this, AppointmentChat.class);
                                        private_chat.putExtra("receiver_id",chatId);
                                        private_chat.putExtra("receiver_name", model1.getName());
                                        startActivity(private_chat);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_history,parent,false);

                        return new HistoryHolder(view);
                    }
                };

        adapter.notifyDataSetChanged();
        chatHistory.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        myAdapter();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent toReg = new Intent(DoctorHistory.this, HomePage.class);
        toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        toReg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toReg);
        finish();
    }
}
