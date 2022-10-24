package com.example.skininjuryapplication.community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.skininjuryapplication.R;
import com.example.skininjuryapplication.user.RegisterActivity;
import com.example.skininjuryapplication.user.UserAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommunityChatActivity extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ImageView mImageEdit;
    private EditText mTitleEditText;
    private EditText mMessageEditText;
    public static final String MESSAGE_CHILD = "List";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_chat);

        // Firebase 실시간 데이터베이스 초기화
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mTitleEditText = findViewById(R.id.title_edit);
        mMessageEditText = findViewById(R.id.message_edit);

        // 보내기 버튼
        findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {

                CommunityList chatMessage = new CommunityList(user.getEmail().toString(),mTitleEditText.getText().toString(),
                        mMessageEditText.getText().toString());

                mFirebaseDatabaseReference.child(MESSAGE_CHILD).child(getTime()).setValue(chatMessage);

                mTitleEditText.setText("");
                mMessageEditText.setText("");
                }
            }
        });
        // 목록 보기 버튼
        Button listBtn = findViewById(R.id.list_button);
        listBtn.setOnClickListener(view -> {
            Intent i = new Intent(CommunityChatActivity.this, CommunityActivity.class);
            startActivity(i);
        });
    }

    // 현재 시간을 yyyy-MM-dd hh:mm:ss로 표시하는 메서드
    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = dateFormat.format(date);

        return getTime;
    }
}