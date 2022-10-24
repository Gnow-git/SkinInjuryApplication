package com.example.skininjuryapplication.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skininjuryapplication.R;
import com.example.skininjuryapplication.user.LoginActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FragmentManager fragmentManager;
    private MapFragment mapFragment;
    private Intent intent;
    String mapName, mapAddress;
    double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        fragmentManager = getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        
        // 전달 받은 값 textview에 출력
        intent = getIntent();

        mapName = intent.getStringExtra("map_Name");
        mapAddress = intent.getStringExtra("map_Address");
        System.out.println("이름: " + mapName);
        System.out.println("주소: " + mapAddress);

        // 전달 받은 주소 -> 좌표로 변경
        Geocoder geocoder = new Geocoder(this);
        List<Address> mResultList = null;
        String Add = mapAddress;

        try {
            if(Add != null){
                mResultList = geocoder.getFromLocationName(Add, 1);
                if(mResultList.size()!=0){
                    lat = mResultList.get(0).getLatitude();
                    lon = mResultList.get(0).getLongitude();
                    System.out.println("변환 주소:" + mResultList);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("주소를 불러오지 못했습니다.");
        }

        // 지도 검색 버튼 구현
        Button btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(view -> {
            Intent intent = new Intent(GoogleMapActivity.this, GoogleMapSearchActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // googleMap 마커 지정
        //double lat = 36.625615, lon = 127.454451;
        LatLng location = new LatLng(lat, lon);    // 충북대 소프트웨어
        LatLng test_location = new LatLng(0.0, 0.0);
        System.out.println("위도" + lat);
        System.out.println("경도" + lon);
        System.out.println("위치" + location);
        System.out.println("테스트 위치" + test_location);

        if(location.longitude == 0.0 && location.latitude == 0){
            location = new LatLng(36.625615, 127.454451);
            Toast.makeText(GoogleMapActivity.this, "지도를 검색해주세요.", Toast.LENGTH_SHORT).show();
        }
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(mapName);
            markerOptions.snippet(mapAddress);
            markerOptions.position(location);
            googleMap.addMarker(markerOptions);
            // googleMap 확대 설정
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));

    }
}