package com.example.skininjuryapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.skininjuryapplication.tflite.ClassifierWithModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GalleryActivity extends AppCompatActivity {
    public static final String TAG = "[IC]GalleryActivity";
    public static final int GALLERY_IMAGE_REQUEST_CODE = 1;

    private ClassifierWithModel cls;
    private ImageView imageView;
    private TextView textView;

    final int CAMERA = 100; // 카메라 선택시 인텐트로 보내는 값
    final int GALLERY = 101; // 갤러리 선택 시 인텐트로 보내는 값
    int imgFrom; // 이미지 어디서 가져왔는지 (카메라 or 갤러리)
    String imagePath = "";
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat imageDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
    Intent intent;

    ProgressDialog mProgressDialog;

    File imageFile = null; // 카메라 선택 시 새로 생성하는 파일 객체
    Uri imageUri;
    Uri uri;
    Bitmap bitmap1;

    FirebaseStorage storage = FirebaseStorage.getInstance(); // 파이어베이스 저장소 객체
    StorageReference reference = null; // 저장소 레퍼런스 객체 : storage 를 사용해 저장 위치를 설정

    @SuppressLint({"NonConstantResourceId", "QueryPermissionsNeeded"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Button selectBtn = findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(v -> getImageFromGallery());
        Button upload = findViewById(R.id.selectBtn);
        upload.setOnClickListener(v -> uploadImage());

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        cls = new ClassifierWithModel(this);
        try {
            cls.init();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void uploadImage() {
        //uri = getImageUri(getApplicationContext(), bitmap1);
        showProgressDialog("업로드 중");
        UploadTask uploadTask = null; // 파일 업로드하는 객체
        String timeStamp = imageDate.format(new Date()); // 중복 파일명을 막기 위한 시간스탬프
        String imageFileName = "IMAGE_" + timeStamp + "_.png"; // 파일명
        reference = storage.getReference().child("item").child(imageFileName); // 이미지 파일 경로 지정 (/item/imageFileName)
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        uploadTask = reference.putFile(imageUri); // 업로드할 파일과 업로드할 위치 설정
        //        파일 업로드 시작
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
    }

    private void getImageFromGallery(){
        /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
//        Intent intent = new Intent(Intent.ACTION_PICK,
//                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_IMAGE_REQUEST_CODE);*/
        //intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        //intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY) {
            if (data == null) {
                return;
            }

            imageUri = data.getData();
            imagePath = data.getDataString();
            //imageUri = data.getData();
            Bitmap bitmap = null;
            //Bitmap bitmap1 = (Bitmap) data.getExtras().get("data");

            try {
                if(Build.VERSION.SDK_INT >= 29) {
                    ImageDecoder.Source src =
                            ImageDecoder.createSource(getContentResolver(), imageUri);
                    bitmap = ImageDecoder.decodeBitmap(src);
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Failed to read Image", ioe);
            }

            if(bitmap != null) {
                Pair<String, Float> output = cls.classify(bitmap);
                String resultStr = String.format(Locale.ENGLISH,
                        "class : %s, prob : %.2f%%",
                        output.first, output.second * 100);

                textView.setText(resultStr);
                imageView.setImageBitmap(bitmap);
                //imageUri = getImageUri(getApplicationContext(), bitmap);
            }

        }
    }

    /*private Uri getImageUri(Context applicationContext, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(applicationContext.getContentResolver(), bitmap, "", null);
        return Uri.parse(path);
    }

    private void getPath(Uri imageUri) {
        String [] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, imageUri, proj, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        //System.out.println(cursor.getString(index));

        //imageFile = new File(cursor.getString(index));

        cursor.close();
    }*/

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