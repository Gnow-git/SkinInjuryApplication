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

import com.example.skininjuryapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> mResultList = geocoder.getFromLocationName("충북대학교", 1);
            double lat = mResultList.get(0).getLatitude();
            double lon = mResultList.get(0).getLongitude();
            System.out.println("위 경도" + lat + lon);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("실패");
        }


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // googleMap 마커 지정
        //double lat = 36.625615, lon = 127.454451;
        LatLng location = new LatLng(lat, lon);    // 충북대 소프트웨어
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(mapName);
        markerOptions.snippet(mapAddress);
        markerOptions.position(location);
        googleMap.addMarker(markerOptions);
        // googleMap 확대 설정
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
    }
}