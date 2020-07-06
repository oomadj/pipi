package com.miraclink.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miraclink.R;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainHolder> {
    List<String> list;

    public MainAdapter() {
        list = new ArrayList<>();
    }

    @NonNull
    @Override
    public MainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main,parent,false);
        return new MainHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainHolder holder, int position) {
        holder.text.setText(list.get(position));
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(ArrayList<String> list){
        this.list = list;
        notifyDataSetChanged();
    }

    class MainHolder extends RecyclerView.ViewHolder{
        private TextView text;

        public MainHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textItemMainactivity);
        }
    }
}
