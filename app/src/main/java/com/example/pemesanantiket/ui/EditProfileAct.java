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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileAct extends AppCompatActivity {


    private static final String TAG = "EditProfileAct";
    Button btn_save, btn_add_new_photo;
    LinearLayout btn_back;
    ImageView photo_edit_profile;
    EditText xnama_lengkap, xbio, xusername, xpassword, xemail_address;

    Uri photo_location;
    Integer photo_max = 1;

    StorageReference storage;

    DatabaseReference reference;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new = "";

    private User currentUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        photo_edit_profile = findViewById(R.id.photo_edit_profile);
        btn_add_new_photo = findViewById(R.id.btn_add_new_photo);

        xnama_lengkap = findViewById(R.id.xnama_lengkap);
        xbio = findViewById(R.id.xbio);
        xusername = findViewById(R.id.xusername);
        xpassword = findViewById(R.id.xpassword);
        xemail_address = findViewById(R.id.xemail_address);

        btn_back = findViewById(R.id.btn_back);
        btn_save = findViewById(R.id.btn_save);
        getUsernameLocal();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
        storage = FirebaseStorage.getInstance().getReference().child("Photousers").child(username_key_new);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                currentUser = user;
                assert user != null;
                xnama_lengkap.setText(user.getName());
                xbio.setText(user.getBio());
                xusername.setText(user.getUsername());
                xpassword.setText(user.getPassword());
                xemail_address.setText(user.getEmail());

                String photoUrl = (!Objects.requireNonNull(user.getUrlPhoto()).isEmpty()) ?
                        user.getUrlPhoto() : getString(R.string.no_photo_url);
                Picasso.get().load(photoUrl).centerCrop().fit().into(photo_edit_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_back.setOnClickListener(v -> finish());

        btn_save.setOnClickListener(v -> {
            //ubah state menjadi loading
            btn_save.setEnabled(false);
            btn_save.setText("Loading...");

            //validasi untuk file apakah ada
            if (photo_location != null) {
                final StorageReference storageReference1 = storage.child(System.currentTimeMillis() + "." + getFileExtention(photo_location));
                storageReference1.putFile(photo_location).addOnSuccessListener(taskSnapshot ->
                        storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
                            String photoUrl = uri.toString();
                            saveData(photoUrl);
                        }).addOnCompleteListener(task -> {
                            Log.d(TAG, "Success upload photo");
                        }));
            } else {
                saveData(currentUser.getUrlPhoto());
            }
        });

        btn_add_new_photo.setOnClickListener(v -> findPhoto());
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
            Picasso.get().load(photo_location).centerCrop().fit().into(photo_edit_profile);
        }
    }

    public void getUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }

    private void saveData(String photUrl) {
        User user = new User(
                xbio.getText().toString(),
                xemail_address.getText().toString(),
                xnama_lengkap.getText().toString(),
                xpassword.getText().toString(),
                photUrl,
                currentUser.getBalance(),
                xusername.getText().toString()
        );
        reference.setValue(user)
                .addOnCompleteListener(task ->
                        startActivity(new Intent(EditProfileAct.this, HomeAct.class))
                )
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Failed setValue: " + e.getMessage());
                });
    }
}
