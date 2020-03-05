package com.adaeze.medicalchatbot.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adaeze.medicalchatbot.MessageModel;
import com.adaeze.medicalchatbot.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter <MessageAdapter.MessageViewHolder>{

    private List<MessageModel> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef, onlineRef;
    private String state;
    private Context context;

    public MessageAdapter(List<MessageModel> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_message_layout, parent, false);

        context = parent.getContext();
        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        String messageSenderId = mAuth.getCurrentUser().getUid();
        final MessageModel messages = userMessagesList.get(position);

        String fromUserId = messages.getFrom();
        String toUserId = messages.getTo();

        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);


        try {
            if (fromMessageType.equals("text")){
                holder.receiverLinear.setVisibility(View.INVISIBLE);
                holder.senderLinear.setVisibility(View.INVISIBLE);
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                String currentDate1= currentDate.format(calendar.getTime());

                if (fromUserId.equals(messageSenderId)){
                    holder.senderLinear.setVisibility(View.VISIBLE);
                    holder.senderLinear.setBackgroundResource(R.drawable.balloon_outgoing_normal);
                    holder.senderMessageText.setText(messages.getMessage());
                    holder.senderMessageTime.setText(messages.getTime());
                    if (currentDate1.matches(messages.getDate())){
                        holder.senderMessageDate.setText("Today ");
                    } else{
                        holder.senderMessageDate.setText(messages.getDate());
                    }


                    if (messageSenderId!=null){
                        holder.senderSentStatus.setVisibility(View.VISIBLE);
                        holder.senderSentStatus.setImageResource(R.drawable.ic_delivered);

                    } else {
                        holder.senderSentStatus.setVisibility(View.INVISIBLE);
                    }

                } else {
                    holder.receiverLinear.setVisibility(View.VISIBLE);
                    holder.receiverLinear.setBackgroundResource(R.drawable.balloon_incoming_normal);
                    holder.receiverMessageText.setText(messages.getMessage());
                    holder.receiverMessTime.setText(messages.getTime());
                    if (currentDate1.matches(messages.getDate())){
                        holder.receiverMessageDate.setText("Today ");
                    } else {
                        holder.receiverMessageDate.setText(messages.getDate());

                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView senderMessageText,receiverMessageText, senderMessageTime, receiverMessTime;
        public TextView senderMessageDate,receiverMessageDate;
        public ImageView senderSentStatus;
        public LinearLayout receiverLinear, senderLinear;

        public MessageViewHolder(View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.chat_sender_message_text);
            senderMessageDate = itemView.findViewById(R.id.chat_sender_message_date);
            senderMessageTime = itemView.findViewById(R.id.chat_sender_message_time);
            receiverMessageText = itemView.findViewById(R.id.chat_receiver_message_text);
            receiverMessTime = itemView.findViewById(R.id.chat_receiver_message_time);
            receiverMessageDate = itemView.findViewById(R.id.chat_receiver_message_date);
            receiverLinear = itemView.findViewById(R.id.chat_receiver_linear1);
            senderLinear = itemView.findViewById(R.id.chat_sender_linear1);
            senderSentStatus = itemView.findViewById(R.id.sent_status);

        }



    }

}
