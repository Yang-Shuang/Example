package com.yang.example.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yang.example.fragment.PageEmptyFragment;

import java.util.HashMap;

public class LivePageAdapter extends FragmentPagerAdapter {

        private static final int LIVE_GOOD_POSITION = 0;
        private static final int LIVE_MAIN_POSITION = 1;
        private static final int LIVE_EMPTY_POSITION = 2;
        private HashMap<Integer, Class<? extends Fragment>> fragmentMap = new HashMap<Integer, Class<? extends Fragment>>() {{
                put(LIVE_GOOD_POSITION, PageEmptyFragment.class);
                put(LIVE_MAIN_POSITION, PageEmptyFragment.class);
                put(LIVE_EMPTY_POSITION, PageEmptyFragment.class);
        }};
        private Fragment[] fragments;

        public LivePageAdapter(FragmentManager fm) {
                super(fm);
                fragments = new Fragment[fragmentMap.size()];
        }

        @Override
        public Fragment getItem(int position) {
                try {
                        if (fragments[position] == null) {
                                Class fragmentClass = fragmentMap.get(position);
                                fragments[position] = (Fragment) fragmentClass.newInstance();
                        }
                } catch (IllegalAccessException e) {
                        e.printStackTrace();
                } catch (InstantiationException e) {
                        e.printStackTrace();
                }
                return fragments[position];
        }

        @Override
        public int getCount() {
                return fragments.length;
        }
}