package com.example.skininjuryapplication.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skininjuryapplication.R;

import org.checkerframework.checker.units.qual.C;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CustomViewHolder> {

    private ArrayList<CommunityList> arrayList;
    private Context context;

    public CommunityAdapter(ArrayList<CommunityList> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    // list view가 adapter 연결 후 view holder 생성
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    // 각 아이템에 대해 매칭
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getProfile())
                .into(holder.list_profile);
        holder.list_title.setText(arrayList.get(position).getTitle());
        holder.list_text.setText(arrayList.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return(arrayList != null ? arrayList.size() : 0);
    }
    
    // 뷰 홀더 지정
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView list_profile;
        TextView list_title;
        TextView list_text;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.list_profile = itemView.findViewById(R.id.list_profile);
            this.list_title = itemView.findViewById(R.id.list_title);
            this.list_text = itemView.findViewById(R.id.list_text);
        }
    }
}
