package com.yang.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yang.base.utils.LogUtil;
import com.yang.example.R;

import java.util.ArrayList;

public class TBGoodsAdapter extends RecyclerView.Adapter<TBGoodsAdapter.TBGoodsHolder> {

    private ArrayList<String> mList = new ArrayList<>();
    private static final int[] colors = new int[]{R.color.colorAccent, R.color.green, R.color.blue, R.color.yellow};

    public TBGoodsAdapter() {
        for (int i = 0; i < 31; i++) {
            mList.add("" + i);
        }
    }

    @NonNull
    @Override
    public TBGoodsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TBGoodsHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_t_b_goods, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TBGoodsHolder tbGoodsHolder, int i) {
        tbGoodsHolder.bindData(i, mList.get(i));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class TBGoodsHolder extends RecyclerView.ViewHolder {

        private TextView text_view;

        public TBGoodsHolder(@NonNull View itemView) {
            super(itemView);
            LogUtil.i("onCreateViewHolder - TBGoodsHolder " + hashCode());
            text_view = itemView.findViewById(R.id.text_view);
        }

        private void bindData(int position, String desc) {
            LogUtil.i("" + hashCode() + " onBindViewHolder - bindData : " + position);
            text_view.setText(desc);
            itemView.setBackgroundColor(itemView.getContext().getResources().getColor(colors[position % 4]));
        }
    }
}
