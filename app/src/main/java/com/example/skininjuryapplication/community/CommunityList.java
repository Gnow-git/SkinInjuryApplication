package com.example.skininjuryapplication.community;

public class CommunityList {
    // 캡슐화를 위해 접근 지정자 private로 지정
    private String title;   // 제목
    private String text;    // 내용
    private String name;    // 사용자 이름

    // 객체 생성을 편하게 하기 위한 생성자 추가
    public CommunityList(String title, String text, String name) {
        this.title = title;
        this.text = text;
        this.name = name;
    }

    // 각 속성에 접근을 위한 getter, setter 메서드 추가
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // 디버깅이나 로그에서 정보 확인을 위한 toString() 메서드 정의
    @Override
    public String toString() {
        return "List{" +
                "title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
