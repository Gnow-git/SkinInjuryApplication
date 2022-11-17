package com.example.skininjuryapplication.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.skininjuryapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증 처리
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText mEtEmail, mName, mEtPwd, mEtPwd_check, mAge;  // 회원가입 입력필드
    private RadioButton radio_men, radio_women; // 성별 체크
    private RadioGroup radioGroup;  // 라디오 그룹
    private Button mBtnRegister;        // 회원가입 버튼
    private Button mBtnLogin;   // 로그인 버튼
    private String gender;      // 성별
    private boolean pwd_equals; // 패스워드 확인

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SkinInjuryApplication");

        mEtEmail = findViewById(R.id.et_email);
        mName = findViewById(R.id.et_name);
        mAge = findViewById(R.id.et_age);
        mEtPwd = findViewById(R.id.et_pwd);
        mEtPwd_check = findViewById(R.id.et_pwd_check);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        mBtnRegister = findViewById(R.id.btn_register);
        mBtnLogin = findViewById(R.id.btn_login);

        mBtnRegister.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                
                // 회원가입 처리 시작
                String strEmail = mEtEmail.getText().toString();
                String strName = mName.getText().toString();
                String strAge = mAge.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                String strPwd_check = mEtPwd_check.getText().toString();

                if(strPwd.equals(strPwd_check)){
                    pwd_equals = true;
                }else pwd_equals = false;
                // Firebase Auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (pwd_equals == true) {
                            if (task.isSuccessful() ) {
                                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                UserAccount account = new UserAccount();
                                account.setIdToken(firebaseUser.getUid());
                                account.setEmailId(firebaseUser.getEmail());
                                account.setUserName(strName);
                                account.setAge(strAge);
                                account.setGender(gender);
                                account.setPassword(strPwd);

                                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);

                                Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                            mEtPwd.setText("");
                            mEtPwd_check.setText("");
                        }

                    }
                });
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();   // Register 종료
            }
        });
    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(checkedId == R.id.radio_men){
                gender = "남자";
            }else
                gender = "여자";
        }
    };
}