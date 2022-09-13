package com.example.skininjuryapplication.community;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.skininjuryapplication.R;

import java.sql.Array;
import java.util.ArrayList;

public class CommunityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        // 표현할 임의의 데이터
        ArrayList<CommunityList> data = new ArrayList<>();
        data.add(new CommunityList("피부 질환", "피부 고민", "사람1"));
        data.add(new CommunityList("여드름", "여드름", "사람2"));
        data.add(new CommunityList("아토피", "아토피 고민", "사람3"));
        data.add(new CommunityList("피부", "피부 고민", "사람4"));
        data.add(new CommunityList("피부 질환", "피부 고민", "사람5"));
        data.add(new CommunityList("여드름", "여드름", "사람6"));
        data.add(new CommunityList("아토피", "아토피 고민", "사람7"));
        data.add(new CommunityList("피부", "피부 고민", "사람8"));

        // 어댑터
        CommunityAdapter adapter = new CommunityAdapter(data);

        // 뷰와 어댑터 연결
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        // 아이템 클릭 이벤트
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CommunityActivity.this, position + " 번째 아이템 선택", Toast.LENGTH_SHORT).show();
            }
        });
    }
}