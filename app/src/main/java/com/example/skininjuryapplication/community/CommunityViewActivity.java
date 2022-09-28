package com.example.skininjuryapplication.community;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.skininjuryapplication.R;

public class CommunityViewActivity extends AppCompatActivity {
    private Intent intent;
    String mtitle, mtext;
    TextView v_title, v_text;

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

    }
}