package com.miraclink.content.list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miraclink.R;
import com.miraclink.adapter.UserListAdapter;
import com.miraclink.content.ContentActivity;
import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.database.UserDatabaseManager;
import com.miraclink.model.User;
import com.miraclink.networks.NetworkController;
import com.miraclink.networks.NetworkUtil;
import com.miraclink.utils.AppExecutors;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.SharePreUtils;
import com.miraclink.widget.UserListRecyclerDecoration;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment implements UserListAdapter.OnUserListItemClick, IUserDatabaseManager.QueryAllUserCallback {
    private static final String TAG = UserListFragment.class.getSimpleName();
    private RecyclerView recyclerUser;
    private UserListAdapter userAdapter;
    private ArrayList<User> users;
    private UserListRecyclerDecoration decoration;
    private UserListContract.Presenter presenter;
    private BroadcastReceiver receiver;
    private IUserDatabaseManager iUserDatabaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View messageLayout = inflater.inflate(R.layout.fragment_user_list, container, false);
        return messageLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initParam();
        initView(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadCastAction.USER_LIST_DATA);
        getContext().registerReceiver(receiver,filter);

        if (NetworkUtil.getConnectivityEnable(getContext())){
            presenter.getUserList(NetworkController.getInstance());
        }else {
            presenter.queryAllUser(iUserDatabaseManager,this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(receiver);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (NetworkUtil.getConnectivityEnable(getContext())){
            presenter.getUserList(NetworkController.getInstance());
        }else {
            presenter.queryAllUser(iUserDatabaseManager,this);
        }
    }

    private void initParam() {
        iUserDatabaseManager = UserDatabaseManager.getInstance(getContext(), AppExecutors.getInstance());
        presenter = new UserListPresenter();
        userAdapter = new UserListAdapter();
        userAdapter.setOnUserListItemClick(this);
        decoration = new UserListRecyclerDecoration();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BroadCastAction.USER_LIST_DATA)){
                    users = intent.<User>getParcelableArrayListExtra("DATA");
                    userAdapter.setData(users);
                    if (SharePreUtils.getCurrentID(getContext()).isEmpty()){
                        SharePreUtils.setCurrentID(getContext(),users.get(0).getID()); // init select 0
                    }
                }
            }
        };
    }

    private void initView(View view) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerUser = view.findViewById(R.id.recyclerUserListFragmentUser);
        recyclerUser.setLayoutManager(linearLayoutManager);
        recyclerUser.setAdapter(userAdapter);
        recyclerUser.addItemDecoration(decoration);
    }

    @Override
    public void onItemClick(int position, String id) {
        Toast.makeText(getContext(),"position"+position,Toast.LENGTH_SHORT).show();
        SharePreUtils.setCurrentID(getContext(),users.get(position).getID());  // save check user id
        getContext().sendBroadcast(new Intent(BroadCastAction.USER_CHANGED));  // user changed
    }

    @Override
    public void onQueried(List<User> userList) {
        users = (ArrayList<User>) userList;
        userAdapter.setData(users);
        if (SharePreUtils.getCurrentID(getContext()).isEmpty() && users.size() != 0){
            SharePreUtils.setCurrentID(getContext(),users.get(0).getID()); // init select 0
        }
    }
}
