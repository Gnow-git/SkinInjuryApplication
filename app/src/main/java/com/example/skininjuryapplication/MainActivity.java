package com.example.skininjuryapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.skininjuryapplication.community.CommunityActivity;
import com.example.skininjuryapplication.community.CommunityAdapter;
import com.example.skininjuryapplication.community.CommunityChatActivity;
import com.example.skininjuryapplication.community.CommunityList;
import com.example.skininjuryapplication.map.GoogleMapActivity;
import com.example.skininjuryapplication.user.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
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

        Button btn_googlemap = findViewById(R.id.btn_googlemap);
        btn_googlemap.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GoogleMapActivity.class);
            startActivity(intent);
        });


        recyclerView = findViewById(R.id.list_recycler_view);   // recyclerview 연결
        recyclerView.setHasFixedSize(true); /// recyclerview 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();  // CommunityList 객체를 담을 arraylist(adapter 쪽으로)

        database = FirebaseDatabase.getInstance();  // firebase database 연동

        databaseReference = database.getReference("List");  // DB TABLE 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // firebase database의 data를 받아오는 곳.
                arrayList.clear();  // 기존 배열리스트 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {  // 반복문을 통해 데이터 리스트 추출
                    CommunityList communityList = snapshot.getValue(CommunityList.class);   // 만들어둔 객체에 데이터 담기
                    arrayList.add(communityList);   // 담은 데이터를 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // DB를 가져오던 중 에러 발생 시
                Log.e("CommunityActivity", String.valueOf(error.toException()));    // 에러문 출력
            }
        });

        // 어댑터 설정
        adapter = new CommunityAdapter(arrayList, this);    //CommunityAdapter의 CommunityAdapter로 접근
        recyclerView.setAdapter(adapter);   // recyclerview에 adapter 연결

    }
}