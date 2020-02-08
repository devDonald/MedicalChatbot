package com.adaeze.medicalchatbot.user;

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
import com.adaeze.medicalchatbot.MainActivity;
import com.adaeze.medicalchatbot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.valdesekamdem.library.mdtoast.MDToast;

public class UserLogin extends AppCompatActivity {
    private TextView lost_password, tv_error;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsers;
    private KProgressHUD hud;
    private Button submit, btnBack;
    private EditText etEmail, etPassword;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        tv_error = findViewById(R.id.login_error);
        etEmail = findViewById(R.id.login_email);
        etPassword = findViewById(R.id.login_password);
        submit = findViewById(R.id.login_submit);
        btnBack = (Button) findViewById(R.id.user_btn_back);

        hud = KProgressHUD.create(UserLogin.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Authenticating User...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .setBackgroundColor(Color.BLACK)
                .setAutoDismiss(true);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserLogin.this, LoginMenu.class));
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
                                        checkIfEmailVerified();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

       try {
           if (user.isEmailVerified())
           {
               // user is verified, so you can finish this activity or send user to activity which you want.
               MDToast.makeText(getApplicationContext(),"UserLogin Successful",
                       MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show();
               Intent toReg = new Intent(UserLogin.this, MainActivity.class);
               toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(toReg);
               finish();
           }
           else
           {
               // email is not verified, so just prompt the message to the user and restart this activity.
               // NOTE: don't forget to log out the user.
               MDToast.makeText(getApplicationContext(),"Authentication Failed",
                       MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();
               FirebaseAuth.getInstance().signOut();

               etEmail.setText("");
               etPassword.setText("");
               tv_error.setText("Authentication Failed");

               //restart this activity

           }
       } catch (Exception e){
           e.printStackTrace();
       }
    }


}
