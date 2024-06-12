package com.example.loginregisterhomework;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private ImageView postImageView;
    private Button selectPostImageButton, savePostButton;
    private EditText postEditText;
    private Uri postImageUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        postImageView = findViewById(R.id.postImageView);
        selectPostImageButton = findViewById(R.id.selectPostImageButton);
        savePostButton = findViewById(R.id.savePostButton);
        postEditText = findViewById(R.id.postEditText);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        selectPostImageButton.setOnClickListener(v -> openFileChooser());

        savePostButton.setOnClickListener(v -> savePost());

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData()!= null) {
                        postImageUri = result.getData().getData();
                        postImageView.setImageURI(postImageUri);
                    }
                });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    private void savePost() {
        String postText = postEditText.getText().toString();

        if (TextUtils.isEmpty(postText) || postImageUri == null) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        //String userId = mAuth.getCurrentUser().getUid();
        StorageReference storageRef = storage.getReference("posts/" + System.currentTimeMillis() + ".jpg");

        UploadTask uploadTask = storageRef.putFile(postImageUri);
        uploadTask.addOnFailureListener(exception -> {
            Toast.makeText(AddPostActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                savePostToFirestore( postText, uri.toString());
            });
        });
    }

    private void savePostToFirestore( String postText, String postImageUrl) {
        Map<String, Object> post = new HashMap<>();
       // post.put("userId", userId);
        post.put("postText", postText);
        post.put("postImageUrl", postImageUrl);
        post.put("timestamp", System.currentTimeMillis());

        db.collection("posts").add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddPostActivity.this, "Post saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddPostActivity.this, "Failed to save post", Toast.LENGTH_SHORT).show();
                });
    }
}