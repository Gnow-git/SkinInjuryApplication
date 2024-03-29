package com.example.skininjuryapplication.user;

/**
 * 사용자 계정 정보 모델 클래스
 */
public class UserAccount {

    private String idToken; // Firebase Uid(고유 토큰 정보)
    private String emailId; // 이메일 아이디
    private String userName; // 사용자 이름
    private String age;    // 사용자 나이
    private String gender;  // 사용자 성별
    private String password;    // 비밀번호

    public UserAccount() { }    // Firebase Realtime Database 를 쓸때 반드시 공백으로 만들어야함

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
