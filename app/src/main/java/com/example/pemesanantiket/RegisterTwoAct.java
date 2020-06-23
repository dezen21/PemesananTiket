package com.example.pemesanantiket;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterTwoAct extends AppCompatActivity {

    LinearLayout btn_back;
    Button btn_continue, btn_add_photo;
    ImageView pic_photo_register_user;
    EditText bio, nama_lengkap;

    Uri photo_location;
    Integer photo_max = 1;
    DatabaseReference reference;
    StorageReference storage;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_two);

        getUsernameLocal();

        btn_add_photo = findViewById(R.id.btn_add_photo);
        btn_continue = findViewById(R.id.btn_continue);
        btn_back = findViewById(R.id.btn_back);
        pic_photo_register_user = findViewById(R.id.pic_photo_register_user);
        bio = findViewById(R.id.bio);
        nama_lengkap = findViewById(R.id.nama_lengkap);

        btn_add_photo.setOnClickListener(v -> findPhoto());
        btn_continue.setOnClickListener(v -> {
            //ubah state menjadi loading
            btn_continue.setEnabled(false);
            btn_continue.setText(R.string.loading_state);
            //menyimpan ke firebase
            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
            storage = FirebaseStorage.getInstance().getReference().child("Photousers").child(username_key_new);

            //validasi untuk file apakah ada
            if (photo_location != null) {

                final StorageReference storageReference1 = storage.child(
                        System.currentTimeMillis() + "." + getFileExtention(photo_location)
                );

                storageReference1.putFile(photo_location).addOnSuccessListener(taskSnapshot ->
                        storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {

                    String uri_photo = uri.toString();
                    reference.getRef().child("url_photo_profile").setValue(uri_photo);
                    reference.getRef().child("nama_lengkap").setValue(nama_lengkap.getText().toString());
                    reference.getRef().child("bio").setValue(bio.getText().toString());

                }).addOnCompleteListener(task -> {
                    //pindah act
                    Intent gotosuccess = new Intent(RegisterTwoAct.this, SuccessRegisterAct.class);
                    startActivity(gotosuccess);
                })).addOnCompleteListener(task -> {
                    //pindah act
                    /*Intent gotosuccess = new Intent(RegisterTwoAct.this,SuccessRegisterAct.class);
                    startActivity(gotosuccess);*/
                });
            } // kalo fotonya kosong diem aja dong?

        });


        btn_back.setOnClickListener(v -> finish());
    }

    String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void findPhoto() {
        Intent pic = new Intent();
        pic.setType("image/*");
        pic.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pic, photo_max);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == photo_max && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photo_location = data.getData();
            Picasso.with(this).load(photo_location).centerCrop().fit().into(pic_photo_register_user);
        }
    }

    public void getUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }
}
