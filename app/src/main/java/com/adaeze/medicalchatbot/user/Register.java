package com.adaeze.medicalchatbot.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.adaeze.medicalchatbot.LoginMenu;
import com.adaeze.medicalchatbot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.valdesekamdem.library.mdtoast.MDToast;

public class Register extends AppCompatActivity {
    private TextView tv_already_registered, tv_error;
    private EditText etNames,etEmail, etPhone, etPass, etConfirmPass, etAge, etSecurityAns;
    private Spinner spSecQuestion, spGender;
    private String names, email, phone, password, comfirm_password, age, securityAns, secQuestion, gender;
    private Button submit;
    private String otp;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsers;
    private KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tv_already_registered = findViewById(R.id.tv_no_account);
        etNames = findViewById(R.id.reg_name);
        etEmail = findViewById(R.id.reg_email);
        etPhone = findViewById(R.id.reg_phone);
        etPass = findViewById(R.id.reg_password);
        etConfirmPass = findViewById(R.id.reg_cpassword);
        etAge = findViewById(R.id.reg_age);
        etSecurityAns = findViewById(R.id.reg_sec_answer);
        spGender = findViewById(R.id.reg_gender);
        spSecQuestion = findViewById(R.id.reg_sec_question);
        submit = findViewById(R.id.reg_button);
        tv_error = findViewById(R.id.tv_reg_error);

        mAuth = FirebaseAuth.getInstance();
        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        hud = KProgressHUD.create(Register.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Adding User...")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .setBackgroundColor(Color.BLACK)
                .setAutoDismiss(true);

        tv_already_registered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg = new Intent(Register.this, UserLogin.class);
                reg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(reg);
                finish();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                names = etNames.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                phone = etPhone.getText().toString().trim();
                password = etPass.getText().toString().trim();
                comfirm_password = etConfirmPass.getText().toString().trim();
                age = etAge.getText().toString().trim();
                securityAns = etSecurityAns.getText().toString().trim();
                gender =spGender.getItemAtPosition(spGender.getSelectedItemPosition()).toString();
                secQuestion = spSecQuestion.getItemAtPosition(spSecQuestion.getSelectedItemPosition()).toString();

                if (names.isEmpty()){
                    etNames.setError("field empty");
                    tv_error.setText("full name empty");
                } else if (email.isEmpty()){
                    etEmail.setError("field empty");
                    tv_error.setText("email empty");
                } else if (!email.contains("@")|| !email.contains(".")){
                    etEmail.setError("invalid email");
                    tv_error.setText("invalid email format");
                } else if (password.isEmpty()){
                    etPass.setError("password empty");
                    tv_error.setText("password empty");
                } else if (password.length()<6){
                    etPass.setError("password too short");
                    tv_error.setText("password too short, it must be 6 or more characters");
                } else if (!password.matches(comfirm_password)){
                    etPass.setError("password mismatch");
                    tv_error.setText("password mismatch");
                } else if (age.isEmpty()){
                    etAge.setError("age empty");
                    tv_error.setText("Age field is empty");
                } else if (phone.isEmpty()){
                    etPhone.setError("phone empty");
                    tv_error.setText("Phone number is empty");
                } else if (gender.matches("Select Gender")){
                    tv_error.setText("Invalid Gender");
                } else if (secQuestion.matches("Select a Security Question")){
                    tv_error.setText("Pls select a security Question");
                } else if (securityAns.isEmpty()){
                    etSecurityAns.setError("field empty");
                    tv_error.setText("Security answer empty");
                } else {
                    hud.show();
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    hud.dismiss();
                                    if (task.isSuccessful()){
                                        String uid = mAuth.getCurrentUser().getUid();
                                        UserModel model = new UserModel(names,email,phone,age,gender,
                                                secQuestion,securityAns,uid);

                                        mUsers.child(uid).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                sendVerificationEmail();
                                            }
                                        });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            MDToast.makeText(getApplicationContext(),"Failed, try again",
                                    MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();
                            tv_error.setText("Authentication failed");
                        }
                    });
                }



            }
        });
    }

    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent


                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            MDToast.makeText(getApplicationContext(),"Registration Successful, Check your email for verification link",
                                    MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show();
                            Intent toReg = new Intent(Register.this, LoginMenu.class);
                            toReg.putExtra("name",names);
                            toReg.putExtra("age",age);
                            toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(toReg);
                            finish();

                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            resetUI();
                            MDToast.makeText(getApplicationContext(),"Failed, try again",
                                    MDToast.LENGTH_LONG,MDToast.TYPE_ERROR).show();
                            tv_error.setText("Authentication failed");
                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }


    private void resetUI(){
        etNames.setText("");
        etEmail.setText("");
        etPass.setText("");
        etPhone.setText("");
        etConfirmPass.setText("");
        etAge.setText("");
        etSecurityAns.setText("");
    }
}
