package com.miraclink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.PluralsRes;
import androidx.recyclerview.widget.RecyclerView;

import com.miraclink.R;
import com.miraclink.model.User;
import com.miraclink.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserHolder> {
    private List<User> users;
    private int selectedPosition;
    private Context context;

    public UserListAdapter() {
        users = new ArrayList<>();
    }

    public interface OnUserListItemClick {
        void onItemClick(int position, String id);
    }

    private OnUserListItemClick onUserListItemClick;

    public void setOnUserListItemClick(OnUserListItemClick click) {
        onUserListItemClick = click;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_list, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        User user = users.get(position);
        holder.textName.setText(user.getName());
        LogUtil.i("ADAPTER", "name-age:" + user.getName() + ":" + user.getAge());
        holder.textAge.setText(String.valueOf(user.getAge()));    //NotFoundException: String resource ID #0x12  int to string
        holder.textHeight.setText(String.valueOf(user.getHeight()));
        holder.textWeight.setText(String.valueOf(user.getWeight()));
        holder.textID.setText(user.getID());
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    public void setData(ArrayList<User> userArrayList) {
        users = userArrayList;
        notifyDataSetChanged();
    }

    class UserHolder extends RecyclerView.ViewHolder {
        private TextView textName;
        private TextView textAge;
        private TextView textHeight;
        private TextView textWeight;
        private TextView textID;

        public UserHolder(@NonNull final View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textItemUserListFragmentName);
            textAge = itemView.findViewById(R.id.textItemUserListFragmentAge);
            textHeight = itemView.findViewById(R.id.textItemUserListFragmentHeight);
            textWeight = itemView.findViewById(R.id.textItemUserListFragmentWeight);
            textID = itemView.findViewById(R.id.textItemUserListFragmentID);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUserListItemClick.onItemClick(getAdapterPosition(), users.get(getAdapterPosition()).getID());
                    selectedPosition = getAdapterPosition();
                    //notifyItemChanged(selectedPosition);
                    notifyDataSetChanged();
                }
            });
        }

        public void bind(){
            if (selectedPosition == getAdapterPosition()){
                itemView.setBackgroundColor(context.getResources().getColor(R.color.enterprise_cyan));  //API >23 --> context.getColor()
            }else {
                itemView.setBackground(context.getResources().getDrawable(R.drawable.bg_user_list));
            }
        }
    }
}
