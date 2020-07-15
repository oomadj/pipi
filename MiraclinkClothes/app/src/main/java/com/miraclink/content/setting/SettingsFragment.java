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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.miraclink.networks.NetworkUtil;
import com.miraclink.utils.AppExecutors;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.LogUtil;
import com.miraclink.utils.SharePreUtils;
import com.miraclink.widget.ScrollerLayout;
import com.miraclink.widget.SlideHorizontalView;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements View.OnClickListener, ScrollerLayout.OnSelectPositionClick, SettingsContract.IView, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = SettingsFragment.class.getSimpleName();
    private String[] strongs = {"10", "20", "30", "40", "50", "60", "70", "80", "90", "100"};
    private List<String> strongsList;
    private SlideHorizontalView slideHorizontalViewRate;
    private SlideHorizontalView slideHorizontalViewStrong;
    private SlideViewTimeAdapter adapterRate;
    private SlideViewTimeAdapter adapterStrong;
    private ScrollerLayout scrollerLayout;
    private SettingsContract.Presenter presenter;
    private RadioGroup rgCompose, rgMode;
    private RadioButton rbCompose1, rbCompose2, rbCompose3, rbCompose4, rbCompose5;
    private RadioButton rbMode1, rbMode2, rbMode3, rbMode4, rbMode5;

    private String[] rates = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
    private List<String> ratesList;

    private Button btSelectDevice;
    private Button btSaveSettings;
    private IUserDatabaseManager iUserDatabaseManager;
    private BroadcastReceiver receiver;
    private User userSettings;

    private int time;
    private int strong;
    private int rate;
    private int compose;
    private int mode;

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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        presenter.queryUser(iUserDatabaseManager, SharePreUtils.getCurrentID(getContext()));
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
                strong = pos * 10;
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
                rate = pos;
            }
        });

        rgCompose = view.findViewById(R.id.rgSettingsFragmentCompose);
        rgCompose.setOnCheckedChangeListener(this);
        rgMode = view.findViewById(R.id.rgSettingsFragmentMode);
        rgMode.setOnCheckedChangeListener(this);
        rbCompose1 = view.findViewById(R.id.rbSettingsFragment1);
        rbCompose2 = view.findViewById(R.id.rbSettingsFragment2);
        rbCompose3 = view.findViewById(R.id.rbSettingsFragment3);
        rbCompose4 = view.findViewById(R.id.rbSettingsFragment4);
        rbCompose5 = view.findViewById(R.id.rbSettingsFragment5);
        rbMode1 = view.findViewById(R.id.rbSettingsFragmentMode1);
        rbMode2 = view.findViewById(R.id.rbSettingsFragmentMode2);
        rbMode3 = view.findViewById(R.id.rbSettingsFragmentMode3);
        rbMode4 = view.findViewById(R.id.rbSettingsFragmentMode4);
        rbMode5 = view.findViewById(R.id.rbSettingsFragmentMode5);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btSettingsFragmentSelect:
                Intent intent = new Intent(getActivity(), DeviceListActivity.class);
                getActivity().startActivityForResult(intent, ContentActivity.REQUEST_SELECT_DEVICE);
                break;
            case R.id.btSettingsFragmentSave:
                if (NetworkUtil.getConnectivityEnable(getContext())) {

                } else {
                    presenter.updateUserSettings(iUserDatabaseManager, time, strong, rate, compose, mode, SharePreUtils.getCurrentID(getContext()));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onGetSelectPosition(int position) {
        LogUtil.i(TAG, "scoller position:" + position);
        time = position * 10;
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

    private void setComposeInit(int i) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (i == 1) {
                    compose = 1;
                    rbCompose1.setChecked(true);
                } else if (i == 2) {
                    compose = 2;
                    rbCompose2.setChecked(true);
                } else if (i == 3) {
                    compose = 3;
                    rbCompose3.setChecked(true);
                } else if (i == 4) {
                    compose = 4;
                    rbCompose4.setChecked(true);
                } else if (i == 5) {
                    compose = 5;
                    rbCompose5.setChecked(true);
                }
            }
        });
    }

    private void setModeInit(int i) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (i == 1) {
                    mode = 1;
                    rbMode1.setChecked(true);
                } else if (i == 2) {
                    mode = 2;
                    rbMode2.setChecked(true);
                } else if (i == 3) {
                    mode = 3;
                    rbMode3.setChecked(true);
                } else if (i == 4) {
                    mode = 4;
                    rbMode4.setChecked(true);
                } else if (i == 5) {
                    mode = 5;
                    rbMode5.setChecked(true);
                }
            }
        });
    }

    @Override
    public void setUserView(User user) {
        userSettings = user;
        LogUtil.i(TAG, "test move:" + userSettings.getStrong());
        slideHorizontalViewRate.setInitPos(userSettings.getRate());
        slideHorizontalViewStrong.setInitPos(userSettings.getStrong() / 10);
        setTimeInit(userSettings.getTime() - 1);
        setComposeInit(userSettings.getCompose());
        setModeInit(userSettings.getMode());
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == rgCompose) {
            switch (checkedId) {
                case R.id.rbSettingsFragment1:
                    compose = 1;
                    break;
                case R.id.rbSettingsFragment2:
                    compose = 2;
                    break;
                case R.id.rbSettingsFragment3:
                    compose = 3;
                    break;
                case R.id.rbSettingsFragment4:
                    compose = 4;
                    break;
                case R.id.rbSettingsFragment5:
                    compose = 5;
                    break;
                default:
                    break;
            }
        } else {
            switch (checkedId) {
                case R.id.rbSettingsFragmentMode1:
                    mode = 1;
                    break;
                case R.id.rbSettingsFragmentMode2:
                    mode = 2;
                    break;
                case R.id.rbSettingsFragmentMode3:
                    mode = 3;
                    break;
                case R.id.rbSettingsFragmentMode4:
                    mode = 4;
                    break;
                case R.id.rbSettingsFragmentMode5:
                    mode = 5;
                    break;
                default:
                    break;
            }

        }
    }
}
