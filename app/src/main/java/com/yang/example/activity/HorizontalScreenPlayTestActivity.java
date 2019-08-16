package com.yang.example.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;

import com.yang.example.R;
import com.yang.example.utils.ScreenUtil;

public class HorizontalScreenPlayTestActivity extends SimpleBarActivity {

        private FrameLayout content_layout;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_horizontal_screen_play_test);

                content_layout = findViewById(R.id.content_layout);

                ViewGroup.LayoutParams params = content_layout.getLayoutParams();
                params.width = ScreenUtil.SCREEN_WIDTH;
                params.height = (int) (params.width / 16f * 9);
                content_layout.setPivotX(params.width / 2);
                content_layout.setPivotY(params.height / 2);
                px = params.width / 2;
                py = params.height / 2;
        }

        int px, py;
        int type = 1;

        public void onClick(View view) {
                super.onClick(view);
                switch (view.getId()) {
                        case R.id.iv_quanping:
                                changeOrientation();
                                break;
                }
        }

        private void changeOrientation() {
                if (type == 1) {
                        type = 2;
//                        content_layout.setRotationX(10);
                        content_layout.setRotation(90);
//                        RotateAnimation animation = new RotateAnimation(0, 90, px, py);
//                        content_layout.startAnimation(animation);
                } else {
                        type = 1;
//                        content_layout.setRotationX(0);
                        content_layout.setRotation(0);
//                        RotateAnimation animation = new RotateAnimation(0, 90, px, py);
//                        content_layout.startAnimation(animation);
                }

        }
}
