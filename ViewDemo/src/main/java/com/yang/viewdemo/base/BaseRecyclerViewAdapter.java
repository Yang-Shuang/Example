package com.yang.viewdemo.base;

import android.os.Handler;
import android.os.Message;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by
 * yangshuang on 2018/6/20.
 */

public abstract class BaseRecyclerViewAdapter extends Adapter<BaseRecyclerViewAdapter.BaseViewHolder> implements View.OnTouchListener {


    public interface AnimationListener {
        public void offsetY(View refreshView, float y);

        public void startRefreshAnima(View refreshView);

        public void endRefreshAnima(View refreshView);
    }

    protected static final int HEAD_TYPE = -100;
    protected static final int FOOT_TYPE = -200;

    protected int mLayoutId;

    private RefreshListener mRefreshListener;
    private LoadMoreListener mLoadMoreListener;
    private OnItemClickListener listener;

    protected RecyclerView mRecyclerView;
    protected View mHeadView;
    protected View mFootView;
    protected boolean useDefaultHead = true;
    protected boolean useDefaultFoot = true;
    private boolean isLoadingMore;
    private boolean isRefreshing;
    private boolean prepareRefresh = true;
    private boolean hasMoreData = true;
    private Handler handler;
    private AnimationListener animationListener;

    public BaseRecyclerViewAdapter(int mLayoutId) {
        this.mLayoutId = mLayoutId;
        this.animationListener = new AnimationListener() {
            public void offsetY(View refreshView, float y) {
                if (useDefaultHead) {
                    RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) refreshView.getLayoutParams();
                    TextView textView = (TextView) refreshView;
                    if (lp.topMargin >= 0) {
                        textView.setText("松开刷新");
                    } else {
                        textView.setText("下拉刷新");
                    }
                }
            }

            public void startRefreshAnima(View refreshView) {
                if (useDefaultHead) {
                    TextView textView = (TextView) refreshView;
                    textView.setText("正在刷新");
                }
            }

            public void endRefreshAnima(View refreshView) {
                if (useDefaultHead) {
                    TextView textView = (TextView) refreshView;
                    textView.setText("刷新完成");
                }
            }
        };

        handler = new BaseHandler(this, new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                int type = msg.what;
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mHeadView.getLayoutParams();
                boolean continueSend = false;
                int endY = 0;
                switch (type) {
                    case 0:
                        endY = -params.height;
                        if (params.topMargin < endY) {
                            params.topMargin = endY;
                            continueSend = false;
                        } else {
                            params.topMargin -= 10;
                            continueSend = true;
                        }
                        break;
                    case 1:
                        endY = 0;
                        if (Math.abs(params.topMargin) <= 10) {
                            params.topMargin = 0;
                            continueSend = false;
                            if (mRefreshListener != null && !isRefreshing) {
                                isRefreshing = true;
                                mRefreshListener.onRefresh();
                                animationListener.startRefreshAnima(mHeadView);
                            }
                            break;
                        } else {
                            continueSend = true;
                        }
                        if (params.topMargin < 0) {
                            params.topMargin += 10;
                        } else {
                            int distance = params.topMargin;
                            int multiple = distance / 5;
                            params.topMargin -= multiple;
                        }
                        break;
                }
                mHeadView.setLayoutParams(params);
                if (continueSend) handler.sendEmptyMessageDelayed(type, 1);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = onBindItemCount();
        if (mRefreshListener != null) count++;
//        if (mLoadMoreListener != null) count++;
        return count;
    }

    public abstract int onBindItemCount();

    public abstract void onBindItemData(BaseViewHolder holder, int position);

    protected int getRealDataPosition(int position) {
        return mRefreshListener != null ? position - 1 : position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mRefreshListener != null) return HEAD_TYPE;
//        if (position == getItemCount() - 1 && mLoadMoreListener != null) return FOOT_TYPE;
        return super.getItemViewType(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        prepareRefresh = true;
        recyclerView.setOnTouchListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mLoadMoreListener == null) return;
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                int firstPosition = lm.findFirstVisibleItemPosition();
                int visibleItemCount = recyclerView.getChildCount();
                if (newState == 0 && firstPosition <= 1) {
                    prepareRefresh = true;
                } else if (firstPosition > 1) {
                    prepareRefresh = false;
                }
                if (onBindItemCount() == 0) return;
                if (getRealDataPosition(lastVisibleItemPosition) >= onBindItemCount() - 1 && visibleItemCount > 0 && lm.findViewByPosition(lastVisibleItemPosition).getBottom() >= recyclerView.getHeight()) {
                    if (!isLoadingMore && hasMoreData) {
                        isLoadingMore = true;
                        mLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder holder = null;
        if (viewType == HEAD_TYPE) {
            holder = mHeadView == null ? new BaseViewHolder(getDefaultHeadView()) : new BaseViewHolder(mHeadView);
        } else if (viewType == FOOT_TYPE) {
            holder = mFootView == null ? new BaseViewHolder(getDefaultFootView()) : new BaseViewHolder(mFootView);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, null);
            holder = new BaseViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        if (getItemViewType(position) != HEAD_TYPE && getItemViewType(position) != FOOT_TYPE) {
            if (listener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v, getRealDataPosition(position));
                    }
                });
            }
            onBindItemData(holder, getRealDataPosition(position));
        }
    }

    private float startY;
    private float startTopMargin;
    private float previousY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (this.mRefreshListener != null) {
            boolean b = false;
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mHeadView.getLayoutParams();
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    startTopMargin = params.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    if (this.prepareRefresh) {
                        this.headTouchEnd();
                        this.startY = 0.0F;
                        this.previousY = 0.0F;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (this.startY == 0.0F) {
                        this.startY = event.getY();
                    }
                    if (this.prepareRefresh) {
                        if (event.getY() < this.startY && params.topMargin != -params.height) {
                            b = true;
                        }
                        this.headTouchMove(event);
                    }
                    this.previousY = event.getY();
            }
            return b;
        } else {
            return false;
        }
    }

    private void headTouchEnd() {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mHeadView.getLayoutParams();
        if (params.topMargin >= 0) {
            handler.sendEmptyMessageDelayed(1, 1);
        } else if (params.topMargin != -params.height) {
            handler.sendEmptyMessageDelayed(0, 1);
        }
    }

    private void headTouchMove(MotionEvent event) {
        float endY = event.getY();
        float moveY = endY - this.startY;
        if (moveY > 0.0F) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mHeadView.getLayoutParams();
            moveY /= 3.0F;
            int viewHeight = (int) Math.abs(startTopMargin);
            float topMargin = moveY - (float) viewHeight;
            if (topMargin < (float) (-viewHeight)) {
                topMargin = (float) (-viewHeight);
            }
            params.topMargin = (int) topMargin;
            mHeadView.setLayoutParams(params);
            if (!isRefreshing) {
                if (this.animationListener != null) {
                    this.animationListener.offsetY(mHeadView, moveY);
                }
            }
        }
    }

    private View getDefaultHeadView() {
        float scale = mRecyclerView.getContext().getResources().getDisplayMetrics().density;
        int height = (int) (45 * scale + 0.5f);
        TextView headTextView = new TextView(mRecyclerView.getContext());
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        headTextView.setGravity(Gravity.CENTER);
        params.topMargin = -height;
        headTextView.setLayoutParams(params);
        mHeadView = headTextView;
        return mHeadView;
    }

    private View getDefaultFootView() {
        float scale = mRecyclerView.getContext().getResources().getDisplayMetrics().density;
        int height = (int) (45 * scale + 0.5f);
        TextView footTextView = new TextView(mRecyclerView.getContext());
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        footTextView.setGravity(Gravity.CENTER);
        params.bottomMargin = -height;
        footTextView.setLayoutParams(params);
        mFootView = footTextView;
        return mFootView;
    }

    public void completeRefresh() {
        isRefreshing = false;
        hasMoreData = true;
        handler.sendEmptyMessageDelayed(0, 1);
        this.animationListener.endRefreshAnima(mHeadView);
    }

    public void completeLoadMore(boolean hasMoreData) {
        this.isLoadingMore = false;
        this.hasMoreData = hasMoreData;
    }

    public void setRefreshListener(RefreshListener mRefreshListener) {
        this.mRefreshListener = mRefreshListener;
    }

    public void setLoadMoreListener(LoadMoreListener mLoadMoreListener) {
        this.mLoadMoreListener = mLoadMoreListener;
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setHeadView(View mHeadView) {
        this.mHeadView = mHeadView;
        this.useDefaultHead = false;
    }

    public void setFootView(View mFootView) {
        this.mFootView = mFootView;
        this.useDefaultFoot = false;
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }

    public interface RefreshListener {
        void onRefresh();
    }

    public interface OnItemClickListener {
        void onClick(View item, int position);
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> viewArray;

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        public <V extends View> V getView(int id) {
            return (V) this.findView(id);
        }

        private View findView(int id) {
            if (this.viewArray == null) {
                this.viewArray = new SparseArray();
            }

            View view = (View) this.viewArray.get(id);
            if (view == null) {
                view = this.itemView.findViewById(id);
                this.viewArray.put(id, view);
            }
            return view;
        }
    }
}
