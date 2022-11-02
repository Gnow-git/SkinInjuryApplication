package com.example.skininjuryapplication.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
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
import java.util.Locale;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FragmentManager fragmentManager;
    private MapFragment mapFragment;
    private Intent intent;
    // gps로 현재 위치 불러오기
    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION};

    String mapName, mapAddress;
    double lat, lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        // 현재 위치 표시
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }


        final TextView textview_gps = (TextView)findViewById(R.id.gps_textview);

        Button btn_gps = (Button) findViewById(R.id.btn_gps);
        btn_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsTracker = new GpsTracker(GoogleMapActivity.this);

                lat = gpsTracker.getLatitude();
                lon = gpsTracker.getLongitude();

                String address = getCurrentAddress(lat, lon);
                textview_gps.setText(address);
            }
        });

        // 현재 위치 표시 끝

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
        System.out.println("위도" + lat);
        System.out.println("경도" + lon);
        System.out.println("위치" + location);

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

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                ;
                // 위치 값을 가져올 수 있음
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다. 2가지의 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(GoogleMapActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();

                } else {

                    Toast.makeText(GoogleMapActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission(){
        
        // 런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(GoogleMapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(GoogleMapActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
        hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){

            // 2. 이미 퍼미션을 가지고 있다면
            // 안드로이드 6.0 이하 버전은 퍼미션 필요 없으므로 허용된 걸로 인식

            // 3. 위치 값을 가져올 수 있습니다.

        } else {
            // 2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다.
            // 3. 2가지 경우
            // 3-1. 사용자가 퍼미션 거부를 한 적 있을 경우
            if(ActivityCompat.shouldShowRequestPermissionRationale(GoogleMapActivity.this,
                    REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자에게 퍼미션이 필요한 이유를 설명
                Toast.makeText(GoogleMapActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자에게 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionsResult 에서 수신됩니다.
                ActivityCompat.requestPermissions(GoogleMapActivity.this,
                        REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);

            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(GoogleMapActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    public String getCurrentAddress(double latitude, double longitude) {

        // 지오코더를 이용하여 GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
              latitude,
              longitude,
              7);
        } catch (IOException ioException) {
            // 네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용 불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if(addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }

    // GPS 활성화를 위한 메소드
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(GoogleMapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
        + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                // 사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()){

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}