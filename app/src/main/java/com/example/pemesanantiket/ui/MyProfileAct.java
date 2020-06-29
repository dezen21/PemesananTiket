package com.example.pemesanantiket.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pemesanantiket.model.MyTicket;
import com.example.pemesanantiket.R;
import com.example.pemesanantiket.adapter.TicketAdapter;
import com.example.pemesanantiket.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class MyProfileAct extends AppCompatActivity {

    LinearLayout item_my_ticket;
    Button btn_edit_profile, btn_back_home, btn_sign_out;
    TextView nama_lengkap, bio;
    ImageView photo_profile;

    DatabaseReference reference, reference2;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new = "";

    RecyclerView myticket_place;
    ArrayList<MyTicket> list;
    TicketAdapter ticketAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        getUsernameLocal();

        item_my_ticket = findViewById(R.id.item_my_ticket);
        btn_edit_profile = findViewById(R.id.btn_edit_profile);
        btn_back_home = findViewById(R.id.btn_back_home);
        btn_sign_out = findViewById(R.id.btn_sign_out);

        nama_lengkap = findViewById(R.id.nama_lengkap);
        bio = findViewById(R.id.bio);
        photo_profile = findViewById(R.id.photo_profile);

        myticket_place = findViewById(R.id.myticket_place);
        myticket_place.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<MyTicket>();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                nama_lengkap.setText(user.getName());
                bio.setText(user.getBio());

                String photoUrl = (!Objects.requireNonNull(user.getUrlPhoto()).isEmpty())?
                        user.getUrlPhoto() : getString(R.string.no_photo_url);
                Picasso.get().load(photoUrl).centerCrop().fit().into(photo_profile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btn_edit_profile.setOnClickListener(v -> {
            Intent gotoeditprofile = new Intent (MyProfileAct.this,EditProfileAct.class);
            startActivity(gotoeditprofile);
        });

        reference2 = FirebaseDatabase.getInstance().getReference().child("MyTickets").child(username_key_new);
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    MyTicket p = dataSnapshot1.getValue(MyTicket.class);
                    list.add(p);
                }
                ticketAdapter = new TicketAdapter(MyProfileAct.this, list);
                myticket_place.setAdapter(ticketAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_back_home.setOnClickListener(v -> finish());
        btn_sign_out.setOnClickListener(v -> {
            //menghapus value dari username local
            SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(username_key, null);
            editor.apply();

            //pindah act
            Intent gotosignin = new Intent (MyProfileAct.this,SignInAct.class);
            startActivity(gotosignin);
            finish();
        });
    }

    public void getUsernameLocal (){
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }

}
