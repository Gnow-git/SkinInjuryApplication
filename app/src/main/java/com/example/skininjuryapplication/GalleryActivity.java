package com.example.skininjuryapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorSpace;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GalleryActivity extends AppCompatActivity {
    public static final String TAG = "[IC]GalleryActivity";
    public static final int GALLERY_IMAGE_REQUEST_CODE = 1;

    private ClassifierWithModel cls;
    private ImageView imageView;
    private TextView textView, textView2, textView3;
    ProgressDialog mProgressDialog;
    String imagePath, disease;
    Uri selectedImage;
    SimpleDateFormat imageDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
    // Firebase Storage 등록 문제 때문에 일단 주석 처리
    FirebaseStorage storage = FirebaseStorage.getInstance(); // 파이어베이스 저장소 객체
    StorageReference reference = null; // 저장소 레퍼런스 객체 : storage 를 사용해 저장 위치를 설정
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Uri selectedImageUri;   // 이미지를 받아올 Uri
    String email = user.getEmail().toString();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Button selectBtn = findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(v -> getImageFromGallery());
        Button upload = findViewById(R.id.btnUpload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);

        cls = new ClassifierWithModel(this);
        try {
            cls.init();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void uploadImage() {
        // Firebase Storage 등록 문제 때문에 일단 주석 처리
        showProgressDialog("업로드 중");
        UploadTask uploadTask = null; // 파일 업로드하는 객체
        String timeStamp = imageDate.format(new Date()); // 중복 파일명을 막기 위한 시간스탬프
        String imageFileName = "IMAGE_" + timeStamp + "_.png"; // 파일명
        reference = storage.getReference().child("item").child(email).child(imageFileName); // 이미지 파일 경로 지정 (/item/imageFileName)
        uploadTask = reference.putFile(selectedImage); // 업로드할 파일과 업로드할 위치 설정

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
                uploadTask = reference.putFile(selectedImage); // 업로드할 파일과 업로드할 위치 설정

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
                Toast.makeText(GalleryActivity.this, "고객님의 공헌에 감사드립니다!", Toast.LENGTH_SHORT).show();
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

    private void getImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
//        Intent intent = new Intent(Intent.ACTION_PICK,
//                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK &&
                requestCode == GALLERY_IMAGE_REQUEST_CODE) {
            if (data == null) {
                return;
            }

            selectedImage = data.getData();
            Bitmap bitmap = null;
            imagePath = data.getDataString();

            try {
                if(Build.VERSION.SDK_INT >= 29) {
                    ImageDecoder.Source src =
                            ImageDecoder.createSource(getContentResolver(), selectedImage);
                    bitmap = ImageDecoder.decodeBitmap(src);
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                }
            } catch (IOException ioe) {
                Log.e(TAG, "Failed to read Image", ioe);
            }

            if(bitmap != null) {
                Pair<String, Float> output = cls.classify(bitmap);
                //String resultStr = String.format(Locale.ENGLISH,
                //"class : %s, prob : %.2f%%",
                //output.first, output.second * 100);
                String resultStr = String.format(Locale.ENGLISH,
                        "병명 : %s, 확률 : %.2f%%",
                        output.first, output.second * 100);

                disease = output.first;
                textView.setText(resultStr);
                imageView.setImageBitmap(bitmap);
                getCourseDetails(disease);
            }

        }
    }

    private void getCourseDetails(String courseId) {
        // url to post our data
        String URL = "http://ourhosting0113.dothome.co.kr/config.php";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(GalleryActivity.this);

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
                        Toast.makeText(GalleryActivity.this, "Please enter valid id.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(GalleryActivity.this, "Fail to get course" + error, Toast.LENGTH_SHORT).show();

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