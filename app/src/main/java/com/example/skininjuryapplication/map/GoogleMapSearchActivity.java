package com.example.skininjuryapplication.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.example.skininjuryapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class GoogleMapSearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
   // private RecyclerView.Adapter adapter;
    private MapAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    // arraylist MapList로 설정
    private ArrayList<MapList> arrayList, filterdList;
    // database 설정
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    EditText searchET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_search);
        
        // 검색시 리스트를 보여줄 listview 연결
        recyclerView = findViewById(R.id.map_list_view);
        // recyclerview 성능 강화
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // MapList 객체를 담을 arrayList
        arrayList = new ArrayList<>();
        // 검색된 객체를 담을 arrayList
        filterdList = new ArrayList<>();

        // 검색
        searchET = findViewById(R.id.search_map);
        // firebase database 연동
        database = FirebaseDatabase.getInstance();

        // DB TABLE 연결
        databaseReference = database.getReference("Map");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // firebase database data 받아오기
                // 배열 리스트 초기화
                arrayList.clear();
                // 반복문을 통해 데이터 리스트 추출
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // 만들어둔 maplist 데이터 담기
                    MapList mapList = snapshot.getValue(MapList.class);
                    // 담음 데이터를 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                    arrayList.add(mapList);

                    // 바뀔 데이터 설정
                    adapter.notifyDataSetChanged();

                }
                // 검색
                searchET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        String searchText = searchET.getText().toString();
                        searchFilter(searchText);
                    }
                });
            }

            // 검색 필터 설정
            public void searchFilter(String searchText) {
                filterdList.clear();

                for(int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getMapName().toLowerCase().contains(searchText.toLowerCase())){
                        filterdList.add(arrayList.get(i));
                    }
                }
                adapter.filterList(filterdList);
            }

            // 에러 발생 시 에러 출력
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("GoogleMapListActivity", String.valueOf(error.toException()));
            }
        });

        // 어댑터 설정, MapAdapter로 접근
        adapter = new MapAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);   // recyclerView에 adapter 연결
    }



}