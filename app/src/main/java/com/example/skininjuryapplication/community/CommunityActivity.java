package com.example.skininjuryapplication.community;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.skininjuryapplication.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class CommunityActivity extends AppCompatActivity {
    // 실시간 데이터 갱신

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<CommunityList, CommunityViewHolder> mFirebaseAdapter;
    public static final String MESSAGE_CHILD = "messages";

    // 리사이클러뷰에 보이게하기 위한 ViewHolder
    public static class CommunityViewHolder extends RecyclerView.ViewHolder {

        TextView mTitleEditText; // 제목
        TextView mMessageEditText;  // 내용

        public CommunityViewHolder(View v) {
            super(v);
            mTitleEditText = itemView.findViewById(R.id.list_title);
            mMessageEditText = itemView.findViewById(R.id.list_text);
        }
    }

    private RecyclerView mCommunityRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        mCommunityRecyclerView = findViewById(R.id.list_recycler_view);

        // Firebase 실시간 데이터베이스 초기화
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // 쿼리 수행 위치
        Query query = mFirebaseDatabaseReference.child(MESSAGE_CHILD);

        // 옵션
        FirebaseRecyclerOptions<CommunityList> options =
                new FirebaseRecyclerOptions.Builder<CommunityList>()
                        .setQuery(query, CommunityList.class)
                        .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<CommunityList,
                CommunityViewHolder>(options) {
            @Override
            protected void onBindViewHolder(CommunityViewHolder holder,
                                            int position, CommunityList model) {
                holder.mTitleEditText.setText(model.getTitle());
                holder.mMessageEditText.setText(model.getText());
            }

            @Override
            public CommunityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_community, parent, false);
                return new CommunityViewHolder(view);
            }
        };

        // 리사이클러뷰에 레이아웃 매니저와 어댑터 설정
        mCommunityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCommunityRecyclerView.setAdapter(mFirebaseAdapter);

        Button editBtn = findViewById(R.id.editButton);    // 게시물 작성 버튼
        editBtn.setOnClickListener(view -> {
            Intent i = new Intent(CommunityActivity.this, CommunityChatActivity.class);
            startActivity(i);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // FirebaseRecyclerAdapter 실시간 쿼리 시작
        mFirebaseAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // FirebaseRecyclerAdapter 실시간 쿼리 중지
        mFirebaseAdapter.stopListening();
    }
}