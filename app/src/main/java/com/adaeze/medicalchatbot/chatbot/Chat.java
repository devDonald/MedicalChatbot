package com.adaeze.medicalchatbot.chatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.adaeze.medicalchatbot.R;

import io.kommunicate.KmConversationBuilder;
import io.kommunicate.Kommunicate;
import io.kommunicate.callbacks.KmCallback;

public class Chat extends AppCompatActivity {
    private static final String APP_ID ="1064d7a7d2998a753815df0ba991dbbad";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = getApplicationContext();


    }
}