package com.example.pemesanantiket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pemesanantiket.R;
import com.example.pemesanantiket.model.Wisata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class TicketDetailAct extends AppCompatActivity {

    Button btn_buy_ticket;
    LinearLayout btn_back;
    TextView title_ticket, location_ticket, photo_spot_ticket, wifi_ticket, festival_ticket, short_desc_ticket;

    DatabaseReference reference;

    ImageView header_ticket_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        btn_buy_ticket = findViewById(R.id.btn_buy_ticket);
        header_ticket_detail = findViewById(R.id.header_ticket_detail);
        btn_back = findViewById(R.id.btn_back);
        title_ticket = findViewById(R.id.title_ticket);
        location_ticket = findViewById(R.id.location_ticket);
        photo_spot_ticket = findViewById(R.id.photo_spot_ticket);
        wifi_ticket = findViewById(R.id.wifi_ticket);
        festival_ticket = findViewById(R.id.festival_ticket);
        short_desc_ticket = findViewById(R.id.short_desc_ticket);

        //mengambil data dari intent
        Bundle bundle = getIntent().getExtras();
        final String jenis_tiket_baru = bundle.getString("jenis_tiket");

        //mengambil data dari firebase berdasarkan intent
        reference = FirebaseDatabase.getInstance().getReference().child("Wisata").child(jenis_tiket_baru);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Wisata wisata = dataSnapshot.getValue(Wisata.class);
                loadDataTicket(wisata);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_buy_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotocheckout = new Intent(TicketDetailAct.this, TicketCheckoutAct.class);
                //meletakkan data kepada intent
                gotocheckout.putExtra("jenis_tiket", jenis_tiket_baru);
                startActivity(gotocheckout);
            }
        });
    }

    private void loadDataTicket(Wisata wisata) {
        title_ticket.setText(wisata.getName());
        location_ticket.setText(wisata.getLocation());
        photo_spot_ticket.setText(wisata.getNumPhotoSpot());
        wifi_ticket.setText(wisata.getWifi());
        festival_ticket.setText(wisata.getNumFest());
        short_desc_ticket.setText(wisata.getDesc());
        Picasso.get().load(wisata.getUrlPhoto()).centerCrop().fit().into(header_ticket_detail);
    }
}
