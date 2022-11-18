package com.example.skininjuryapplication.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skininjuryapplication.R;
import com.example.skininjuryapplication.community.CommunityActivity;
import com.example.skininjuryapplication.community.comment.CommentAdapter;
import com.example.skininjuryapplication.community.comment.CommentList;
import com.example.skininjuryapplication.community.comment.CommunityViewActivity;
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

public class MapDetailActivity extends AppCompatActivity {
    private Intent intent;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MapReviewList> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private EditText review_text;
    String mname, maddress, mnum;
    ImageView bs_image;
    TextView bs_title, bs_address, bs_number;
    private TextView v_name, v_address, v_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map_detail);

    // 전달 받은 값 textview에 출력
    intent = getIntent();

    mname = intent.getStringExtra("map_name");
    maddress = intent.getStringExtra("address");
    mnum = intent.getStringExtra("mapNum");

    v_name = (TextView) findViewById(R.id.bs_title);
    v_address = (TextView) findViewById(R.id.bs_address);
    v_num = (TextView) findViewById(R.id.bs_number);

    // textview에 해당 값 넣기
    v_name.setText(mname);
    v_address.setText(maddress);
    v_num.setText(mnum);

    /** recyclerview start **/
    recyclerView = findViewById(R.id.list_recycler_view);   // recyclerview 연결
    recyclerView.setHasFixedSize(true); /// recyclerview 성능 강화
    layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    arrayList = new ArrayList<>();  // CommentList 객체를 담을 arraylist(adapter 쪽으로)

    database = FirebaseDatabase.getInstance();  // firebase database 연동
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    String name = mname.toString();


    databaseReference = database.getReference("MapReview").child(name);  // DB TABLE 연결
    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            // firebase database의 data를 받아오는 곳.
            arrayList.clear();  // 기존 배열리스트 초기화
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {  // 반복문을 통해 데이터 리스트 추출
                MapReviewList mapReviewList = snapshot.getValue(MapReviewList.class);   // 만들어둔 객체에 데이터 담기
                arrayList.add(mapReviewList);   // 담은 데이터를 배열리스트에 넣고 리사이클러뷰로 보낼 준비
            }
            adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            // DB를 가져오던 중 에러 발생 시
            Log.e("MapDetailActivity", String.valueOf(error.toException()));    // 에러문 출력
        }
    });

    // 어댑터 설정
    adapter = new MapReviewAdapter(arrayList, this);    //CommunityAdapter의 CommunityAdapter로 접근
    recyclerView.setAdapter(adapter);   // recyclerview에 adapter 연결
    /** recyclerview end**/

    MapReviewList mapReviewList = new MapReviewList();
        review_text = findViewById(R.id.review_text);
    findViewById(R.id.review_btn).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(review_text != null){
                mapReviewList.setEmail(user.getEmail().toString());
                mapReviewList.setText(review_text.getText().toString());

                databaseReference.child(getTime()).setValue(mapReviewList);
                Toast.makeText(MapDetailActivity.this, "리뷰를 등록했습니다.", Toast.LENGTH_SHORT).show();
            }else   Toast.makeText(MapDetailActivity.this, "리뷰를 입력해주세요.", Toast.LENGTH_SHORT).show();

        }
    });

}
private String getTime() {
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String getTime = dateFormat.format(date);

    return getTime;
    }

    /** 병원 정보 불러오기 **/
    private void readHospital_info(String map_name) {
        databaseReference.child(map_name).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                String address = (String) datasnapshot.child("address").getValue();
                String Num = (String) datasnapshot.child("mapNum").getValue();
                bs_title.setText(map_name);
                bs_address.setText(address);
                bs_number.setText(Num);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}