package com.miraclink.content.setting;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.miraclink.R;
import com.miraclink.adapter.SlideViewTimeAdapter;
import com.miraclink.bluetooth.DeviceListActivity;
import com.miraclink.content.ContentActivity;
import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.database.UserDatabaseManager;
import com.miraclink.model.User;
import com.miraclink.utils.AppExecutors;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.LogUtil;
import com.miraclink.utils.SharePreUtils;
import com.miraclink.widget.ScrollerLayout;
import com.miraclink.widget.SlideHorizontalView;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements View.OnClickListener, ScrollerLayout.OnSelectPositionClick, SettingsContract.IView {
    private static final String TAG = SettingsFragment.class.getSimpleName();
    private String[] strongs = {"10", "20", "30", "40", "50", "60", "70", "80", "90", "100"};
    private List<String> strongsList;
    private SlideHorizontalView slideHorizontalViewRate;
    private SlideHorizontalView slideHorizontalViewStrong;
    private SlideViewTimeAdapter adapterRate;
    private SlideViewTimeAdapter adapterStrong;
    private ScrollerLayout scrollerLayout;
    private SettingsContract.Presenter presenter;

    private String[] rates = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
    private List<String> ratesList;

    private Button btSelectDevice;
    private Button btSaveSettings;
    private IUserDatabaseManager iUserDatabaseManager;
    private BroadcastReceiver receiver;
    private User userSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View messageView = inflater.inflate(R.layout.fragment_user_settings, container, false);
        return messageView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initParam();
        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        btSelectDevice.setText(ContentActivity.bleAddress);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadCastAction.USER_CHANGED);
        getContext().registerReceiver(receiver, filter);
        presenter.queryUser(iUserDatabaseManager, SharePreUtils.getCurrentID(getContext()));
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(receiver);
    }

    private void initParam() {
        iUserDatabaseManager = UserDatabaseManager.getInstance(getContext(), AppExecutors.getInstance());
        presenter = new SettingsPresenter(this);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BroadCastAction.USER_CHANGED)) {
                    presenter.queryUser(iUserDatabaseManager, SharePreUtils.getCurrentID(getContext()));
                }
            }
        };
    }

    private void initView(View view) {
        strongsList = new ArrayList<>();
        for (String time : strongs) {
            strongsList.add(time);
        }
        ratesList = new ArrayList<>();
        for (String rate : rates) {
            ratesList.add(rate);
        }

        btSelectDevice = view.findViewById(R.id.btSettingsFragmentSelect);
        btSelectDevice.setOnClickListener(this);
        btSaveSettings = view.findViewById(R.id.btSettingsFragmentSave);
        btSaveSettings.setOnClickListener(this);
        adapterRate = new SlideViewTimeAdapter(ratesList);
        adapterStrong = new SlideViewTimeAdapter(strongsList);
        slideHorizontalViewRate = view.findViewById(R.id.slideSettingsFragmentRate);
        LinearLayoutManager linearLayoutManagerRate = new LinearLayoutManager(getContext());
        linearLayoutManagerRate.setOrientation(LinearLayoutManager.HORIZONTAL);
        slideHorizontalViewRate.setLayoutManager(linearLayoutManagerRate);
        slideHorizontalViewRate.setAdapter(adapterRate);
        slideHorizontalViewRate.setOnSelectedPositionChangedListener(new SlideHorizontalView.OnSelectedPositionChangedListener() {
            @Override
            public void selectedPositionChanged(int pos) {
                Toast.makeText(getContext(), "pos 1:" + pos, Toast.LENGTH_SHORT).show();
            }
        });
        scrollerLayout = view.findViewById(R.id.scrollerSettingsFragmentTime);
        scrollerLayout.setOnSelectPositionClick(this);

        slideHorizontalViewStrong = view.findViewById(R.id.slideSettingsFragmentStrong);
        LinearLayoutManager linearLayoutManagerStrong = new LinearLayoutManager(getContext());
        linearLayoutManagerStrong.setOrientation(LinearLayoutManager.HORIZONTAL);
        slideHorizontalViewStrong.setLayoutManager(linearLayoutManagerStrong);
        slideHorizontalViewStrong.setAdapter(adapterStrong);
        slideHorizontalViewStrong.setOnSelectedPositionChangedListener(new SlideHorizontalView.OnSelectedPositionChangedListener() {
            @Override
            public void selectedPositionChanged(int pos) {
                Toast.makeText(getContext(), "pos 2:" + pos, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btSettingsFragmentSelect:
                Intent intent = new Intent(getActivity(), DeviceListActivity.class);
                getActivity().startActivityForResult(intent, ContentActivity.REQUEST_SELECT_DEVICE);
                break;
            case R.id.btSettingsFragmentSave:
                scrollerLayout.scrollTo(scrollerLayout.getLayoutLong() / 5, 0);
                break;
            default:
                break;
        }
    }

    @Override
    public void onGetSelectPosition(int position) {
        LogUtil.i(TAG, "scoller position:" + position);
    }

    private void setTimeInit(int i) {
        int b;
        if (i <= 0) {
            i = 10;    //default time
        }
        b = i / 10;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scrollerLayout.scrollTo(scrollerLayout.getLayoutLong() * b / 5, 0);
            }
        });

    }

    @Override
    public void setUserView(User user) {
        userSettings = user;
        LogUtil.i(TAG, "test move:" + userSettings.getStrong());
        //slideHorizontalViewStrong.moveToPosition(userSettings.getStrong()/10-1);
        //slideHorizontalViewRate.moveToPosition(userSettings.getRate()-1);
        slideHorizontalViewRate.setInitPos(userSettings.getRate() - 1);
        slideHorizontalViewStrong.setInitPos(userSettings.getStrong() / 10 - 1);
        setTimeInit(userSettings.getTime() - 1);
    }
}
