package com.example.skininjuryapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.skininjuryapplication.tflite.ClassifierWithModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CameraActivity extends AppCompatActivity {
    //카메라 앱을 실행하기 위한 요청에 사용할 코드
    public static final String TAG = "[IC]CameraActivity";
    public static final int CAMERA_IMAGE_REQUEST_CODE = 1;
    private static final String KEY_SELECTED_URI = "KEY_SELECTED_URI";

    private ClassifierWithModel cls;
    private ImageView imageView;
    private TextView textView, textView2, textView3;
    private Button btnUpload;
    String disease;
    String imagePath ="";
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat imageDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Intent intent;
    ProgressDialog mProgressDialog;

    File imageFile = null; // 카메라 선택 시 새로 생성하는 파일 객체

    // Firebase Storage 등록 문제 때문에 일단 주석 처리
    FirebaseStorage storage = FirebaseStorage.getInstance(); // 파이어베이스 저장소 객체
    StorageReference reference = null; // 저장소 레퍼런스 객체 : storage 를 사용해 저장 위치를 설정
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Uri selectedImageUri;   // 이미지를 받아올 Uri
    String email = user.getEmail().toString();
    @Override
    //UI 컨트롤 및 Classifier 가져오기
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Button takeBtn = findViewById(R.id.takeBtn);
        takeBtn.setOnClickListener(v -> {
            try {
                getImageFromCamera();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        btnUpload = findViewById(R.id.btnUpload);

        cls = new ClassifierWithModel(this);
        try {
            cls.init();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int permissionResult= checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(permissionResult== PackageManager.PERMISSION_DENIED){
                String[] permissions= new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,10);
            }
        }

        //액티비티가 죽어도 selectedImageUri 값 유지
        if(savedInstanceState != null){
            Uri uri = savedInstanceState.getParcelable(KEY_SELECTED_URI);
            if (uri != null)
                selectedImageUri = uri;
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadtoserver();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 10:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "외부 메모리 읽기/쓰기 사용 가능", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "외부 메모리 읽기/쓰기 제한", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void uploadtoserver() {
        // Firebase Storage 등록 문제 때문에 일단 주석 처리
        showProgressDialog("업로드 중");
        UploadTask uploadTask = null;
        reference = storage.getReference().child("item").child(email).child(imageFile.getName()); // imageFile.toString()을 할 경우 해당 파일의 경로 자체가 불러와짐
        uploadTask = reference.putFile(Uri.fromFile(imageFile)); // 업로드할 파일과 업로드할 위치 설정
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//            업로드 성공 시 동작
                hideProgressDialog();
                Log.d(TAG, "onSuccess: upload");
                //downloadUri(); // 업로드 성공 시 업로드한 파일 Uri 다운받기
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                업로드 실패 시 동작
                hideProgressDialog();
                Log.d(TAG, "onFailure: upload");
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("이 버튼을 누르면 고객님의 정보가 우리의 앱 성능을 높이는 데 공헌할 것이라 믿습니다.\n" +
                "사진을 저희 서버로 업로드하겠습니까?");
        builder.setIcon(R.drawable.warning);

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Firebase Storage 등록 문제 때문에 일단 주석 처리
                showProgressDialog("업로드 중");
                UploadTask uploadTask = null; // 파일 업로드하는 객체
                String timeStamp = imageDate.format(new Date()); // 중복 파일명을 막기 위한 시간스탬프
                String imageFileName = "IMAGE_" + timeStamp + "_.png"; // 파일명
                reference = storage.getReference().child("item").child(imageFileName); // 이미지 파일 경로 지정 (/item/imageFileName)
                uploadTask = reference.putFile(selectedImageUri); // 업로드할 파일과 업로드할 위치 설정

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//            업로드 성공 시 동작
                        hideProgressDialog();
                        Log.d(TAG, "onSuccess: upload");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                업로드 실패 시 동작
                        hideProgressDialog();
                        Log.d(TAG, "onFailure: upload");
                    }
                });
                Toast.makeText(CameraActivity.this, "고객님의 공헌에 감사드립니다!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    //CameraActivity 종료 방지 -> 이미지 정상 로드 불가능
    protected void onSaveInstanceState(@NonNull Bundle outState){
        //액티비티가 종료될 때 호출
        //앱이 다시 실행되었을 때 저장 값 사용 가능
        super.onSaveInstanceState(outState);

        outState.putParcelable(KEY_SELECTED_URI, selectedImageUri);
    }

    //카메라 앱을 실행
    private void getImageFromCamera() throws IOException {
        //사용자에게 보여주면 되므로 앱에서만 접근할 수 있는 영역에 저장
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null) {
            imageFile = createImageFile();
            if (imageFile != null) {
                selectedImageUri = FileProvider.getUriForFile(this, getPackageName(), imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
                startActivityForResult(intent, CAMERA_IMAGE_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = imageDate.format(new Date()); // 파일명 중복을 피하기 위한 "yyyyMMdd_HHmmss"꼴의 timeStamp
        String fileName = "IMAGE_" + timeStamp; // 이미지 파일 명
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = File.createTempFile(fileName,
                ".jpg",
                storageDir); // 이미지 파일 생성
        imagePath = file.getAbsolutePath(); // 파일 절대경로 저장하기
        return file;
    }

    // 이미지 가져오기, galleryActivity와 유사
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_IMAGE_REQUEST_CODE) {

            Bitmap bitmap = null;
            try{
                if(Build.VERSION.SDK_INT >= 29){
                    ImageDecoder.Source src = ImageDecoder.createSource(
                            getContentResolver(), selectedImageUri);
                    bitmap = ImageDecoder.decodeBitmap(src);
                }else{
                    bitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), selectedImageUri);
                }
            } catch (IOException ioe){
                Log.e(TAG, "Failed to read Image", ioe);
            }

            if(bitmap != null) {
                Pair<String, Float> output = cls.classify(bitmap);
                String resultStr = String.format(Locale.ENGLISH,
                        "병명 : %s, 확률 : %.2f%%", output.first, output.second * 100);

                disease = output.first;
                imageView.setImageBitmap(bitmap);
                textView. setText(resultStr);
                getCourseDetails(disease);
            }
        }
    }

    private void getCourseDetails(String courseId) {
        // url to post our data
        String URL = "http://ourhosting0113.dothome.co.kr/config.php";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(CameraActivity.this);

        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, URL , new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // on below line passing our response to json object.
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    // on below line we are checking if the response is null or not.
                    if (!success) {
                        // displaying a toast message if we get error
                        Toast.makeText(CameraActivity.this, "Please enter valid id.", Toast.LENGTH_SHORT).show();
                    } else {
                        // if we get the data then we are setting it in our text views in below line.
                        textView2.setText(jsonObject.getString("symptom"));
                        textView3.setText(jsonObject.getString("treatment"));
                    }
                    // on below line we are displaying
                    // a success toast message.
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CameraActivity.this, "Fail to get course" + error, Toast.LENGTH_SHORT).show();

            }

        }) {
            @Override
            public String getBodyContentType() {
                // as we are passing data in the form of url encoded
                // so we are passing the content type below
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                // below line we are creating a map for storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key and value pair to our parameters.
                params.put("name", courseId);

                // at last we are returning our params.
                return params;
            }
        };

        //request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // below line is to make
        // a json object request.
        queue.add(request);
    }

    @Override
    protected void onDestroy() {
        cls.finish();
        super.onDestroy();
    }

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}