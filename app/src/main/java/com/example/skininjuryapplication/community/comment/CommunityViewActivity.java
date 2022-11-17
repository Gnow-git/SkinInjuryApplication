package com.example.skininjuryapplication.community.comment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skininjuryapplication.R;
import com.example.skininjuryapplication.community.CommunityActivity;
import com.example.skininjuryapplication.community.CommunityChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// 게시글 상세보기 Activity, 추후에 수정 삭제 기능 추가할 것
public class CommunityViewActivity extends AppCompatActivity {
    private Intent intent;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CommentList> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private EditText comment_text;
    String mtitle, mtext;
    private TextView v_title, v_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_community_view);

     // 전달 받은 값 textview에 출력
     intent = getIntent();

     mtitle = intent.getStringExtra("title");
     mtext = intent.getStringExtra("text");

     v_title = (TextView) findViewById(R.id.v_title);
     v_text = (TextView) findViewById(R.id.v_text);

     // textview에 해당 값 넣기
     v_title.setText(mtitle);
     v_text.setText(mtext);

     /** recyclerview start **/
    recyclerView = findViewById(R.id.list_recycler_view);   // recyclerview 연결
    recyclerView.setHasFixedSize(true); /// recyclerview 성능 강화
    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    arrayList = new ArrayList<>();  // CommentList 객체를 담을 arraylist(adapter 쪽으로)

    database = FirebaseDatabase.getInstance();  // firebase database 연동
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    /** 메일 앞부분만 추출 **/
    String mail_front = user.getEmail();
    int idx = mail_front.indexOf("@");
    String mail = mail_front.substring(0, idx);

    String title = mtitle.toString();

    String comment_path = title + mail; // 답글 달릴 경로

    databaseReference = database.getReference("Comment").child(title);  // DB TABLE 연결
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
        /** recyclerview end**/

        CommentList commentList = new CommentList();
        comment_text = findViewById(R.id.comment_text);
        findViewById(R.id.comment_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comment_text != null){
                    commentList.setEmail(user.getEmail().toString());
                    commentList.setText(comment_text.getText().toString());

                    databaseReference.child(getTime()).setValue(commentList);
                    Toast.makeText(CommunityViewActivity.this, "댓글을 등록했습니다.", Toast.LENGTH_SHORT).show();
                }else   Toast.makeText(CommunityViewActivity.this, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show();

            }
        });

        // 게시물 리스트로 이동 버튼
        Button listbtn = findViewById(R.id.btn_list);
        listbtn.setOnClickListener(view -> {
            Intent i = new Intent(CommunityViewActivity.this, CommunityActivity.class);
            startActivity(i);
        });
    }
    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = dateFormat.format(date);

        return getTime;
    }


}