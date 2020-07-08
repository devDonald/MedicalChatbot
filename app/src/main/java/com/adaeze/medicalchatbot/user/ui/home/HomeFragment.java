package com.adaeze.medicalchatbot.user.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.adaeze.medicalchatbot.LoginMenu;
import com.adaeze.medicalchatbot.R;
import com.adaeze.medicalchatbot.chatbot.Chat;
import com.adaeze.medicalchatbot.user.HomePage;
import com.adaeze.medicalchatbot.user.Meeting;
import com.adaeze.medicalchatbot.user.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.valdesekamdem.library.mdtoast.MDToast;

import io.kommunicate.KmConversationBuilder;
import io.kommunicate.Kommunicate;
import io.kommunicate.callbacks.KmCallback;


public class HomeFragment extends Fragment {
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

    private HomeViewModel homeViewModel;
    private static final String APP_ID ="1064d7a7d2998a753815df0ba991dbbad";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        Kommunicate.init(getContext(), APP_ID);


        try {
            mAuth=FirebaseAuth.getInstance();
            cUsers = mAuth.getCurrentUser();
            uid = cUsers.getUid();
            email=cUsers.getEmail();

            mUsers = FirebaseDatabase.getInstance().getReference().child("Users");

            tv_welcome = root.findViewById(R.id.welcome_text);
            meetDoctor = root.findViewById(R.id.chat_with_doc);
            chat = root.findViewById(R.id.bot_care);
            mUsers.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel profileModel = dataSnapshot.getValue(UserModel.class);
                    names = profileModel.getName();
                    phone =profileModel.getPhone();
                    age =profileModel.getAge();

                     tv_welcome.setText("Hello, "+names);

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

                Intent intent = new Intent(getContext(), LoginMenu.class);
                startActivity(intent);
                Log.d(TAG, "Logged out after 3 minutes on inactivity.");
                getActivity().finish();

                MDToast.makeText(getContext(), "Logged out after 3 minutes on inactivity.",MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show();
            }
        };

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new KmConversationBuilder(getContext())
                        .launchConversation(new KmCallback() {
                            @Override
                            public void onSuccess(Object message) {
                                Log.d("Conversation", "Success : " + message);
                            }

                            @Override
                            public void onFailure(Object error) {
                                Log.d("Conversation", "Failure : " + error);
                            }
                        });
//                Intent medchat = new Intent(getContext(), Chat.class);
//                medchat.putExtra("names",names);
//
//                startActivity(medchat);
            }
        });

        meetDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent medDoc = new Intent(getContext(), Meeting.class);
                medDoc.putExtra("myName", names);
                startActivity(medDoc);
            }
        });

        startHandler();

        return root;
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
    public void onPause() {

        stopHandler();
        Log.d("onPause", "onPauseActivity change");
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        startHandler();

        Log.d("onResume", "onResume_restartActivity");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopHandler();
        Log.d("onDestroy", "onDestroyActivity change");

    }

}