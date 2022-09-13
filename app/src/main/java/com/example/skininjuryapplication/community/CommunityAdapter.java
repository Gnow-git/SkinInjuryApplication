package com.example.skininjuryapplication.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skininjuryapplication.R;

import java.util.List;

public class CommunityAdapter extends BaseAdapter {

    private final List<CommunityList> mData;
    // List를 구현한 모든 것(ArrayList 등)을 받는 생성자
    public CommunityAdapter(List<CommunityList> data) {
        mData = data;
    }

    // 아이템의 개수
    @Override
    public int getCount() {
        return mData.size();
    }

    // position 번째의 아이템
    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    // position 번째의 아이디
    @Override
    public long getItemId(int position) {
        return position;
    }

    // position 번째의 아이템의 View를 구성하는 부분
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_community, parent, false);
        // 커뮤니티 view
        ImageView listImage = (ImageView) convertView.findViewById(R.id.list_image);
        TextView listTitle = (TextView) convertView.findViewById(R.id.list_title);
        TextView listUser = (TextView) convertView.findViewById(R.id.list_name);
        // 현재 position의 Community Data
        CommunityList communitylist = mData.get(position);
        // 데이터 설정
        listTitle.setText(communitylist.getTitle());
        listUser.setText(communitylist.getName());
        return convertView;
    }
}
