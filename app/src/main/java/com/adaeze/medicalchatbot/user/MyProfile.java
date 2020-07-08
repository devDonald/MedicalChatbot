package com.adaeze.medicalchatbot.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adaeze.medicalchatbot.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {

    private DatabaseReference mUsersDatabase,contactsRef;
    private String user_id, user_age, user_name,user_email, user_phone, user_question;
    private TextView mNames,mPhone,mAge,mEmail, mQuestion;
    private CircleImageView mProfileImage;
    private ImageView change_profile;
    private FirebaseAuth mAuth;
    private StorageReference imageReference;
    private static final int GALLERY_REQUEST =2;
    private Uri imageUri =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        mNames = findViewById(R.id.mp_name);
        mPhone = findViewById(R.id.mp_phone);
        mAge = findViewById(R.id.mp_age);
        mEmail = findViewById(R.id.mp_email);
        mQuestion = findViewById(R.id.mp_sec_question);
        mProfileImage = findViewById(R.id.mprofile_image);
        change_profile = findViewById(R.id.m_change_profile_image);


        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        imageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user_id= mAuth.getCurrentUser().getUid();



        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                user_age = bundle.getString("age");
                user_name = bundle.getString("names");
                user_email = bundle.getString("email");
                user_phone = bundle.getString("phone");
                user_question = bundle.getString("question");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        mNames.setText(user_name);
        mAge.setText(user_age+" Years");
        mPhone.setText(user_phone);
        mEmail.setText(user_email);
        mQuestion.setText(user_question);
        mUsersDatabase.child(user_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel model = dataSnapshot.getValue(UserModel.class);
                        try {

                            if (dataSnapshot.hasChild("photoUrl")){
                                RequestOptions options = new RequestOptions();
                                options.centerCrop();
                                options.centerInside();
                                options.fitCenter();
                                Glide.with(MyProfile.this)
                                        .load(model.getPhotoUrl())
                                        .apply(options)
                                        .into(mProfileImage);

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galIntent, "Choose picture"), GALLERY_REQUEST);


            }
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK)
        {
            imageUri = data.getData();


            try {
                final StorageReference filePath = imageReference.child("images").child(user_id).child("myImage.jpg");

                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                byte[] datas = baos.toByteArray();
                UploadTask uploadTask = filePath.putBytes(datas);
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(MyProfile.this, new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Uri downloadUrl = task.getResult();

                            mUsersDatabase.child(user_id).child("photoUrl").setValue(downloadUrl.toString());

                            MDToast mdToast = MDToast.makeText(getApplicationContext(),
                                    "Profile image Updated successfully!",
                                    MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS);
                            mdToast.show();
                            mProfileImage.setImageURI(imageUri);


                        } else {
                            MDToast mdToast = MDToast.makeText(getApplicationContext(),
                                    "Profile image failed to update!",
                                    MDToast.LENGTH_LONG,MDToast.TYPE_ERROR);
                            mdToast.show();

                        }

                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}
