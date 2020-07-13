package com.example.pemesanantiket.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pemesanantiket.R;
import com.example.pemesanantiket.model.Ticket;
import com.example.pemesanantiket.model.User;
import com.example.pemesanantiket.model.Wisata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class TicketCheckoutAct extends AppCompatActivity {

    Button btn_buy_ticket, btnmines, btnplus;
    TextView textjumlahtiket, texttotalharga, textmybalance, namawisata, lokasi, ketentuan;
    Integer valuejumlahtiket = 1;
    Integer mybalance = 0;
    Integer valuetotalharga = 0;
    Integer valuehargatiket = 0;
    ImageView notice_uang;
    LinearLayout btn_back;
    Integer sisa_balance = 0;

    DatabaseReference reference, reference2, reference3, reference4;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new = "";

    String date_wisata;
    String time_wisata;

    // generate nomor int secara random
    // karena kita ingin membuat transaksi secara unik
    Integer nomor_transaksi = new Random().nextInt();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_checkout);

        getUsernameLocal();

        //mengambil data dari intent
        Bundle bundle = getIntent().getExtras();
        final String jenis_tiket_baru = bundle.getString("jenis_tiket");

        //registrasi element
        btn_back = findViewById(R.id.btn_back);
        btnmines = findViewById(R.id.btnmines);
        btnplus = findViewById(R.id.btnplus);
        textjumlahtiket = findViewById(R.id.textjumlahtiket);
        btn_buy_ticket = findViewById(R.id.btn_buy_ticket);
        notice_uang = findViewById(R.id.notice_uang);

        namawisata = findViewById(R.id.nama_wisata);
        ketentuan = findViewById(R.id.ketentuan);
        lokasi = findViewById(R.id.lokasi);

        texttotalharga = findViewById(R.id.texttotalharga);
        textmybalance = findViewById(R.id.textmybalance);

        //setting value baru untuk beberapa komponen
        textjumlahtiket.setText(valuejumlahtiket.toString());


        //secara default hide
        btnmines.animate().alpha(0).setDuration(100).start();
        btnmines.setEnabled(false);
        notice_uang.setVisibility(View.GONE);

        //mengambil data user dari firebase
        reference2 = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mybalance = user.getBalance();
                textmybalance.setText("US$" + mybalance + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //mengambil data dari firebase berdasarkan intent
        reference = FirebaseDatabase.getInstance().getReference().child("Wisata").child(jenis_tiket_baru);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Wisata wisata = dataSnapshot.getValue(Wisata.class);
                loadDataWisata(wisata);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btnplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valuejumlahtiket += 1;
                textjumlahtiket.setText(valuejumlahtiket.toString());
                if (valuejumlahtiket > 1) {
                    btnmines.animate().alpha(1).setDuration(300).start();
                    btnmines.setEnabled(true);
                }
                valuetotalharga = valuehargatiket * valuejumlahtiket;
                texttotalharga.setText("US$ " + valuetotalharga + "");
                if (valuetotalharga > mybalance) {
                    btn_buy_ticket.animate().translationY(250).alpha(0).setDuration(350).start();
                    btn_buy_ticket.setEnabled(false);
                    textmybalance.setTextColor(Color.parseColor("#D1206B"));
                    notice_uang.setVisibility(View.VISIBLE);
                }
            }
        });

        btnmines.setOnClickListener(v -> {
            valuejumlahtiket -= 1;
            textjumlahtiket.setText(valuejumlahtiket.toString());
            if (valuejumlahtiket < 2) {
                btnmines.animate().alpha(0).setDuration(300).start();
                btnmines.setEnabled(false);
            }
            valuetotalharga = valuehargatiket * valuejumlahtiket;
            texttotalharga.setText("US$ " + valuetotalharga + "");
            if (valuetotalharga < mybalance) {
                btn_buy_ticket.animate().translationY(0).alpha(1).setDuration(350).start();
                btn_buy_ticket.setEnabled(true);
                textmybalance.setTextColor(Color.parseColor("#203DD1"));
                notice_uang.setVisibility(View.GONE);
            }
        });


        btn_buy_ticket.setOnClickListener(v -> {

            //menyimpan data user kepada firebase dan membuat table baru "com.example.pemesanantiket.model.MyTicket"
            reference3 = FirebaseDatabase.getInstance().getReference().child("MyTickets").child(username_key_new).child(namawisata.getText().toString() + nomor_transaksi);
            reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Ticket ticket = new Ticket(
                            namawisata.getText().toString() + nomor_transaksi,
                            namawisata.getText().toString(),
                            valuejumlahtiket.toString(),
                            ketentuan.getText().toString(),
                            lokasi.getText().toString(),
                            date_wisata,
                            time_wisata,
                            valuetotalharga
                    );
                    reference3.getRef().setValue(ticket);
                    Intent gotosuccessticket = new Intent(TicketCheckoutAct.this, SuccessBuyTicketAct.class);
                    startActivity(gotosuccessticket);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            // update data balance kepada user (yang saat ini login)
            //mengambil data user dari firebase
            reference4 = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
            reference4.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    sisa_balance = mybalance - valuetotalharga;
                    user.setBalance(sisa_balance);
                    reference4.getRef().setValue(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        btn_back.setOnClickListener(v -> finish());
    }

    private void loadDataWisata(Wisata wisata) {
        //menimpa data yang ada dengan data yang baru
        namawisata.setText(wisata.getName());
        lokasi.setText(wisata.getLocation());
        ketentuan.setText(wisata.getRequirement());

        date_wisata = wisata.getDateWisata();
        time_wisata = wisata.getTime();

        valuehargatiket = Integer.valueOf(wisata.getPrice());

        valuetotalharga = valuehargatiket * valuejumlahtiket;
        texttotalharga.setText("US$ " + valuetotalharga + "");
    }

    public void getUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }
}
