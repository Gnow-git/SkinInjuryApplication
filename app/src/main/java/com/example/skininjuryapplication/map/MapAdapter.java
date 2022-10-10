package com.example.skininjuryapplication.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skininjuryapplication.R;
import java.util.ArrayList;


public class MapAdapter extends  RecyclerView.Adapter<MapAdapter.CustomViewHolder> {

    private ArrayList<MapList> map_arrayList;
    private Context context;

    //ClickEvent 처리
    private RecyclerViewClickListener mListener;

    // ClickEvent 처리
    public void setOnClickListener(RecyclerViewClickListener listener) { mListener = listener; }
    public interface RecyclerViewClickListener {
        void onItemClicked(View v, int position);
    }

        public MapAdapter(ArrayList<MapList> map_arrayList, Context context) {
            this.map_arrayList = map_arrayList;
            this.context = context;
        }

    @NonNull
    @Override
    // list view가 adapter 연결 후 viewHolder 생성
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    // 각 아이템에 대해 매칭
    public void onBindViewHolder(@NonNull MapAdapter.CustomViewHolder holder, int position) {
        // 병원 이름
        holder.map_name.setText(map_arrayList.get(position).getMapName());
        // 병원 주소
        holder.map_address.setText(map_arrayList.get(position).getAddress());


        // 아이템 클릭 이벤트 처리
        holder.itemView.setTag(position);   // 각각 리스트 지정
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(context.getApplicationContext(),holder.map_name.getText(),Toast.LENGTH_SHORT).show();

                // 주소 검색 후 리스트 클릭시 GoogleMapActivity로 좌표 전달
                String mapName = holder.map_name.getText().toString();
                String mapAddress = holder.map_address.getText().toString();

                Intent i = new Intent(context, GoogleMapActivity.class);
                i.putExtra("map_Name", mapName);
                i.putExtra("map_Address", mapAddress);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return(map_arrayList != null ? map_arrayList.size() : 0);
    }

    // 검색 시 해당 검색어가 포함되면 목록을 표시
    public void filterList(ArrayList<MapList> filteredList) {
        map_arrayList =filteredList;
        notifyDataSetChanged();
    }

    // 뷰 홀더 지정
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView map_name;
        TextView map_address;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.map_name = itemView.findViewById(R.id.map_name);
            this.map_address = itemView.findViewById(R.id.map_address);
        }
    }
}
