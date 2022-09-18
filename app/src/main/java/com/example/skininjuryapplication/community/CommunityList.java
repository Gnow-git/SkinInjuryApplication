package com.example.skininjuryapplication.community;

// 모델 클래스 작성
public class CommunityList {
    // 캡슐화를 위해 접근 지정자 private로 지정
    private String id;  // DB에 저장할 ID
    private String title;   // 제목
    private String text;    // 내용


    // 객체 생성을 편하게 하기 위한 생성자 추가
    public CommunityList() { }

    public CommunityList(String title, String text) {
        this.title = title;
        this.text = text;
    }

    // 각 속성에 접근을 위한 getter, setter 메서드 추가
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
