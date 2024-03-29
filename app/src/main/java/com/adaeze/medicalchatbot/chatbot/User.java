package com.adaeze.medicalchatbot.chatbot;

import android.graphics.Bitmap;

import com.github.bassaer.chatmessageview.model.IChatUser;

public class User implements IChatUser {
    private String id;
    private String name;
    private Bitmap icon;

    public User() {
    }

    public User(String id, String name, Bitmap icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }


    @Override
    public Bitmap getIcon() {
        return icon;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setIcon(Bitmap bitmap) {
        this.icon=bitmap;
    }
}
