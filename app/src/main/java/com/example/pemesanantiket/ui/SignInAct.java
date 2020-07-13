package com.example.pemesanantiket.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pemesanantiket.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInAct extends AppCompatActivity {

    TextView btn_new_account;
    Button btn_sign_in;
    EditText xusername, xpassword;

    DatabaseReference reference;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btn_new_account = findViewById(R.id.btn_new_account);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        xusername    = findViewById(R.id.xusername);
        xpassword    = findViewById(R.id.xpassword);

        btn_sign_in.setOnClickListener(v -> {

            //ubah state menjadi loading
            btn_sign_in.setEnabled(false);
            btn_sign_in.setText("Loading...");

            final String username = xusername.getText().toString();
            final String password = xpassword.getText().toString();

            if (username.isEmpty()){
                Toast.makeText(getApplicationContext(), "Username Kosong!", Toast.LENGTH_SHORT).show();
                //ubah state menjadi loading
                btn_sign_in.setEnabled(true);
                btn_sign_in.setText("SIGN IN");
            }
            else{
                if (password.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Password Kosong", Toast.LENGTH_SHORT).show();
                    //ubah state menjadi loading
                    btn_sign_in.setEnabled(true);
                    btn_sign_in.setText("SIGN IN");

                }else {
                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                //ambil data password dari firebase
                                String passwordFromFirebase = dataSnapshot.child("password").getValue().toString();

                                //validatai password dengan psw firebase
                                if(password.equals(passwordFromFirebase)){

                                    //simpan username (key) kepada local
                                    SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(username_key, xusername.getText().toString());
                                    editor.apply();

                                    //pindah act
                                    Intent gotohome = new Intent(SignInAct.this,HomeAct.class);
                                    startActivity(gotohome);

                                    //Toast.makeText(getApplicationContext(), "Selamat Datang", Toast.LENGTH_SHORT).show();

                                }else {
                                    Toast.makeText(getApplicationContext(), "Password salah", Toast.LENGTH_SHORT).show();
                                    //ubah state menjadi loading
                                    btn_sign_in.setEnabled(true);
                                    btn_sign_in.setText("SIGN IN");
                                }

                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Username tidak ada!", Toast.LENGTH_SHORT).show();
                                //ubah state menjadi loading
                                btn_sign_in.setEnabled(true);
                                btn_sign_in.setText("SIGN IN");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Database Eror", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        btn_new_account.setOnClickListener(v -> {
            Intent gotoregisterone = new Intent(SignInAct.this,RegisterOneAct.class);
            startActivity(gotoregisterone);
        });
    }
}