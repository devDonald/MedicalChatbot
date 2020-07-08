package com.adaeze.medicalchatbot.user.ui.bot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.adaeze.medicalchatbot.R;

public class BotFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BotViewModel botViewModel = ViewModelProviders.of(this).get(BotViewModel.class);

        return inflater.inflate(R.layout.fragment_bot_history, container, false);
    }
}