package com.yang.example.utils;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class TBLayoutManager extends RecyclerView.LayoutManager {

    private RecyclerView mRecyclerView;
    private int currentPosition = 0;
    private int pageHeight;

    public TBLayoutManager(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
    }

    public void setPageHeight(int pageHeight) {
        this.pageHeight = pageHeight;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        detachAndScrapAttachedViews(recycler);
        int itemCount = getItemCount();

        int previewIndex = currentPosition - 1;
        int currentIndex = currentPosition;
        int nextIndex = currentPosition + 1;

        if (pageHeight == 0) {
            pageHeight = (int) (mRecyclerView.getHeight() * 0.8f);
        }


        if (previewIndex >= 0 && previewIndex < itemCount) {
            layoutChild(recycler, -pageHeight, previewIndex);
        }

        if (currentIndex >= 0 && currentIndex < itemCount) {
            layoutChild(recycler, 0, currentIndex);
        }

        if (nextIndex >= 0 && nextIndex < itemCount) {
            layoutChild(recycler, pageHeight, nextIndex);
        }
    }

    private void layoutChild(RecyclerView.Recycler recycler, int top, int position) {
        final View view = recycler.getViewForPosition(position);
        addView(view);
        measureChildWithMargins(view, 0, 0);

        int widthSpace = getDecoratedMeasuredWidth(view);
        layoutDecoratedWithMargins(view, 0, top, widthSpace, top + pageHeight);
    }

//    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
//
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            RecyclerView.ViewHolder childViewHolder = mRecyclerView.getChildViewHolder(v);
//            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
//                mItemTouchHelper.startSwipe(childViewHolder);
//            }
//            return false;
//        }
//    };

}
