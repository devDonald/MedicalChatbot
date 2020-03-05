package com.adaeze.medicalchatbot.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adaeze.medicalchatbot.R;
import com.adaeze.medicalchatbot.doctor.AppointmentChat;
import com.adaeze.medicalchatbot.doctor.DoctorModel;
import com.adaeze.medicalchatbot.utils.GMailSender;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import de.hdodenhof.circleimageview.CircleImageView;

public class Meeting extends AppCompatActivity {
    private RecyclerView mUsersRecycler;

    private DatabaseReference mDoctors;
    private String uid, myName,docEmail;
    private FirebaseUser mUsers;
    private FirebaseAuth mAuth;
    private GMailSender sender;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        mUsersRecycler = findViewById(R.id.all_doctor_recycler);
        mUsersRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mDoctors = FirebaseDatabase.getInstance().getReference().child("Doctors");
        mAuth=FirebaseAuth.getInstance();
        mUsers = mAuth.getCurrentUser();
        uid = mUsers.getUid();
        sender = new GMailSender("medcarebotservices@gmail.com", "adaezelydia");

        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                myName = bundle.getString("myName");

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            displayUsers();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView rvName,rvLocation, rvPresence, rvAvailability;
        CircleImageView rvUserImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            rvName = itemView.findViewById(R.id.meet_doctor_name);
            rvLocation = itemView.findViewById(R.id.meet_doctor_location);
            rvAvailability = itemView.findViewById(R.id.meet_doctor_availability);
            rvPresence = itemView.findViewById(R.id.meet_doctor_presence);
            rvUserImage = itemView.findViewById(R.id.meet_doctor_image);
        }


    }

    public void displayUsers(){
        final FirebaseRecyclerOptions<DoctorModel> options =
                new FirebaseRecyclerOptions.Builder<DoctorModel>()
                        .setQuery(mDoctors, DoctorModel.class)
                        .build();

        FirebaseRecyclerAdapter<DoctorModel,MyViewHolder> adapter = new
                FirebaseRecyclerAdapter<DoctorModel, MyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(final MyViewHolder holder, final int position, final DoctorModel model) {
                        try {
                            mDoctors.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){

                                        try {
                                            if (dataSnapshot.hasChild("photoUrl")){
                                                Glide.with(Meeting.this)

                                                        .load(model.getPhotoUrl())
                                                        .into(holder.rvUserImage);
                                            }

                                            holder.rvName.setText(model.getPosition()+" "+model.getName());
                                            holder.rvPresence.setText(model.getPresence());
                                            holder.rvAvailability.setText("Available From: "+model.getAvailability());
                                            holder.rvLocation.setText(model.getLocation());

                                        } catch (Exception e){
                                            e.printStackTrace();
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    docEmail = model.getEmail();

                                    new MyAsyncClass().execute();

                                    String visit_user_id= getRef(position).getKey();
                                    Intent private_chat = new Intent(Meeting.this, AppointmentChat.class);
                                    private_chat.putExtra("receiver_id",visit_user_id);
                                    private_chat.putExtra("receiver_name", model.getName());
                                    startActivity(private_chat);

                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    @NonNull
                    @Override
                    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_all_doctors,parent,false);

                        return new MyViewHolder(view);
                    }
                };
        mUsersRecycler.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent toReg = new Intent(Meeting.this, HomePage.class);
        toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        toReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        toReg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toReg);
    }

    class MyAsyncClass extends AsyncTask<Void, Void, Void> {



        @Override

        protected void onPreExecute() {

            super.onPreExecute();


        }



        @Override

        protected Void doInBackground(Void... mApi) {

            try {

                // Add subject, Body, your mail Id, and receiver mail Id.


                sender.sendMail("Appointment Request For MedCare Services",
                        "Hello Doctor, "+myName+" is requesting to have a Chat Session with you during your Appointment Hours. Kindly Login to the App and reply his chats.", "medcarebotservices@gmail.com", docEmail);

            }



            catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;

        }



        @Override

        protected void onPostExecute(Void result) {

            super.onPostExecute(result);


            MDToast.makeText(getApplicationContext(), "Email sent", MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show();

        }

    }
}
