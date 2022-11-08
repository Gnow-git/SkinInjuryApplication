package com.example.skininjuryapplication.map;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.skininjuryapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
      View v = inflater.inflate(R.layout.map_bottom_sheet_layout, container, false);

        ImageView bs_image = v.findViewById(R.id.bs_image);
        TextView bs_title = v.findViewById(R.id.bs_title);
        RatingBar ratingBar = v.findViewById(R.id.bs_ratingBar);
        TextView bs_address = v.findViewById(R.id.bs_address);
        TextView bs_number = v.findViewById(R.id.bs_number);

        Bundle bundle = getArguments();
        String map_name = null;

        if (bundle != null) {
            map_name = bundle.getString("map_name");
        }
        bs_title.setText(map_name);

        database = FirebaseDatabase.getInstance(); // firebase database 연동
        databaseReference = database.getReference("Map");   // Map Table 연결
        // map_name 가지고 db 조회하여 나머지 정보 출력
/*
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                // firebase database 에서 Data 받아오기
                for (DataSnapshot snapshot : datasnapshot.getChildren()){
                    MapList mapList = snapshot.getValue(MapList.class);
                    String mapName = mapList.getMapName();
                    String mapAddress = mapList.getAddress();
                    String mapNumber = mapList.getMapNum();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
      return v;
    }

    public interface BottomSheetListener{
        //void onButtonClicked(String text);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try{
            mListener = (BottomSheetListener) context;
        } catch(ClassCastException e){
           // throw new ClassCastException(context.toString() + "must implement BottomSheetListener");
        }
    }
}
