package com.example.pemesanantiket.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pemesanantiket.R;
import com.example.pemesanantiket.model.Ticket;
import com.example.pemesanantiket.model.User;
import com.example.pemesanantiket.utils.UIUtilsKt;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MyTicketDetailAct extends AppCompatActivity {

    public static final String ID_TICKET_EXTRA = "id_ticket_extra";
    DatabaseReference reference;

    LinearLayout btn_back;
    TextView xnama_wisata, xlokasi, xtime_wisata, xdate_wisata, xketentuan;
    ImageView ivBarcode;
    private Button btnRefund;
    private Ticket ticket;
    private String idWisata;
    private User mUser;

    String USERNAME_KEY = "usernamekey";
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ticket_detail);

        btn_back = findViewById(R.id.btn_back);
        xnama_wisata = findViewById(R.id.xnama_wisata);
        xlokasi = findViewById(R.id.xlokasi);
        xtime_wisata = findViewById(R.id.xtime_wisata);
        xdate_wisata = findViewById(R.id.xdate_wisata);
        xketentuan = findViewById(R.id.xketentuan);
        btnRefund = findViewById(R.id.btnRefund);
        ivBarcode = findViewById(R.id.ivBarcode);
        reference = FirebaseDatabase.getInstance().getReference();

        getUsernameLocal();
        initView();

        btn_back.setOnClickListener(v -> finish());

        btnRefund.setOnClickListener(v -> {
            UIUtilsKt.showPopupDialog(this,
                    "Refund Ticket",
                    "Apakah anda yaking akan merefund tiket ini?",
                    "Iya",
                    "Tidak", isOk -> {
                        if (isOk) {
                            Integer mBalance =  mUser.getBalance()+ticket.getTotalPrice();
                            mUser.setBalance(mBalance);
                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
                            reference.getRef().setValue(mUser);
                            reference = FirebaseDatabase.getInstance().getReference().child("MyTickets").child(username);
                            reference.getRef().child(idWisata).removeValue().addOnSuccessListener(aVoid -> {
                                UIUtilsKt.showToast(this, "Tiket berhasil direfund");
                                finish();
                            });
                        }
                        return null;
                    });
        });
    }

    private void initView() {
        Log.d("TAG", "initView: " + username);
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                setDataUser(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        String idTicket = getIntent().getStringExtra(ID_TICKET_EXTRA);
        assert idTicket != null;
        reference = FirebaseDatabase.getInstance().getReference().child("MyTickets").child(username).child(idTicket);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ticket = dataSnapshot.getValue(Ticket.class);
                loadDataWisata(ticket);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setDataUser(User user) {
        mUser = user;
        Log.d("TAG", "setDataUser: "+user.getBalance());
    }

    private void loadDataWisata(Ticket wisata) {
        Log.d("TAG", "loadDataWisata: " + wisata.getDate());
        idWisata = wisata.getIdTicket();
        xnama_wisata.setText(wisata.getName());
        xlokasi.setText(wisata.getLocation());
        xtime_wisata.setText(wisata.getTime());
        xdate_wisata.setText(wisata.getDate());
        xketentuan.setText(wisata.getRequirement());

        UIUtilsKt.generateBarcode(this, wisata.getIdTicket(), imgUri -> {
            Picasso.get().load(imgUri).into(ivBarcode);
            return null;
        });
    }

    public void getUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username = sharedPreferences.getString(username, "");
    }
}
