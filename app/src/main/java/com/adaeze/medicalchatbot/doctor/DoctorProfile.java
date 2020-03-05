package com.adaeze.medicalchatbot.doctor;

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

public class DoctorProfile extends AppCompatActivity {
    private DatabaseReference mDocData,contactsRef;
    private String doc_id, doc_name,doc_position, doc_availability, doc_location;
    private TextView mNames,mAvailable,mLocation;
    private CircleImageView mProfileImage;
    private ImageView change_profile;
    private FirebaseAuth mAuth;
    private StorageReference imageReference;
    private static final int GALLERY_REQUEST =2;
    private Uri imageUri =null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);
        mNames = findViewById(R.id.dp_name);
        mAvailable = findViewById(R.id.dp_availability);
        mLocation = findViewById(R.id.dp_location);
        mProfileImage = findViewById(R.id.dprofile_image);
        change_profile = findViewById(R.id.d_change_profile_image);


        mDocData = FirebaseDatabase.getInstance().getReference().child("Doctors");
        imageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        doc_id= mAuth.getCurrentUser().getUid();


        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                doc_name = bundle.getString("name");
                doc_availability = bundle.getString("avail");
                doc_position = bundle.getString("position");
                doc_location = bundle.getString("location");


            }
        }catch (Exception e){
            e.printStackTrace();
        }

        mNames.setText(doc_position+" "+doc_name);
        mLocation.setText(doc_location);
        mAvailable.setText(doc_availability);
        try {
            mDocData.child(doc_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DoctorModel model = dataSnapshot.getValue(DoctorModel.class);

                    if (dataSnapshot.hasChild("photoUrl")){
                        RequestOptions options = new RequestOptions();
                        options.centerCrop();
                        options.centerInside();
                        options.fitCenter();
                        Glide.with(DoctorProfile.this)
                                .load(model.getPhotoUrl())
                                .apply(options)
                                .into(mProfileImage);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


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
                final StorageReference filePath = imageReference.child("images").child(doc_id).child("myImage.jpg");

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
                }).addOnCompleteListener(DoctorProfile.this, new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Uri downloadUrl = task.getResult();

                            mDocData.child(doc_id).child("photoUrl").setValue(downloadUrl.toString());

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
