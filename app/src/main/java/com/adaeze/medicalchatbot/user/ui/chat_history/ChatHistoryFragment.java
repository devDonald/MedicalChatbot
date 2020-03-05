package com.adaeze.medicalchatbot.user.ui.chat_history;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adaeze.medicalchatbot.MessageModel;
import com.adaeze.medicalchatbot.R;
import com.adaeze.medicalchatbot.doctor.AppointmentChat;
import com.adaeze.medicalchatbot.user.UserModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatHistoryFragment extends Fragment {

    private ChatHistoryViewModel chatHistoryViewModel;
    private RecyclerView chatHistory;
    private DatabaseReference usersRef, usersChat;
    private FirebaseAuth mAuth;
    private String current_user_id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatHistoryViewModel =
                ViewModelProviders.of(this).get(ChatHistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_chat_history, container, false);
        chatHistory = root.findViewById(R.id.chat_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        chatHistory.setLayoutManager(layoutManager);

        mAuth=FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersChat = FirebaseDatabase.getInstance().getReference().child("Messages").child(current_user_id);
        return root;
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
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<MessageModel>()
                        .setQuery(usersChat,MessageModel.class)
                        .build();

        FirebaseRecyclerAdapter<MessageModel,HistoryHolder> adapter =
                new FirebaseRecyclerAdapter<MessageModel, HistoryHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final HistoryHolder holder, int position, @NonNull final MessageModel model) {

                        final String chatId = getRef(position).getKey();
                        usersRef.child(chatId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final UserModel model1 = dataSnapshot.getValue(UserModel.class);

                                holder.historyName.setText(model1.getName());
                                if (dataSnapshot.hasChild("photoUrl")){
                                    Glide.with(getContext())

                                            .load(model1.getPhotoUrl())
                                            .into(holder.rvUserImage);
                                }

                                holder.itemView.findViewById(R.id.lay_chat_history).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent private_chat = new Intent(getContext(), AppointmentChat.class);
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
}