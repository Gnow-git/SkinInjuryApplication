package com.example.skininjuryapplication.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.os.Bundle;

import com.example.skininjuryapplication.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FragmentManager fragmentManager;
    private MapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        fragmentManager = getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // googleMap 마커 지정
        LatLng location = new LatLng(36.625615, 127.454451);    // 충북대 소프트웨어
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("충북대 소프트웨어학과");
        markerOptions.snippet("학과 건물");
        markerOptions.position(location);
        googleMap.addMarker(markerOptions);
        // googleMap 확대 설정
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
    }
}