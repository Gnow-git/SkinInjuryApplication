package com.example.skininjuryapplication.community.comment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.skininjuryapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CustomViewHolder> {

    private ArrayList<CommentList> arrayList;
    private Context context;
    private DatabaseReference mFirebaseDatabaseReference;
    public static final String MESSAGE_CHILD = "List";

    // ClickEvent 처리
    private RecyclerViewClickListener mListener;

    // ClickEvent 처리
    public void setOnClickListener(RecyclerViewClickListener listener) {
        mListener = listener;
    }
    public interface RecyclerViewClickListener {
        void onItemClicked(View v, int position);
    }

    public CommentAdapter(ArrayList<CommentList> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    // list view가 adapter 연결 후 view holder 생성
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    // 각 아이템에 대해 매칭
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.comment_text.setText(arrayList.get(position).getText());

        // 아이템 클릭 이벤트 처리
        holder.itemView.setTag(position);   // 각각의 리스트 의미
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mtext = holder.comment_text.getText().toString();

            }
        });
    }

    @Override
    public int getItemCount() {
        return(arrayList != null ? arrayList.size() : 0);
    }
    
    // 뷰 홀더 지정
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView comment_text;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.comment_text = itemView.findViewById(R.id.comment_text);
        }
    }


}
