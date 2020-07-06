package com.miraclink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miraclink.R;
import com.miraclink.widget.SlideHorizontalView;

import java.util.List;

public class SlideViewTimeAdapter extends RecyclerView.Adapter<SlideViewTimeAdapter.TimeViewHolder> implements SlideHorizontalView.IAutoLocateHorizontalView {
    private Context context;
    private List<String> times;
    private View view;

    public SlideViewTimeAdapter(List<String> times) {
        this.times = times;
    }

    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slide_time,parent,false);
        return new TimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        holder.textTime.setText(times.get(position));
    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    @Override
    public View getItemView() {
        return view;
    }

    @Override
    public void onViewSelected(boolean isSelected, int pos, RecyclerView.ViewHolder holder, int itemWidth) {
        if (isSelected){
            ((TimeViewHolder)holder).textTime.setTextSize(20);
        }else {
            ((TimeViewHolder)holder).textTime.setTextSize(16);
        }
    }

    class TimeViewHolder extends RecyclerView.ViewHolder{
        private TextView textTime;

        public TimeViewHolder(@NonNull View itemView) {
            super(itemView);
            textTime = itemView.findViewById(R.id.textItemSlideViewTime);
        }
    }
}
