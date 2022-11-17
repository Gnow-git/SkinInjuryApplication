package com.example.skininjuryapplication.map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skininjuryapplication.R;
import com.example.skininjuryapplication.community.CommunityAdapter;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class MapReviewAdapter extends RecyclerView.Adapter<MapReviewAdapter.CustomViewHolder> {

    private ArrayList<MapReviewList> arrayList;
    private Context context;
    private DatabaseReference mFirebaseDatabaseReference;


    // ClickEvent 처리
    private RecyclerViewClickListener mListener;

    // ClickEvent 처리
    public void setOnClickListener(RecyclerViewClickListener listener) {
        mListener = listener;
    }
    public interface RecyclerViewClickListener {
        void onItemClicked(View v, int position);
    }

    public MapReviewAdapter(ArrayList<MapReviewList> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    // list view가 adapter 연결 후 view holder 생성
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map_review, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    // 각 아이템에 대해 매칭
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.map_review.setText(arrayList.get(position).getText());

        // 아이템 클릭 이벤트 처리
        holder.itemView.setTag(position);   // 각각의 리스트 의미
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return(arrayList != null ? arrayList.size() : 0);
    }

    // 뷰 홀더 지정
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        //ImageView list_profile;
        TextView map_review;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //this.list_profile = itemView.findViewById(R.id.list_profile);
            this.map_review = itemView.findViewById(R.id.map_review);
        }
    }
}
