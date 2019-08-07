package com.yang.example.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yang.example.R;

import java.util.HashMap;
import java.util.List;

public class ExampleListAdapter extends RecyclerView.Adapter<ExampleListAdapter.ExampleListViewHolder> {


    private List<HashMap<String, String>> data;

    public ExampleListAdapter(List<HashMap<String, String>> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ExampleListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ExampleListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_example_list, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleListViewHolder viewHolder, final int i) {
        viewHolder.title.setText(data.get(i).get("name"));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    v.getContext().startActivity(new Intent(v.getContext(), Class.forName(data.get(i).get("class"))));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ExampleListViewHolder extends RecyclerView.ViewHolder {

        private TextView title;

        public ExampleListViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_title);
        }
    }
}
