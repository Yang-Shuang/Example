package com.yang.example.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yang.example.R;
import com.yang.example.activity.SimpleBarActivity;

import java.util.Random;

public class PageEmptyFragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_live_empty, null);
                view.setFocusable(false);
                view.setClickable(false);
                view.setEnabled(false);
                view.setFocusableInTouchMode(false);
                return view;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);
                int color = new Random().nextInt(89999) + 100000;
                view.setBackgroundColor(Color.parseColor("#55" + color));
                view.findViewById(R.id.test_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                ((SimpleBarActivity) getActivity()).showToast("test");
                        }
                });
        }

}
