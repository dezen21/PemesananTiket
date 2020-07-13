package com.example.pemesanantiket.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pemesanantiket.R;
import com.example.pemesanantiket.model.User;
import com.github.florent37.shapeofview.shapes.CircleView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class HomeAct extends AppCompatActivity {

    LinearLayout btn_ticket_pisa, btn_ticket_torri, btn_ticket_pagoda, btn_ticket_candi, btn_ticket_sphinx, btn_ticket_monas;
    CircleView btn_to_profile;
    ImageView photo_home_user;
    TextView user_balance, nama_lengkap, bio;

    DatabaseReference reference;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getUsernameLocal();

        btn_ticket_pisa = findViewById(R.id.btn_ticket_pisa);
        btn_ticket_torri = findViewById(R.id.btn_ticket_torri);
        btn_ticket_pagoda = findViewById(R.id.btn_ticket_pagoda);
        btn_ticket_candi = findViewById(R.id.btn_ticket_candi);
        btn_ticket_sphinx = findViewById(R.id.btn_ticket_sphinx);
        btn_ticket_monas = findViewById(R.id.btn_ticket_monas);

        btn_to_profile = findViewById(R.id.btn_to_profile);
        photo_home_user = findViewById(R.id.photo_home_user);
        user_balance = findViewById(R.id.user_balance);
        nama_lengkap = findViewById(R.id.nama_lengkap);
        bio = findViewById(R.id.bio);

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                nama_lengkap.setText(user.getName());
                bio.setText(user.getBio());
                user_balance.setText("US$ " + user.getBalance());
                String photoUrl = (!Objects.requireNonNull(user.getUrlPhoto()).isEmpty())?
                        user.getUrlPhoto() : getString(R.string.no_photo_url);
                Picasso.get().load(photoUrl).centerCrop().fit().into(photo_home_user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_to_profile.setOnClickListener(v -> {
            Intent gotoprofile = new Intent(HomeAct.this, MyProfileAct.class);
            startActivity(gotoprofile);
        });


        btn_ticket_pisa.setOnClickListener(v -> {

            Intent gotopisaticket = new Intent(HomeAct.this, TicketDetailAct.class);
            //meletakkan data kepada intent
            gotopisaticket.putExtra("jenis_tiket", "Pisa");
            startActivity(gotopisaticket);
        });

        btn_ticket_torri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gototorriticket = new Intent(HomeAct.this, TicketDetailAct.class);
                gototorriticket.putExtra("jenis_tiket", "Torri");
                startActivity(gototorriticket);
            }
        });

        btn_ticket_pagoda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotopagodaticket = new Intent(HomeAct.this, TicketDetailAct.class);
                gotopagodaticket.putExtra("jenis_tiket", "Pagoda");
                startActivity(gotopagodaticket);
            }
        });

        btn_ticket_candi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotocanditicket = new Intent(HomeAct.this, TicketDetailAct.class);
                gotocanditicket.putExtra("jenis_tiket", "Candi");
                startActivity(gotocanditicket);
            }
        });

        btn_ticket_sphinx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotosphinxticket = new Intent(HomeAct.this, TicketDetailAct.class);
                gotosphinxticket.putExtra("jenis_tiket", "Sphinx");
                startActivity(gotosphinxticket);
            }
        });

        btn_ticket_monas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotomonasticket = new Intent(HomeAct.this, TicketDetailAct.class);
                gotomonasticket.putExtra("jenis_tiket", "Monas");
                startActivity(gotomonasticket);
            }
        });

    }

    public void getUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }
}
