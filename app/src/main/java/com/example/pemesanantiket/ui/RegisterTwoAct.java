package com.example.pemesanantiket.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.pemesanantiket.R;
import com.example.pemesanantiket.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterTwoAct extends AppCompatActivity {

    public static final String DATA_USER_EXTRA = "data_user_extra";
    private static final String TAG = "RegisterTwoAct";
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

        User user = getIntent().getParcelableExtra(DATA_USER_EXTRA);

        btn_add_photo = findViewById(R.id.btn_add_photo);
        btn_continue = findViewById(R.id.btn_continue);
        btn_back = findViewById(R.id.btn_back);
        pic_photo_register_user = findViewById(R.id.pic_photo_register_user);
        bio = findViewById(R.id.bio);
        nama_lengkap = findViewById(R.id.nama_lengkap);

        btn_add_photo.setOnClickListener(v -> findPhoto());
        btn_continue.setOnClickListener(v -> {

            btn_continue.setEnabled(false);
            btn_continue.setText(R.string.loading_state);

            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
            storage = FirebaseStorage.getInstance().getReference().child("Photousers").child(username_key_new);

            assert user != null;
            user.setName(nama_lengkap.getText().toString());
            user.setBio(bio.getText().toString());

            if (photo_location != null) {
                final StorageReference storageReference1 = storage.child(
                        System.currentTimeMillis() + "." + getFileExtention(photo_location)
                );
                storageReference1.putFile(photo_location).addOnSuccessListener(taskSnapshot ->
                        storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
                            String uri_photo = uri.toString();
                            user.setUrlPhoto(uri_photo);
                            Log.d(TAG, "complate upload photo");
                        }).addOnFailureListener(e -> Log.d(TAG, "error upload photo: " + e.getMessage()))

                );
            } else {
                user.setUrlPhoto(String.valueOf(R.string.no_photo_url));
            }
            reference.setValue(user)
                    .addOnCompleteListener(task -> startActivity(
                            new Intent(RegisterTwoAct.this, SuccessRegisterAct.class)
                    ))
                    .addOnFailureListener(e -> Log.d(TAG, "failure update data user: " + e.getMessage()));
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
            Picasso.get().load(photo_location).centerCrop().fit().into(pic_photo_register_user);
        }
    }

    public void getUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }
}
