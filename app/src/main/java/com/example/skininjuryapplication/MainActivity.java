package com.example.skininjuryapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.skininjuryapplication.community.CommunityActivity;
import com.example.skininjuryapplication.community.CommunityAdapter;
import com.example.skininjuryapplication.community.CommunityChatActivity;
import com.example.skininjuryapplication.community.CommunityList;
import com.example.skininjuryapplication.map.GoogleMapActivity;
import com.example.skininjuryapplication.map.GoogleMapSearchActivity;
import com.example.skininjuryapplication.user.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CommunityList> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout galleryBtn = findViewById(R.id.galleryBtn);  // 갤러리 버튼을 누를 때 이벤트
        galleryBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, GalleryActivity.class);
            startActivity(i);
        });

        LinearLayout cameraBtn = findViewById(R.id.cameraBtn);    // 카메라 버튼
        cameraBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(i);
        });

/*        // 로그아웃 처리

        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(view -> {
            mFirebaseAuth.signOut();

            Intent i = new Intent(MainActivity.this, LoginActivity.class);

            startActivity(i);
            //finish();
        });*/

        LinearLayout btn_community = findViewById(R.id.btn_community);
        btn_community.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CommunityActivity.class);
            startActivity(intent);
        });

        LinearLayout btn_googlemap = findViewById(R.id.btn_googlemap);
        btn_googlemap.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GoogleMapActivity.class);
            startActivity(intent);
        });

        LinearLayout search_map = findViewById(R.id.search_map);
        search_map.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GoogleMapSearchActivity.class);
            startActivity(intent);
        });

    }
}