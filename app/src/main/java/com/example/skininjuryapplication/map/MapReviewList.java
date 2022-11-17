package com.example.skininjuryapplication.map;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MapReviewList {
    // 캡슐화를 위해 접근 지정자 private로 지정
    private String email;  // DB에 저장할 email
    private String text;    // 내용


    // 객체 생성을 편하게 하기 위한 생성자 추가
    public MapReviewList() { }

    public MapReviewList(String email, String text) {
        this.email = email;
        this.text = text;
    }

    // 각 속성에 접근을 위한 getter, setter 메서드 추가

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    // 디버깅이나 로그에서 정보 확인을 위한 toString() 메서드 정의
    @Override
    public String toString() {
        return "List{" +
                "email='" + email + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}



