package com.example.skininjuryapplication.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.skininjuryapplication.MainActivity;
import com.example.skininjuryapplication.R;
import com.example.skininjuryapplication.community.CommunityActivity;
import com.example.skininjuryapplication.user.ProfileActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GoogleMapActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMarkerClickListener{

    MapBottomSheetDialog bottomSheetDialog;

    // 메소드 실행 : onCreate > onStart > onStart:call > onMapReady > startLocationUpdates : call
    private FragmentManager fragmentManager;
    private MapFragment mapFragment;
    private Intent intent;
    String mapName, mapAddress;
    double lat, lon;

    protected double latitude, longitude;


    private GoogleMap mMap;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000; // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
    private BottomNavigationView bottomNavigationView;

    /** onRequestPermissionsResult 에서 수신된 결과에서 ActivityCompat.requestPermission를
    사용한 요청을 구별하기 위해 사용됩니다.**/
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    /** 카메라 움직임 제어 **/
    private boolean cameraControl = false;

    boolean search_activity = false;
    boolean move_camera = true;    // 처음 실행시 카메라 이동
    boolean move_camera2 = false;   // GoogleMapSearchActivity 리스트 클릭시

    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION};    // 외부 저장소

    Location mCurrentLocation;
    LatLng currentPosition;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    private View mLayout;   // Snackbar 사용하기 위해서는 View가 필요(ex)Toast -> Context)

    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mLayout = findViewById(R.id.layout_google_map);


        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 구글 맵 프래그먼트 관리
        fragmentManager = getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        // 지도 검색 버튼 구현
        LinearLayout btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(view -> {
            Intent intent = new Intent(GoogleMapActivity.this, GoogleMapSearchActivity.class);
            startActivity(intent);
        });

        googleMapSearchProcess();


        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.action_home:
                        Intent intent_home = new Intent(GoogleMapActivity.this, MainActivity.class);
                        startActivity(intent_home);
                        break;
                    case R.id.action_map:
                        Intent intent_map = new Intent(GoogleMapActivity.this, GoogleMapActivity.class);
                        startActivity(intent_map);
                        break;
                    case R.id.action_community:
                        Intent intent_community = new Intent(GoogleMapActivity.this, CommunityActivity.class);
                        startActivity(intent_community);
                        break;
                    case R.id.action_profile:
                        Intent intent_profile = new Intent(GoogleMapActivity.this, ProfileActivity.class);
                        startActivity(intent_profile);
                        break;
                }
                return false;
            }
        });

    } // onCreate() 끝

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG,"onStart");

        if(checkPermission()) {
            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if(mMap!=null)
                // 파란 점 표시
                mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        Log.d(TAG, "OnMapReady:");

        mMap = googleMap;
        setDefaultLocation();

        /** 런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        지도의 초기위치를 지정한 곳으로 이동 **/


        /** 런타임 퍼미션 처리
         1. 위치 퍼미션을 가지고 있는지 체크합니다.**/
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
        hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED ) {
            /**
             * 2. 이미 퍼미션을 가지고 있다면
             * (안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요 없기 때문에 이미 허용된 걸로 인식합니다.)
             */

            startLocationUpdate();  // 3. 위치 업데이트 시작
        }else { // 2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])){
                // 3-2. 요청을 진행하기 전에 사용자에게 퍼미션이 필요한 이유를 설명합니다.

                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener(){

                            @Override
                    public void onClick(View view) {

                                // 3-3. 사용자에게 퍼미션 요청을 합니다. 요청 결과는
                                // onRequestPermissionResult 에서 수신됩니다.

                                ActivityCompat.requestPermissions(GoogleMapActivity.this, REQUIRED_PERMISSIONS,
                                        PERMISSIONS_REQUEST_CODE);
                            }
                }).show();
            }else{
                /** 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                 * 요청 결과는 onRequestPermissionResult 에서 수신됩니다.
                 */
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

        // 지도 location_button ui 설정 -> custom으로 제작(btn_gps) -> false
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        // 지도 location_button custom
        ImageButton btn_gps = findViewById(R.id.btn_gps);

        btn_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                // location 버튼 이미지 변경
                btn_gps.setSelected(!btn_gps.isSelected());

                if(btn_gps.isSelected() == true){   // btn_gps가 선택되면
                    cameraControl = true;   // 카메라 focusing
                    Toast.makeText(GoogleMapActivity.this, "내 위치 활성화", Toast.LENGTH_SHORT).show();
                }else if(btn_gps.isSelected() == false) { // 선택이 안되면
                    cameraControl = false;  // 카메라 focusing 해제
                    Toast.makeText(GoogleMapActivity.this, "내 위치 비활성화", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /** map 터치시 카메라 추적 중지 **/
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(GoogleMapActivity.this, "내 위치 비활성화", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onMapClick :");
                btn_gps.setSelected(false);
                cameraControl = false;
            }
        });

        /** GoogleMapSearchActivity 에서 상호작용시 **/
        if(search_activity == true){
            LatLng search_location = new LatLng(lat, lon);
        /** 해당 장소가 없을 시 **/
            if(search_location.longitude == 0.0 && search_location.latitude == 0){
                setDefaultLocation();   // 본인 위치로 이동
                Toast.makeText(GoogleMapActivity.this, "해당 주소를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }

            MarkerOptions search_markerOptions = new MarkerOptions();
            search_markerOptions.title(mapName);
            search_markerOptions.snippet(mapAddress);
            search_markerOptions.position(search_location);
            search_markerOptions.draggable(true);
            mMap.addMarker(search_markerOptions);
            if(move_camera2 == true){
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(search_location, 16));
                move_camera2 = false;
            }
        }

        mMap.setOnMarkerClickListener(this);
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if(locationList.size() > 0) {
                location = locationList.get(locationList.size() -1);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + "경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult:" + markerSnippet);

                    /** 현재 위치 계속 추적 -> 지도 터치시 작동 안하게 지정 **/
                    setCurrentLocation(location, markerTitle, markerSnippet);
                    mCurrentLocation = location;

            }
        }
    };

    /** 권한 확인**/
    private void startLocationUpdate() {
        if (!checkLocationServicesStatus()){
            Log.d(TAG, "sartLocationUpdates: call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
            hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "startLocationUpdate: 퍼미션 안가지고 있음");
                return;
            }

            Log.d(TAG, "startLocationUpdate : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            
            // my location button 표시
            if(checkPermission())
                mMap.setMyLocationEnabled(true);
        }
    }

    /** 지오코더 변환 함수 **/
    public String getCurrentAddress(LatLng latLng) {
        // 지오코더로 GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    1);
        } catch (IOException ioException) {
            // 네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if(addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    /** GPS 활성화 확인 **/
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /** 현재 위치 찾기 **/
    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet){
        if(currentMarker != null) currentMarker.remove();

            MarkerOptions markerOptions = new MarkerOptions();
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            markerOptions.position(currentLatLng);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            currentMarker = mMap.addMarker(markerOptions);

            if(move_camera == true) {  // 처음 실행시 한번만 카메라 이동
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));
                move_camera = false;
            }

        /** location button 클릭시 cameraControl = true 카메라 실시간 추척**/
        if(cameraControl == true){
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mMap.moveCamera(cameraUpdate);
        }
    }


    /** 기본 지정 위치 **/
    public void setDefaultLocation() {

        // 디폴트 위치, 충북대 소프트웨어
        LatLng DEFAULT_LOCATION = new LatLng(36.625615, 127.454451);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 여부를 확인하세요";

        if(currentMarker != null) currentMarker.remove();

        MarkerOptions default_markerOptions = new MarkerOptions();
        default_markerOptions.position(DEFAULT_LOCATION);
        default_markerOptions.title(markerTitle);
        default_markerOptions.snippet(markerSnippet);
        default_markerOptions.draggable(true);
        default_markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(default_markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);
    }


    // 런타임 퍼미션 처리를 위한 메소드
    private boolean checkPermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarsetLoctionPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
        hasCoarsetLoctionPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;
    }

    /**
     * ActivityCompat.requestPermissions 를 사용한 퍼미션 요청의 결과를 리턴받는 메소드
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                          @NonNull String[] permissions,
                                          @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length ==
                REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdate();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명 후 종료
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,
                        REQUIRED_PERMISSIONS[1])) {

                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있음
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                } else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을
                    // 다시 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }

    // 여기부터는 GPS 활성화를 위한 메소드
    private void showDialogForLocationServiceSetting(){

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            case GPS_ENABLE_REQUEST_CODE:

                // 사용자가 GPS 활성 시켰는지 검사
                if(checkLocationServicesStatus()){
                    if(checkLocationServicesStatus()){
                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");

                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        onStop();
    }

    // 백버튼을 눌러 앱 종료시 메소드 실행
    @Override
    protected void onStop(){

        super.onStop();

        if(mFusedLocationClient != null) {
            Log.d(TAG, "onStop :call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    /** GoogleMapSearchActivity 에서 리스트 항목 클릭 **/
    public void googleMapSearchProcess(){


        intent = getIntent();

        mapName = intent.getStringExtra("map_Name");
        mapAddress = intent.getStringExtra("map_Address");
        System.out.println("이름: " + mapName);
        System.out.println("주소: " + mapAddress);

        Geocoder search_geocoder = new Geocoder(this);
        List<Address> mResultList = null;
        String Add = mapAddress;

        try {
            if(mapAddress != null){
                mResultList = search_geocoder.getFromLocationName(Add, 1);
                if(mResultList.size()!=0){
                    lat = mResultList.get(0).getLatitude();
                    lon = mResultList.get(0).getLongitude();
                    System.out.println("변환 주소:" + mResultList);
                    currentPosition = new LatLng(lat,lon);

                    String markerSnippet = "위도:" + String.valueOf(lat) + "경도:" + String.valueOf(lon);

                    search_activity = true; // GoogleMapSearchActivity 에서 넘어갈 경우 -> onMapReady
                    cameraControl = false;
                    move_camera = false;
                    move_camera2 = true;
                    mCurrentLocation = location;
                }
            }
        } catch (IOException e) {
            System.out.println( mapAddress);
            e.printStackTrace();
            System.out.println("주소를 불러오지 못했습니다.");
        } // 전달 받은 주소 좌표 변경 끝
    }

    /** 마커 클릭 메소드 **/
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if(mapName == null ){   // 현재 위치는 bottom_sheet 에 안나타나게 지정
            Toast.makeText(this, "현재 위치는 나타낼 수 없습니다.", Toast.LENGTH_SHORT).show();
        }else if(mapName != null){  // 검색된 마커 클릭시 GoogleMapSearchActivity 의 정보를 bottom_sheet 로 전달
            Toast.makeText(this, mapName, Toast.LENGTH_SHORT).show();

            androidx.fragment.app.FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            Bundle bundle = new Bundle();
            String map_name = mapName;
            bundle.putString("map_name",mapName);
            MapBottomSheetDialog bottomSheetDialog = new MapBottomSheetDialog();
            bottomSheetDialog.setArguments(bundle);
            bottomSheetDialog.show(getSupportFragmentManager(), "BottomSheet");


        }else{
            Toast.makeText(this, "해당 위치 정보를 불러 올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}