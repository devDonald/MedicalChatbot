package com.adaeze.medicalchatbot.user.ui.bot;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BotViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BotViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is share fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}