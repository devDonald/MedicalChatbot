package com.adaeze.medicalchatbot.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.adaeze.medicalchatbot.LoginMenu;
import com.adaeze.medicalchatbot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.valdesekamdem.library.mdtoast.MDToast;

public class DoctorLogin extends AppCompatActivity {
    private TextView lost_password, tv_error;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsers;
    private KProgressHUD hud;
    private Button submit,btnBack;
    private EditText etEmail, etPassword;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_login);

        tv_error = findViewById(R.id.doc_login_error);
        etEmail = findViewById(R.id.doc_login_email);
        etPassword = findViewById(R.id.doc_login_password);
        submit = findViewById(R.id.doc_login_submit);
        btnBack = (Button) findViewById(R.id.doc_btn_back);


        hud = KProgressHUD.create(DoctorLogin.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Authenticating Doctor...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .setBackgroundColor(Color.BLACK)
                .setAutoDismiss(true);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DoctorLogin.this, LoginMenu.class));
                finish();
            }
        });


        mAuth = FirebaseAuth.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString().trim();

                if (email.isEmpty()){
                    etEmail.setError("field empty");
                    tv_error.setText("email empty");
                } else if (!email.contains("@")|| !email.contains(".")){
                    etEmail.setError("invalid email");
                    tv_error.setText("invalid email format");
                } else if (password.isEmpty()){
                    etPassword.setError("password empty");
                    tv_error.setText("password empty");
                } else if (password.length()<6){
                    etPassword.setError("password too short");
                    tv_error.setText("password too short, it must be 6 or more characters");
                } else {

                    hud.show();
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    hud.dismiss();
                                    if (task.isSuccessful()){
                                        MDToast.makeText(getApplicationContext(),"Doctor Authentication Successful",
                                                MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show();
                                        Intent toReg = new Intent(DoctorLogin.this, DoctorMenu.class);
                                        toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(toReg);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

    }
}
