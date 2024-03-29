package com.adaeze.medicalchatbot.user.ui.services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.adaeze.medicalchatbot.R;


public class ServicesFragment extends Fragment {

    private ServicesViewModel servicesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        servicesViewModel =
                ViewModelProviders.of(this).get(ServicesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_services, container, false);

        return root;
    }
}