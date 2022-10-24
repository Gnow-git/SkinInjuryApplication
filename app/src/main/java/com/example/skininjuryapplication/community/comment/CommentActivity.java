package com.example.skininjuryapplication.community.comment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skininjuryapplication.R;
import com.example.skininjuryapplication.community.CommunityChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CommentList> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

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
                    CommentList commentList = snapshot.getValue(CommentList.class);   // 만들어둔 객체에 데이터 담기
                    arrayList.add(commentList);   // 담은 데이터를 배열리스트에 넣고 리사이클러뷰로 보낼 준비
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
        adapter = new CommentAdapter(arrayList, this);    //CommunityAdapter의 CommunityAdapter로 접근
        recyclerView.setAdapter(adapter);   // recyclerview에 adapter 연결

        // 게시물 작성 버튼
        Button editBtn = findViewById(R.id.editButton);
        editBtn.setOnClickListener(view -> {
            Intent i = new Intent(CommentActivity.this, CommunityChatActivity.class);
            startActivity(i);
        });
    }
}