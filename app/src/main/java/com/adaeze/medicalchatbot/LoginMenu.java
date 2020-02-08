package com.adaeze.medicalchatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adaeze.medicalchatbot.doctor.DoctorLogin;
import com.adaeze.medicalchatbot.user.Register;
import com.adaeze.medicalchatbot.user.UserLogin;

public class LoginMenu extends AppCompatActivity {

    private TextView lost_password, tv_error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        lost_password = findViewById(R.id.tv_login_forgot);


        TextView tv_not_registered = findViewById(R.id.login_no_account);

        tv_not_registered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toReg = new Intent(LoginMenu.this, Register.class);
                toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(toReg);
                finish();
            }
        });

        lost_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toreset = new Intent(LoginMenu.this, ResetPassword.class);
                toreset.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(toreset);
                finish();
            }
        });

        Button doctorLogin = findViewById(R.id.doctor_login);
        doctorLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tordoctor = new Intent(LoginMenu.this, DoctorLogin.class);
                tordoctor.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(tordoctor);
                finish();
            }
        });


        Button userLogin = findViewById(R.id.user_login);

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent touser = new Intent(LoginMenu.this, UserLogin.class);
                touser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(touser);
                finish();
            }
        });
    }
}
