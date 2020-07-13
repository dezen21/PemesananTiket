package com.example.pemesanantiket.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pemesanantiket.R;

public class SuccessBuyTicketAct extends AppCompatActivity {

    Animation app_splash,btt,ttb;
    Button btn_view_ticket, button_my_dashboard;
    TextView app_subtitle, app_tittle;
    ImageView icon_success_ticket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_buy_ticket);

        btn_view_ticket = findViewById(R.id.btn_view_ticket);
        button_my_dashboard = findViewById(R.id.button_my_dashboard);
        app_subtitle = findViewById(R.id.app_subtitle);
        app_tittle = findViewById(R.id.app_tittle);
        icon_success_ticket = findViewById(R.id.icon_success_ticket);

        //load animation
        app_splash = AnimationUtils.loadAnimation(this, R.anim.app_splash);
        btt = AnimationUtils.loadAnimation(this, R.anim.app_splash);
        ttb = AnimationUtils.loadAnimation(this, R.anim.app_splash);

        //run animation
        btn_view_ticket.startAnimation(btt);
        button_my_dashboard.startAnimation(btt);
        app_subtitle.startAnimation(ttb);
        app_tittle.startAnimation(ttb);
        icon_success_ticket.startAnimation(app_splash);

        btn_view_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoprofile = new Intent(SuccessBuyTicketAct.this,MyProfileAct.class);
                startActivity(gotoprofile);
            }
        });

        button_my_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotohome = new Intent(SuccessBuyTicketAct.this,HomeAct.class);
                startActivity(gotohome);
            }
        });
    }
}
