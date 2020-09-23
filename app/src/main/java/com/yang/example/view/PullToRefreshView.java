package com.yang.example.view;

import android.content.Context;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yang.example.R;
import com.yang.example.utils.LogUtil;

public class PullToRefreshView extends LinearLayout {
    protected final String TAG = getClass().getSimpleName();
    // refresh states
    protected static final int PULL_TO_REFRESH = 2;
    protected static final int RELEASE_TO_REFRESH = 3;
    protected static final int REFRESHING = 4;
    // pull state
    protected static final int PULL_UP_STATE = 0;
    protected static final int PULL_DOWN_STATE = 1;

    /**
     * last y
     */
    public int mLastMotionY, mLastMotionX;
    /**
     * lock
     */
    private boolean mLock;
    /**
     * header view
     */
    private View mHeaderView;
    /**
     * footer view
     */
    private View mFooterView;
    /**
     * list or grid
     */
    protected AdapterView<?> mAdapterView;
    /**
     * scrollview
     */
    private ScrollView mScrollView;
    private WebView mWebView;
    private RecyclerView mRecyclerView;
    /**
     * header view height
     */
    private int mHeaderViewHeight;
    /**
     * footer view height
     */
    private int mFooterViewHeight;
    /**
     * header view image
     */
//	private ImageView mHeaderImageView;
    /**
     * header tip text
     */
    private TextView mHeaderTextView;
    /**
     * footer tip text
     */
    private TextView mFooterTextView;
    /**
     * header refresh time
     */
    private TextView mHeaderUpdateTextView;
    /**
     * footer refresh time
     */
    // private TextView mFooterUpdateTextView;
    /**
     * header progress bar
     */
//	private ProgressBar mHeaderProgressBar;
    /**
     * footer progress bar
     */
    private ProgressBar mFooterProgressBar;
    /**
     * layout inflater
     */
    private LayoutInflater mInflater;
    /**
     * header view current state
     */
    protected int mHeaderState;
    /**
     * footer view current state
     */
    protected int mFooterState;
    /**
     * pull state,pull up or pull down;PULL_UP_STATE or PULL_DOWN_STATE
     */
    protected int mPullState;
    /**
     * 变为向下的箭�?改变箭头方向
     */
    private RotateAnimation mFlipAnimation;
    /**
     * 变为逆向的箭�?旋转
     */
    private RotateAnimation mReverseFlipAnimation;
    /**
     * footer refresh listener
     */
    protected OnFooterRefreshListener mOnFooterRefreshListener;
    /**
     * footer refresh listener
     */
    protected OnHeaderRefreshListener mOnHeaderRefreshListener;

    /**  */
    protected OnPullDownChangeListener onPullDownChangeListener;

    protected OnPullDownStateChangeListener mOnPullDownStateChangeListener;
    /**
     * last update time
     */
    private String mLastUpdateTime;
    //	private ImageView iv_pull_to_refresh_title;
    private int labelHeight;

    private String refreshPullLabel = getResources().getString(R.string.pull_to_refresh_pull_label);
    private String refreshRefreshingLabel = getResources().getString(R.string.pull_to_refresh_refreshing_label);
    private String refreshReleaseLabel = getResources().getString(R.string.pull_to_refresh_release_label);

    private LinearLayout.LayoutParams adapterViewParams;
    private ExampleInterpolator exampleInterpolator;
    private ReboundRunable mReboundRunable;

    public String getRefreshPullLabel() {
        return refreshPullLabel;
    }

    public void setRefreshPullLabel(String refreshPullLabel) {
        this.refreshPullLabel = refreshPullLabel;
    }

    public String getRefreshRefreshingLabel() {
        return refreshRefreshingLabel;
    }

    public void setRefreshRefreshingLabel(String refreshRefreshingLabel) {
        this.refreshRefreshingLabel = refreshRefreshingLabel;
    }

    public String getRefreshReleaseLabel() {
        return refreshReleaseLabel;
    }

    public void setRefreshReleaseLabel(String refreshReleaseLabel) {
        this.refreshReleaseLabel = refreshReleaseLabel;
    }

    public PullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToRefreshView(Context context) {
        super(context);
        init();
    }

    public PullToRefreshView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * init
     *
     * @description hylin 2012-7-26上午10:08:33
     */
    private void init() {
        exampleInterpolator = new ExampleInterpolator();
        // Load all of the animations we need in code rather than through XML
        mFlipAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setFillAfter(true);
        mReverseFlipAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);

        mInflater = LayoutInflater.from(getContext());
        // header view 在此添加,保证是第�?��添加到linearlayout的最上端
        addHeaderView();
    }

    private void addHeaderView() {
        // header view
        mHeaderView = mInflater.inflate(R.layout.refresh_header, this, false);

//		iv_pull_to_refresh_title = (ImageView) mHeaderView
//				.findViewById(R.id.iv_pull_to_refresh_title);
//		mHeaderImageView = (ImageView) mHeaderView
//				.findViewById(R.id.pull_to_refresh_image);
        mHeaderTextView = (TextView) mHeaderView
                .findViewById(R.id.pull_to_refresh_text);
        mHeaderUpdateTextView = (TextView) mHeaderView
                .findViewById(R.id.pull_to_refresh_updated_at);
//		mHeaderProgressBar = (ProgressBar) mHeaderView
//				.findViewById(R.id.pull_to_refresh_progress);
        // header layout
        measureView(mHeaderView);
//		measureView(iv_pull_to_refresh_title);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
//		labelHeight = iv_pull_to_refresh_title.getMeasuredHeight();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                mHeaderViewHeight);
        // 设置topMargin的�?为负的header View高度,即将其隐藏在�?���?
        params.topMargin = -(mHeaderViewHeight);
        addView(mHeaderView, params);
        mReboundRunable = new ReboundRunable(mHeaderViewHeight);
    }

    public void setHeaderViewVisible() {
        mHeaderView.setVisibility(VISIBLE);
    }

    public void setHeaderViewInVisible() {
        mHeaderView.setVisibility(INVISIBLE);
    }

    public void setFooterViewVisible() {
        mFooterView.setVisibility(VISIBLE);
    }

    public void setFooterViewInVisible() {
        mFooterView.setVisibility(INVISIBLE);
    }

    protected void addFooterView() {
        // footer view
        mFooterView = mInflater.inflate(R.layout.refresh_footer, this, false);
//        mFooterImageView = (ImageView) mFooterView
//                .findViewById(R.id.pull_to_load_image);
        mFooterTextView = (TextView) mFooterView
                .findViewById(R.id.pull_to_load_text);
        mFooterProgressBar = (ProgressBar) mFooterView
                .findViewById(R.id.pull_to_load_progress);
        // footer layout
        measureView(mFooterView);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                mFooterViewHeight);
        // int top = getHeight();
        // params.topMargin
        // =getHeight();//在这里getHeight()==0,但在onInterceptTouchEvent()方法里getHeight()已经有�?�?不再�?;
        // getHeight()�?��时�?会赋�?稍�?再研究一�?
        // 由于是线性布�?��以直接添�?只要AdapterView的高度是MATCH_PARENT,那么footer
        // view就会被添加到�?��,并隐�?
        addView(mFooterView, params);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // footer view 在此添加保证添加到linearlayout中的�?��
        addFooterView();
        initContentAdapterView();
    }

    /**
     * init AdapterView like ListView,GridView and so on;or init ScrollView
     *
     * @description hylin 2012-7-30下午8:48:12
     */
    public void initContentAdapterView() {
        int count = getChildCount();
        if (count < 3) {
            throw new IllegalArgumentException(
                    "this layout must contain 3 child views,and AdapterView or ScrollView must in the second position!");
        }
        View view = null;
        for (int i = 0; i < count - 1; ++i) {
            view = getChildAt(i);
            if (view instanceof AdapterView<?>) {
                mAdapterView = (AdapterView<?>) view;
            }
            if (view instanceof ScrollView) {
                // finish later
                mScrollView = (ScrollView) view;
            }
            if (view instanceof WebView) {
                mWebView = (WebView) view;
            }
            if (view instanceof RecyclerView) {
                mRecyclerView = (RecyclerView) view;
            }
        }
        if (mAdapterView == null && mScrollView == null && mWebView == null && mRecyclerView == null) {
            throw new IllegalArgumentException(
                    "must contain a AdapterView or ScrollView or WebView or RecyclerView in this layout!");
        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight,
                    View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    //    private List<H5PageJsBean> mList=new ArrayList<>();
//
//    private boolean getXY(int y){
//        //获取WebView距离屏幕顶部距离
//        int[] position = new int[2];
//        //y坐标为view左上角到屏幕顶部的距离.
//        mWebView.getLocationOnScreen(position);
//        int viewTop=position[1];
//
//        for (H5PageJsBean b:mList){
//            if(b!=null) {
//                int imageHigh = CommonUtils.dip2px(AppConst.context, b.getH()) + CommonUtils.dip2px(AppConst.context, b.getY());
//                int size = viewTop + imageHigh - mWebView.getScrollY();
//                int size1 = viewTop + CommonUtils.dip2px(AppConst.context, b.getY()) - mWebView.getScrollY();
//                if (y < size && y > size1) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//    public  void setWebViewImageSize(int y,int h){
//        if(mList!=null) {
//            mList.clear();
//            H5PageJsBean bean = new H5PageJsBean();
//            bean.setY(y);
//            bean.setH(h);
//            mList.add(bean);
//        }
//    }
//    public void setWebViewImageSize(H5PageJsBean bean){
//        if(mList!=null&&bean.getList() != null) {
//            mList.clear();
//            mList.addAll(bean.getList());
//        }
//    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int y = (int) e.getRawY();
        int x = (int) e.getRawX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 首先拦截down事件,记录y坐标
                mLastMotionY = y;
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                //***下面代码用于解决ViewPager与PullRefreshView滑动冲突*********//
//			int moveY = (int) Math.abs(y-mLastMotionY);
//			int moveX = (int) Math.abs(x-mLastMotionX);
//			if(moveX > 20 || moveY < 20){//
//				return false;
//			}
                //*******************结束******************************//

                //支持webView轮播图
//                if(mWebView != null) {

                //v3.6.1版本替换为getXY(y)方法判断，可删除
//				//获取WebView距离屏幕顶部距离
//				int[] position = new int[2];
//				//y坐标为view左上角到屏幕顶部的距离.
//				mWebView.getLocationOnScreen(position);
//				int viewTop=position[1];
//				int imageHigh=CommonUtils.dip2px(AppConst.context,imageH) + CommonUtils.dip2px(AppConst.context,imageY);
//				int size = viewTop+imageHigh-mWebView.getScrollY();
//				int size1 = viewTop+CommonUtils.dip2px(AppConst.context,imageY)-mWebView.getScrollY();
//				LogUtil.i("支持轮播","viewTop="+viewTop+",imageH="+imageH+",imageY="+imageY+",mWebView.getScrollY()="+mWebView.getScrollY()+",y="+y+",size="+size);

//
//                    if (getXY(y)) {
//                        int daltaX = x - mLastMotionX;
//                        if (daltaX != 0) {
//                            getParent().requestDisallowInterceptTouchEvent(true);
//                            return false;
//                        }
//                    }

                //v3.6.1版本替换为getXY(y)方法判断，可删除
//				if (y<size&&mWebView.getScrollY()<imageHigh) {
//					int daltaX = x - mLastMotionX;
//					if (daltaX != 0) {
//						getParent().requestDisallowInterceptTouchEvent(true);
//					}
//				}
//                }

                // deltaY > 0 是向下运�?< 0是向上运�?
                int deltaY = y - mLastMotionY;

                if (isRefreshViewScroll(deltaY)) {
                    LogUtil.e("-----onInterceptTouchEvent---- true");
                    keepAdapterViewParams();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return false;
    }

    private void keepAdapterViewParams() {
        if (mAdapterView == null) return;
        adapterViewParams = (LayoutParams) mAdapterView.getLayoutParams();
        LogUtil.e("keepAdapterViewParams ----" + adapterViewParams.width);
        LayoutParams params = new LinearLayout.LayoutParams(mAdapterView.getWidth(), mAdapterView.getHeight());
        params.gravity = adapterViewParams.gravity;
        params.bottomMargin = adapterViewParams.bottomMargin;
        params.topMargin = adapterViewParams.topMargin;
        params.leftMargin = adapterViewParams.leftMargin;
        params.rightMargin = adapterViewParams.rightMargin;
        params.weight = adapterViewParams.weight;
        mAdapterView.setLayoutParams(params);
    }

    private void revertAdapterViewParams() {
        if (mAdapterView == null) return;
        mAdapterView.setLayoutParams(adapterViewParams);
    }

    /*
     * 如果在onInterceptTouchEvent()方法中没有拦�?即onInterceptTouchEvent()方法�?return
     * false)则由PullToRefreshView 的子View来处�?否则由下面的方法来处�?即由PullToRefreshView自己来处�?
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLock) {
            return true;
        }
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // onInterceptTouchEvent已经记录
                // mLastMotionY = y;
                mReboundRunable.stopAny();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - mLastMotionY;
                if (mPullState == PULL_DOWN_STATE) {
                    // PullToRefreshView执行下拉
                    LogUtil.i(TAG, "--- pull down!parent view move! " + deltaY);
                    headerPrepareToRefresh(deltaY);
                    if (mOnPullDownStateChangeListener != null) {
                        mOnPullDownStateChangeListener.onPullDown();
                    }
                    if (onPullDownChangeListener != null) {
                        onPullDownChangeListener.gradualChange(deltaY);
                    }
                    // setHeaderPadding(-mHeaderViewHeight);
                } else if (mPullState == PULL_UP_STATE) {
                    // PullToRefreshView执行上拉
                    LogUtil.i(TAG, "pull up!parent view move!");
                    if (mOnPullDownStateChangeListener != null) {
                        mOnPullDownStateChangeListener.onPullUp();
                    }
                    footerPrepareToRefresh(deltaY);
                }
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int topMargin = getHeaderTopMargin();
                if (mPullState == PULL_DOWN_STATE) {
                    if (topMargin >= 0) {
                        // �?��刷新
                        headerRefreshing();
                    } else {
                        // 还没有执行刷新，重新隐藏
//                        setHeaderTopMargin(-mHeaderViewHeight);
                        mReboundRunable.backToStop();
                    }
                } else if (mPullState == PULL_UP_STATE) {
                    if (Math.abs(topMargin) >= mFooterViewHeight + mHeaderViewHeight) {
                        // �?��执行footer 刷新
                        footerRefreshing();
                    } else {
                        // 还没有执行刷新，重新隐藏
                        setHeaderTopMargin(-mHeaderViewHeight);
                        revertAdapterViewParams();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    public void changeState(){}

    /**
     * 是否应该到了父View,即PullToRefreshView滑动
     *
     * @param deltaY , deltaY > 0 是向下运�?< 0是向上运�?
     * @return
     */
    public boolean isRefreshViewScroll(int deltaY) {
        if (mHeaderState == REFRESHING || mFooterState == REFRESHING) {
            lock();
            return true;
        }
        // 对于ListView和GridView
        if (mAdapterView != null) {
            // 子view(ListView or GridView)滑动到最顶端
            if (deltaY > 5) {
                View child = mAdapterView.getChildAt(0);
                if (child == null) {
                    // 如果mAdapterView中没有数�?不拦�?
                    return false;
                }
                if (mAdapterView.getFirstVisiblePosition() == 0
                        && child.getTop() == 0) {
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }
                int top = child.getTop();
                int padding = mAdapterView.getPaddingTop();
                if (mAdapterView.getFirstVisiblePosition() == 0
                        && Math.abs(top - padding) <= 8) {// 这里之前�?可以判断,但现在不�?还没找到原因
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }

            } else if (deltaY < -5) {
                View child = mAdapterView.getChildAt(0);
                if (child == null) {
                    return false;
                }
                View lastChild = mAdapterView.getChildAt(mAdapterView
                        .getChildCount() - 1);
                if (lastChild == null) {
                    // 如果mAdapterView中没有数�?不拦�?
                    return false;
                }
                // �?���?��子view的Bottom小于父View的高度说明mAdapterView的数据没有填满父view,
                // 等于父View的高度说明mAdapterView已经滑动到最�?
                if (lastChild.getBottom() <= getHeight()
                        && mAdapterView.getLastVisiblePosition() == mAdapterView
                        .getCount() - 1) {
                    mPullState = PULL_UP_STATE;
                    return true;
                }
            }
        }
        //对于RecyclerView
        if (mRecyclerView != null) {
            if (deltaY > 5) {
                View child = mRecyclerView.getChildAt(0);
                if (child == null) {
                    // 如果mAdapterView中没有数�?不拦�?
                    return false;
                }
                LinearLayoutManager lm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                if (lm.findFirstVisibleItemPosition() == 0
                        && child.getTop() == 0) {
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }
                int top = child.getTop();
                int padding = mRecyclerView.getPaddingTop();
                if (lm.findFirstVisibleItemPosition() == 0
                        && Math.abs(top - padding) <= 8) {// 这里之前�?可以判断,但现在不�?还没找到原因
                    mPullState = PULL_DOWN_STATE;
                    return true;
                }

            } else if (deltaY < -5) {
                View child = mRecyclerView.getChildAt(0);
                if (child == null) {
                    return false;
                }
                View lastChild = mRecyclerView.getChildAt(mRecyclerView
                        .getChildCount() - 1);
                if (lastChild == null) {
                    // 如果mAdapterView中没有数�?不拦�?
                    return false;
                }
                LinearLayoutManager lm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                // �?���?��子view的Bottom小于父View的高度说明mAdapterView的数据没有填满父view,
                // 等于父View的高度说明mAdapterView已经滑动到最�?
                if (lastChild.getBottom() <= getHeight()
                        && lm.findLastVisibleItemPosition() == mRecyclerView
                        .getChildCount() - 1) {
                    mPullState = PULL_UP_STATE;
                    return true;
                }
            }
        }
        // 对于ScrollView
        if (mScrollView != null) {
            // 子scroll view滑动到最顶端
            View child = mScrollView.getChildAt(0);
            if (deltaY > 0 && mScrollView.getScrollY() == 0) {
                mPullState = PULL_DOWN_STATE;
                return true;
            } else if (deltaY < 0) {
                if (child.getMeasuredHeight() <= getHeight()
                        + mScrollView.getScrollY()) {
                    mPullState = PULL_UP_STATE;
                    return true;
                }
            }
        }
        if (mWebView != null) {
            // 子scroll view滑动到最顶端
//			View child = mWebView.getChildAt(0);
            if (deltaY > 0 && mWebView.getScrollY() == 0) {
                mPullState = PULL_DOWN_STATE;
                return true;
            } else if (deltaY < 0) {
//				if(mWebView.getMeasuredHeight() <= getHeight()
//						+ mWebView.getScrollY()){
//					mPullState = PULL_UP_STATE;
//					return true;
//				}
            }
        }
        return false;
    }

    /**
     * header 准备刷新,手指移动过程,还没有释�?
     *
     * @param deltaY ,手指滑动的距�?
     */
    private void headerPrepareToRefresh(int deltaY) {
        int newTopMargin = changingHeaderViewTopMargin(deltaY);
        // 当header view的topMargin>=0时，说明已经完全显示出来�?修改header view 的提示状�?
        if (newTopMargin >= 0 && mHeaderState != RELEASE_TO_REFRESH) {
            mHeaderTextView.setText(getRefreshReleaseLabel());
//			mHeaderUpdateTextView.setVisibility(View.VISIBLE);
//			mHeaderImageView.clearAnimation();
//			mHeaderImageView.startAnimation(mFlipAnimation);
            mHeaderState = RELEASE_TO_REFRESH;
        } else if (newTopMargin < 0 && newTopMargin > -mHeaderViewHeight) {// 拖动时没有释�?
//			mHeaderImageView.clearAnimation();
//			mHeaderImageView.startAnimation(mFlipAnimation);
            // mHeaderImageView.
            mHeaderTextView.setText(getRefreshPullLabel());
            mHeaderState = PULL_TO_REFRESH;
        }
    }

    /**
     * footer 准备刷新,手指移动过程,还没有释�?移动footer view高度同样和移动header view
     * 高度是一样，都是通过修改header view的topmargin的�?来达�?
     *
     * @param deltaY ,手指滑动的距�?
     */
    private void footerPrepareToRefresh(int deltaY) {
        int newTopMargin = changingHeaderViewTopMargin(deltaY);
        // 如果header view topMargin 的绝对�?大于或等于header + footer 的高�?
        // 说明footer view 完全显示出来了，修改footer view 的提示状�?
        if (Math.abs(newTopMargin) >= (mHeaderViewHeight + mFooterViewHeight)
                && mFooterState != RELEASE_TO_REFRESH) {
            mFooterTextView
                    .setText(R.string.pull_to_refresh_footer_release_label);
//            mFooterImageView.clearAnimation();
//            mFooterImageView.startAnimation(mFlipAnimation);
            mFooterState = RELEASE_TO_REFRESH;
        } else if (Math.abs(newTopMargin) < (mHeaderViewHeight + mFooterViewHeight)) {
//            mFooterImageView.clearAnimation();
//            mFooterImageView.startAnimation(mFlipAnimation);
            mFooterTextView.setText(R.string.pull_to_refresh_footer_pull_label);
            mFooterState = PULL_TO_REFRESH;
        }
    }

    /**
     * 修改Header view top margin的�?
     *
     * @param deltaY
     * @return hylin 2012-7-31下午1:14:31
     * @description
     */
    private int changingHeaderViewTopMargin(int deltaY) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mHeaderView.getLayoutParams();
        float moveY = deltaY;
        float h = mHeaderViewHeight / 2f;
        if (params.topMargin > 0) {
            moveY = deltaY / ((params.topMargin + mHeaderViewHeight) / h);
        } else {
            moveY = deltaY / ((mHeaderViewHeight) / h);
        }
        params.topMargin = (int) (params.topMargin + moveY);
        mHeaderView.setLayoutParams(params);
        invalidate();
        return params.topMargin;
    }

    private int changingHeaderViewTopMarginForRebound(int delaty) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mHeaderView.getLayoutParams();
        LogUtil.e("params.topMargin   " + params.topMargin + "-----delaty: " + delaty);
        params.topMargin = params.topMargin - delaty;
        mHeaderView.setLayoutParams(params);
        invalidate();
        return params.topMargin;
    }

    /**
     * header refreshing
     *
     * @description hylin 2012-7-31上午9:10:12
     */
    public void headerRefreshing() {
        mHeaderState = REFRESHING;
        mReboundRunable.backToRefresh();
//        setHeaderTopMargin(-labelHeight);
//		mHeaderImageView.setVisibility(View.GONE);
//		mHeaderImageView.clearAnimation();
//		mHeaderImageView.setImageDrawable(null);
//		mHeaderProgressBar.setVisibility(View.VISIBLE);
        mHeaderTextView.setText(getRefreshRefreshingLabel());
//		iv_pull_to_refresh_title.setVisibility(View.INVISIBLE);
        if (mOnHeaderRefreshListener != null) {
            mOnHeaderRefreshListener.onHeaderRefresh(this);
        }
    }

    /**
     * footer refreshing
     *
     * @description hylin 2012-7-31上午9:09:59
     */
    private void footerRefreshing() {
        mFooterState = REFRESHING;
        int top = mHeaderViewHeight + mFooterViewHeight;
        setHeaderTopMargin(-top);
//        mFooterImageView.setVisibility(View.GONE);
//        mFooterImageView.clearAnimation();
//        mFooterImageView.setImageDrawable(null);
        mFooterProgressBar.setVisibility(View.VISIBLE);
        mFooterTextView
                .setText(R.string.pull_to_refresh_footer_refreshing_label);
        if (mOnFooterRefreshListener != null) {
            mOnFooterRefreshListener.onFooterRefresh(this);
        }
    }

    /**
     * 设置header view 的topMargin的�?
     *
     * @param topMargin ，为0时，说明header view 刚好完全显示出来�?�?mHeaderViewHeight时，说明完全隐藏�?
     *                  hylin 2012-7-31上午11:24:06
     * @description
     */
    private void setHeaderTopMargin(int topMargin) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mHeaderView.getLayoutParams();
        params.topMargin = topMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
    }

    /**
     * header view 完成更新后恢复初始状�?
     *
     * @description hylin 2012-7-31上午11:54:23
     */
    public void onHeaderRefreshComplete() {
//        setHeaderTopMargin(-mHeaderViewHeight);
//		mHeaderImageView.setVisibility(View.VISIBLE);
//		mHeaderImageView.setImageResource(R.drawable.arrow);
        mHeaderTextView.setText(getRefreshPullLabel());
//		iv_pull_to_refresh_title.setVisibility(View.VISIBLE);
//		mHeaderProgressBar.setVisibility(View.INVISIBLE);
        // mHeaderUpdateTextView.setText("");
        mHeaderState = PULL_TO_REFRESH;
        mReboundRunable.backToStop();
        unlock();
    }

    /**
     * Resets the list to a normal state after a refresh.
     *
     * @param lastUpdated Last updated at.
     */
    public void onHeaderRefreshComplete(CharSequence lastUpdated) {
        setLastUpdated(lastUpdated);
        onHeaderRefreshComplete();
    }

    /**
     * footer view 完成更新后恢复初始状�?
     */
    public void onFooterRefreshComplete() {
        setHeaderTopMargin(-mHeaderViewHeight);
//        mFooterImageView.setVisibility(View.VISIBLE);
//        mFooterImageView.setImageResource(R.drawable.arrow_up);
        mFooterTextView.setText(R.string.pull_to_refresh_footer_pull_label);
        mFooterProgressBar.setVisibility(View.GONE);
        // mHeaderUpdateTextView.setText("");
        mFooterState = PULL_TO_REFRESH;
        unlock();
    }

    /**
     * Set a text to represent when the list was last updated.
     *
     * @param lastUpdated Last updated at.
     */
    public void setLastUpdated(CharSequence lastUpdated) {
        if (lastUpdated != null) {
            mHeaderUpdateTextView.setVisibility(View.VISIBLE);
            mHeaderUpdateTextView.setText(lastUpdated);
        } else {
            mHeaderUpdateTextView.setVisibility(View.GONE);
        }
    }

    /**
     * 获取当前header view 的topMargin
     *
     * @return hylin 2012-7-31上午11:22:50
     * @description
     */
    public int getHeaderTopMargin() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mHeaderView.getLayoutParams();
        return params.topMargin;
    }

    /**
     * lock
     *
     * @description hylin 2012-7-27下午6:52:25
     */
    protected void lock() {
        mLock = true;
    }

    /**
     * unlock
     *
     * @description hylin 2012-7-27下午6:53:18
     */
    private void unlock() {
        mLock = false;
    }

    /**
     * set headerRefreshListener
     *
     * @param headerRefreshListener hylin 2012-7-31上午11:43:58
     * @description
     */
    public void setOnHeaderRefreshListener(
            OnHeaderRefreshListener headerRefreshListener) {
        mOnHeaderRefreshListener = headerRefreshListener;
    }

    public void setOnFooterRefreshListener(
            OnFooterRefreshListener footerRefreshListener) {
        mOnFooterRefreshListener = footerRefreshListener;
    }

    public void setOnPullDownChangeListener(
            OnPullDownChangeListener onPullDownChangeListener) {
        this.onPullDownChangeListener = onPullDownChangeListener;
    }

    public void setmOnPullDownStateChangeListener(OnPullDownStateChangeListener mOnPullDownStateChangeListener) {
        this.mOnPullDownStateChangeListener = mOnPullDownStateChangeListener;
    }

    /**
     * Interface definition for a callback to be invoked when list/grid footer
     * view should be refreshed.
     */
    public interface OnFooterRefreshListener {
        public void onFooterRefresh(PullToRefreshView view);
    }

    /**
     * Interface definition for a callback to be invoked when list/grid header
     * view should be refreshed.
     */
    public interface OnHeaderRefreshListener {
        public void onHeaderRefresh(PullToRefreshView view);
    }

    /**
     * 接口定义如下:
     * 下拉刷新过程中逐渐隐藏标题的监听器
     */
    public interface OnPullDownChangeListener {

        /**
         * @param direction 操作方向 0向上滑动 1向下滑动
         */
        public void gradualChange(int direction);
    }

    /**
     * 接口定义如下:
     * 下拉刷新过程中逐渐隐藏标题的监听器
     */
    public interface OnPullDownStateChangeListener {

        /**
         * 操作方向 0向上滑动 1向下滑动
         */
        public void onPullDown();

        public void onPullUp();
    }

    /**
     *
     * 下拉刷新使用的回弹动画，仿写RecyclerView滚动实现方式，使用{@link ViewCompat}的postOnAnimation
     *
     * */
    public class ReboundRunable implements Runnable {

        private int headerHeight;
        private int stopHeight;
        private int reboundType = -1; //1  2

        public ReboundRunable(int headerHeight) {
            this.headerHeight = headerHeight;
        }

        @Override
        public void run() {
            int top = getHeaderTopMargin();
            if (top > stopHeight) {
                if (top - stopHeight < 50) {
                    changingHeaderViewTopMarginForRebound(top - stopHeight);
                } else {
                    changingHeaderViewTopMarginForRebound(50);
                }
                postOnAnimation();
            } else {
                if (reboundType == 2)
                    revertAdapterViewParams();
                removeCallbacks(this);
            }
        }

        public void backToRefresh() {
            reboundType = 1;
            stopHeight = 0;
            removeCallbacks(this);
            ViewCompat.postOnAnimation(PullToRefreshView.this, this);
        }

        public void backToStop() {
            reboundType = 2;
            stopHeight = -headerHeight;
            removeCallbacks(this);
            ViewCompat.postOnAnimation(PullToRefreshView.this, this);
        }

        private void postOnAnimation() {
            removeCallbacks(this);
            ViewCompat.postOnAnimation(PullToRefreshView.this, this);
        }

        public void stopAny() {
            removeCallbacks(this);
            reboundType = -1;
        }
    }
}
