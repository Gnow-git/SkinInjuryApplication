package com.example.skininjuryapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.skininjuryapplication.R;

public class MainActivity extends AppCompatActivity {

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
    }
}