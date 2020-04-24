package com.yang.example.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.yang.example.R;
import com.yang.example.adapter.StringItemAdapter;
import com.yang.example.fragment.ListStringFragment;
import com.yang.example.utils.ImageUtils;
import com.yang.example.utils.LogUtil;
import com.yang.example.utils.ScreenUtil;
import com.yang.example.utils.StatusBarUtil;
import com.yang.example.view.BannerColorBackView;
import com.yang.example.view.HMBallPulseHeader;

import java.util.ArrayList;
import java.util.List;

public class TaoBaoHomeActivity extends BaseActivity implements View.OnClickListener {

    //    SwipeRefreshLayout mRefresh;
//    CoordinatorLayout mCoordinatorLayout;
    SmartRefreshLayout mRefresh;
    RecyclerView mRecyclerView;
    ViewPager mViewPager;
    String[] mTitles = {"推荐", "TV购物", "时尚街", "美食汇", "居家馆"};
    BannerColorBackView banner_bg_iv;
    LinearLayout titlebar;

    boolean userImage = true;

    private List<String> data1;
    StringItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tao_bao_home);

        mRecyclerView = (RecyclerView) findViewById(R.id.behavior_top_view);
        mViewPager = (ViewPager) findViewById(R.id.behavior_bottom_view);
//        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.root);
        banner_bg_iv = (BannerColorBackView) findViewById(R.id.banner_bg_iv);
        titlebar = (LinearLayout) findViewById(R.id.titlebar);

        mRefresh = (SmartRefreshLayout) findViewById(R.id.refresh);
        mRefresh.setRefreshHeader(new HMBallPulseHeader(this));
        mRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefresh.finishLoadMore();
                    }
                }, 2000);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefresh.finishRefresh();
                    }
                }, 2000);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(layoutManager);


        data1 = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            String s = String.format("我是第%d个", i);
            data1.add(s);
        }

        mAdapter = new StringItemAdapter(data1);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LogUtil.e("onScrolled : " + dy);
            }
        });

        setupViewPager();

        findViewById(R.id.iv_menu_btn_id).setOnClickListener(this);

        initTitleBar();

        StatusBarUtil.setTransparentForImageView(this, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initTitleBar() {
        final int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        final int titleBarHeight = ScreenUtil.dp2px(60) + statusBarHeight;
        ViewGroup.LayoutParams params = titlebar.getLayoutParams();
        params.width = ScreenUtil.SCREEN_WIDTH;
        params.height = titleBarHeight;
        titlebar.setLayoutParams(params);
        titlebar.setPadding(0, statusBarHeight, 0, 0);

        final ViewGroup.LayoutParams p = banner_bg_iv.getLayoutParams();
        p.width = ScreenUtil.SCREEN_WIDTH;
        p.height = (int) ((params.width / 750f * 244) + titleBarHeight);
        banner_bg_iv.setLayoutParams(p);

        if (userImage) {
            ImageUtils.loadBannerBackgroud(this, R.drawable.bg_main_banner, p.height, new ImageUtils.onLoadBitmapListener() {
                @Override
                public void onLoadComplete(Bitmap drawable) {
                    banner_bg_iv.setBackground(new BitmapDrawable(getResources(), drawable));
                    titlebar.setBackground(new BitmapDrawable(getResources(), ImageUtils.crop(drawable, titleBarHeight, p.height)));
                }
            });
        } else {
            titlebar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            banner_bg_iv.setColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void setupViewPager() {
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.indicator);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        List<Fragment> mFragments = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            Bundle args = new Bundle();
            args.putString("title", mTitles[i]);
            ListStringFragment listFragment = ListStringFragment.newInstance(args);
            mFragments.add(listFragment);
        }
        BaseFragmentAdapter adapter =
                new BaseFragmentAdapter(getSupportFragmentManager(), mFragments, mTitles);


        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu_btn_id:
                userImage = !userImage;
                initTitleBar();
                break;
            case R.id.add:
                data1.add(String.format("我是第%d个", data1.size()));
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.reduce:
                data1.remove(data1.size() - 1);
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    class BaseFragmentAdapter extends FragmentPagerAdapter {

        protected List<Fragment> mFragmentList;

        protected String[] mTitles;

        public BaseFragmentAdapter(FragmentManager fm) {
            this(fm, null, null);
        }

        public BaseFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList, String[] mTitles) {
            super(fm);
            if (fragmentList == null) {
                fragmentList = new ArrayList<>();
            }
            this.mFragmentList = fragmentList;
            this.mTitles = mTitles;
        }

        public void add(Fragment fragment) {
            if (isEmpty()) {
                mFragmentList = new ArrayList<>();

            }
            mFragmentList.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            //        Logger.i("BaseFragmentAdapter position=" +position);
            return isEmpty() ? null : mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return isEmpty() ? 0 : mFragmentList.size();
        }

        public boolean isEmpty() {
            return mFragmentList == null;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

    /*  @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }*/


    }

}
