package com.example.skininjuryapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skininjuryapplication.tflite.ClassifierWithModel;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {
    //카메라 앱을 실행하기 위한 요청에 사용할 코드
    public static final String TAG = "[IC]CameraActivity";
    public static final int CAMERA_IMAGE_REQUEST_CODE = 1;
    private static final String KEY_SELECTED_URI = "KEY_SELECTED_URI";

    private ClassifierWithModel cls;
    private ImageView imageView;
    private TextView textView;

    Uri selectedImageUri;   // 이미지를 받아올 Uri

    @Override
    //UI 컨트롤 및 Classifier 가져오기
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Button takeBtn = findViewById(R.id.takeBtn);
        takeBtn.setOnClickListener(v -> getImageFromCamera());

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        cls = new ClassifierWithModel(this);
        try {
            cls.init();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }

        //액티비티가 죽어도 selectedImageUri 값 유지
        if(savedInstanceState != null){
            Uri uri = savedInstanceState.getParcelable(KEY_SELECTED_URI);
            if (uri != null)
                selectedImageUri = uri;
        }
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
    private void getImageFromCamera(){
        //사용자에게 보여주면 되므로 앱에서만 접근할 수 있는 영역에 저장
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "picture.jpg");
        if(file.exists()) file.delete();
        selectedImageUri = FileProvider.getUriForFile(this, getPackageName(), file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
        startActivityForResult(intent, CAMERA_IMAGE_REQUEST_CODE);
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
                        "class : %s, prob : %.2f%%", output.first, output.second * 100);

                imageView.setImageBitmap(bitmap);
                textView. setText(resultStr);
            }
        }
    }

    @Override
    protected void onDestroy() {
        cls.finish();
        super.onDestroy();
    }
}