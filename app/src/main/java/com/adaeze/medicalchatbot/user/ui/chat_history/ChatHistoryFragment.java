package com.adaeze.medicalchatbot.user.ui.chat_history;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatHistoryFragment extends Fragment {
    private Dialog popDialog;
    private Button botHistory,doctorHistory;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsers;
    private FirebaseUser currentUser;
    private String onlineAns;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_chat_history, container, false);
         botHistory =root.findViewById(R.id.bot_care_history);
         doctorHistory =root.findViewById(R.id.chat_with_doc_history);
        popDialog = new Dialog(getContext());

        mAuth = FirebaseAuth.getInstance();
        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUser = mAuth.getCurrentUser();

        botHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        doctorHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secAns1 = new Intent(getContext(), ChatPopUp1.class);

                startActivity(secAns1);

            }
        });
        return root;
    }

}