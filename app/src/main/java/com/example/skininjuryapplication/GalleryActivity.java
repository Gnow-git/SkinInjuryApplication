package com.example.skininjuryapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skininjuryapplication.R;
import com.example.skininjuryapplication.tflite.ClassifierWithModel;

import java.io.IOException;
import java.util.Locale;

public class GalleryActivity extends AppCompatActivity {
    public static final int GALLERY_IMAGE_REQUEST_CODE =1;  // 요청에 사용할 코드
    public static final String TAG = "[IC]GalleryActivity";

    private ClassifierWithModel cls;    //tflite 분류 모델
    private ImageView imageView;    //선택된 이미지 출력
    private TextView textView;  //추론 결과 출력

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Button selectBtn = findViewById(R.id.selectBtn);    //버튼 연결
        selectBtn.setOnClickListener(v -> getImageFromGallery());   //누르면 갤러리 창으로 이동

        imageView = findViewById(R.id.imageView);   //이미지 뷰 연결
        textView = findViewById(R.id.textView);    //텍스트 연결

        cls = new ClassifierWithModel(this);    //init 호출
        try {
            cls.init();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    //이미지를 선택하도록 선택기 UI를 실행하는 함수
    private void getImageFromGallery() {
        //기기에 저장된 모든 이미지를 가져온다.(MIME 기준)
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        startActivityForResult(intent, GALLERY_IMAGE_REQUEST_CODE); // 기기에 설치된 사진 관련 앱 실행하여 이미지 선택
    }

    @Override
    // 이미지를 선택했을 때 선택 UI가 종료되고 GalleryActivity가 그 이미지를 받아서 처리
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //requestCode: 요청 코드
        //resultCode: 처리 결과
        //전달된 결과 검증
        if (resultCode == Activity.RESULT_OK &&
                requestCode == GALLERY_IMAGE_REQUEST_CODE) {
            if (data == null) {
                return;
            }

            //이미지에 접근할 수 있는 Uri 획득
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;

            //기기의 이미지를 Bitmao으로 불러오기
            try {
                if (Build.VERSION.SDK_INT >= 29) {
                    //안드로이드 버전이 29이상이면 ImageDecoder 사용
                    ImageDecoder.Source src =
                            ImageDecoder.createSource(getContentResolver(), selectedImage);
                    bitmap = ImageDecoder.decodeBitmap(src);
                } else {    //아니라면 getBitmap 사용
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Failed to read Image", ioe);
            }

            //추론에 사용한 이미지, 결과 문구룰 출력
            if (bitmap != null) {
                Pair<String, Float> output = cls.classify(bitmap);
                String resultStr = String.format(Locale.ENGLISH,
                        "class : %s, prob : %.2f%%",
                        output.first, output.second * 100);

                textView.setText(resultStr);
                imageView.setImageBitmap(bitmap);
            }

        }
    }

    @Override
    protected void onDestroy() {
        cls.finish();
        super.onDestroy();
    }
}