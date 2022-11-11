package com.example.skininjuryapplication.map;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.skininjuryapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    ImageView bs_image;
    TextView bs_title, bs_address, bs_number;
    RatingBar ratingBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
      View v = inflater.inflate(R.layout.map_bottom_sheet_layout, container, false);

        bs_image = v.findViewById(R.id.bs_image);
        bs_title = v.findViewById(R.id.bs_title);
        ratingBar = v.findViewById(R.id.bs_ratingBar);
        bs_address = v.findViewById(R.id.bs_address);
        bs_number = v.findViewById(R.id.bs_number);

        // firebase 지정
        mDatabase = FirebaseDatabase.getInstance().getReference("Map");
        
        // GoogleMapSearchActivity 값 전달 받음
        Bundle bundle = getArguments();
        String map_name = null;

        if (bundle != null) {
            map_name = bundle.getString("map_name");
        }


        // firebase 에서 병원 정보 파싱하기
        readHospital_info(map_name);
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

    /** 병원 정보 불러오기 **/
    private void readHospital_info(String map_name) {
        mDatabase.child(map_name).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                String address = (String) datasnapshot.child("address").getValue();
                String Num = (String) datasnapshot.child("mapNum").getValue();
                bs_title.setText(map_name);
                bs_address.setText(address);
                bs_number.setText(Num);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
