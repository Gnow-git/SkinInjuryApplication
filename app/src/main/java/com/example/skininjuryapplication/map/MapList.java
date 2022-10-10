package com.example.skininjuryapplication.map;

// 맵 리스트로 표현될 모델 클래스 작성
public class MapList {

    // 캡슐화를 위한 private 지정
    private String id;  // DB에 저장할 ID;
    private String mapName; // 장소 이름
    private String address; // 장소

    // 객체 초기화
    public MapList() { }

    public MapList(String mapName, String address) {
        this.mapName = mapName;
        this.address = address;
    }
    
    // 각 속성 접근을 위한 getter, setter 메서드 추가


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // 디버깅, 로그에서 확인을 위한 메서드 정의
    @Override
    public String toString() {
        return "MapList{" +
                "mapName='" + mapName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
