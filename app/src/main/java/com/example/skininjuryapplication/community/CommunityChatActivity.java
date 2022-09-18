package com.example.skininjuryapplication.community;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.skininjuryapplication.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CommunityChatActivity extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabaseReference;
    private EditText mTitleEditText;
    private EditText mMessageEditText;
    public static final String MESSAGE_CHILD = "messages";
    
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
                CommunityList chatMessage = new CommunityList(mTitleEditText.getText().toString(),
                        mMessageEditText.getText().toString());
                mFirebaseDatabaseReference.child(MESSAGE_CHILD).push().setValue(chatMessage);
                mTitleEditText.setText("");
                mMessageEditText.setText("");
            }
        });

        // 목록 보기 버튼
        Button listBtn = findViewById(R.id.list_button);
        listBtn.setOnClickListener(view -> {
            Intent i = new Intent(CommunityChatActivity.this, CommunityActivity.class);
            startActivity(i);
        });
    }
}