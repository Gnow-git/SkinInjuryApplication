package com.example.skininjuryapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.skininjuryapplication.community.CommunityActivity;
import com.example.skininjuryapplication.community.CommunityChatActivity;
import com.example.skininjuryapplication.user.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button galleryBtn = findViewById(R.id.galleryBtn);  // 갤러리 버튼을 누를 때 이벤트
        galleryBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, GalleryActivity.class);
            startActivity(i);
        });

        Button cameraBtn = findViewById(R.id.cameraBtn);    // 카메라 버튼
        cameraBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(i);
        });

        // 로그아웃 처리

        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(view -> {
            mFirebaseAuth.signOut();

            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            //finish();
        });

        Button btn_community = findViewById(R.id.btn_community);
        btn_community.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CommunityActivity.class);
            startActivity(intent);
        });

    }
}