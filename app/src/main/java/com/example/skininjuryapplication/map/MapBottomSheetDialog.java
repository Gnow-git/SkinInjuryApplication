package com.example.skininjuryapplication.map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.skininjuryapplication.R;
import com.example.skininjuryapplication.community.CommunityChatActivity;
import com.example.skininjuryapplication.community.comment.CommentActivity;
import com.example.skininjuryapplication.community.comment.CommunityViewActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapBottomSheetDialog extends BottomSheetDialogFragment {
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private View view;
    private Context context;

    ImageView bs_image;
    TextView bs_title, bs_address, bs_number;
    RatingBar ratingBar;
    Button bt_map_detail;
    boolean expanded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
      view = inflater.inflate(R.layout.map_bottom_sheet_layout, container, false);

        bs_image = view.findViewById(R.id.bs_image);
        bs_title = view.findViewById(R.id.bs_title);
        ratingBar = view.findViewById(R.id.bs_ratingBar);
        bs_address = view.findViewById(R.id.bs_address);
        bs_number = view.findViewById(R.id.bs_number);
        bt_map_detail = view.findViewById(R.id.map_detail);

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


      return view;
    }


    /*@Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;

                setupRatio(bottomSheetDialog);
        });

        return dialog;
    }

    private void setupRatio(BottomSheetDialog bottomSheetDialog) {

        FrameLayout bottomSheet = (FrameLayout)
                bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
        layoutParams.height = getBottomSheetDialogDefaultHeight();
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getBottomSheetDialogDefaultHeight(){
        return getWindowHeight() * 85 / 100;
    }

    private int getWindowHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }*/



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

                bt_map_detail.setOnClickListener(view -> {
                    Intent i = new Intent(getActivity(), MapDetailActivity.class);
                    i.putExtra("map_name", map_name);
                    i.putExtra("address", address);
                    i.putExtra("mapNum", Num);
                    startActivity(i);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
