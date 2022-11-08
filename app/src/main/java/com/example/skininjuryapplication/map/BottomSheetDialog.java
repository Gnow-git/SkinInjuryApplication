package com.example.skininjuryapplication.map;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.skininjuryapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
      View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);


      Button button1 = v.findViewById(R.id.button1);
      Button button2 = v.findViewById(R.id.button2);

      button1.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v) {
             // mListener.onButtonClicked("Button 1 clicked");
              Toast.makeText(getActivity(), "Button1", Toast.LENGTH_SHORT).show();
              dismiss();
          }
      });

      button2.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v) {
              //mListener.onButtonClicked("Button2 clicked");
              Toast.makeText(getActivity(), "Button2", Toast.LENGTH_SHORT).show();
              dismiss();
          }
      });

      return v;
    }

    public interface BottomSheetListener{
        void onButtonClicked(String text);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try{
            mListener = (BottomSheetListener) context;
        } catch(ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement BottomSheetListener");
        }
    }
}
