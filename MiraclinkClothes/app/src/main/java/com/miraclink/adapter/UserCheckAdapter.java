package com.miraclink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miraclink.R;
import com.miraclink.model.User;
import com.miraclink.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class UserCheckAdapter extends RecyclerView.Adapter<UserCheckAdapter.CheckHolder> {
    private List<User> users;
    private Context context;

    public UserCheckAdapter() {
        users = new ArrayList<>();
    }

    @NonNull
    @Override
    public CheckHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_check_list, parent, false);
        return new CheckHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckHolder holder, int position) {
        User user = users.get(position);
        holder.textName.setText(user.getName());
        holder.textCompose.setText(Utils.getComposeText(context,user.getCompose()));
        holder.textMode.setText(Utils.getModeText(context,user.getMode()));
        holder.textId.setText(user.getID());
        holder.imgSex.setImageResource(user.getSex() == 0 ? R.drawable.icon_check_list_male : R.drawable.icon_check_list_female);
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    public void setData(ArrayList<User> userArrayList) {
        users = userArrayList;
        notifyDataSetChanged();
    }

    class CheckHolder extends RecyclerView.ViewHolder {
        private TextView textName;
        private TextView textCompose;
        private TextView textMode;
        private TextView textId;
        private ImageView imgSex;

        public CheckHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textItemUserCheckFragmentName);
            textCompose = itemView.findViewById(R.id.textItemUserCheckFragmentCompose);
            textMode = itemView.findViewById(R.id.textItemUserCheckFragmentMode);
            textId = itemView.findViewById(R.id.textItemUserCheckFragmentId);
            imgSex = itemView.findViewById(R.id.imageItemUserCheckFragmentIcon);
        }
    }

}
