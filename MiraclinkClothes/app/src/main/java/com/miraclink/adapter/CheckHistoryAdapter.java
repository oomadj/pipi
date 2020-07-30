package com.miraclink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miraclink.R;
import com.miraclink.model.CheckHistory;
import com.miraclink.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CheckHistoryAdapter extends RecyclerView.Adapter<CheckHistoryAdapter.HistoryHolder> {
    private Context context;
    private List<CheckHistory> histories;

    public CheckHistoryAdapter() {
        histories = new ArrayList<>();
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_check_history, parent, false);
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
        CheckHistory history = histories.get(position);
        holder.textTime.setText(history.getNum());
        holder.textMode.setText(Utils.getModeText(context, history.getMode()));
        holder.textHour.setText(history.getClassHour() + "");
        holder.textProgram.setText(Utils.getComposeText(context, history.getCompose()));
        holder.textBalance.setText(history.getBalance() + "");
    }

    @Override
    public int getItemCount() {
        return histories == null ? 0 : histories.size();
    }

    public void setData(ArrayList<CheckHistory> histories) {
        this.histories = histories;
        notifyDataSetChanged();
    }

    class HistoryHolder extends RecyclerView.ViewHolder {
        private TextView textTime;
        private TextView textMode;
        private TextView textProgram;
        private TextView textHour;
        private TextView textBalance;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            textTime = itemView.findViewById(R.id.textItemCheckHistoryTime);
            textMode = itemView.findViewById(R.id.textItemCheckHistoryMode);
            textProgram = itemView.findViewById(R.id.textItemCheckHistoryProgram);
            textHour = itemView.findViewById(R.id.textItemCheckHistoryHour);
            textBalance = itemView.findViewById(R.id.textItemCheckHistoryBalance);
        }
    }
}
