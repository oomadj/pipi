package com.miraclink.content.check;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miraclink.R;
import com.miraclink.adapter.UserCheckAdapter;
import com.miraclink.content.ContentActivity;
import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.database.UserDatabaseManager;
import com.miraclink.model.CheckHistory;
import com.miraclink.model.User;
import com.miraclink.utils.AppExecutors;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.LogUtil;
import com.miraclink.utils.SharePreUtils;
import com.miraclink.utils.Utils;
import com.miraclink.widget.UserListRecyclerDecoration;

import java.util.ArrayList;
import java.util.List;


public class UserCheckFragment extends Fragment implements View.OnClickListener, UserCheckContract.IView, IUserDatabaseManager.QueryAllUserCallback, View.OnTouchListener, IUserDatabaseManager.QueryUserByCheckIDsCallback {
    private static final String TAG = UserCheckFragment.class.getSimpleName();
    private Button btStart;
    private Button btLeg, btLegZero, btNeck, btArm, btArmZero, btChest, btStomach, btBack, btRear;
    private TextView tvTime;
    private ImageButton btAdd, btCut;
    private TextView tvName, tvId, tvCompose, tvMode;
    private DrawerLayout drawerLayout;
    private ImageView imgSlide;
    private ImageView imgSelectAll;

    private HorizontalScrollView horizontalScrollView;
    private ImageView imgChangeBody1;
    private RecyclerView recyclerView;
    private UserCheckAdapter userCheckAdapter;
    private UserListRecyclerDecoration decoration;
    private ArrayList<User> users;

    private UserCheckContract.Presenter presenter;
    ContentActivity activity;
    private IUserDatabaseManager iUserDatabaseManager;
    private BroadcastReceiver receiver;

    private int time;
    private int compose;
    private int mode;
    String[] ids;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View messageLayout = inflater.inflate(R.layout.fragment_user_check, container, false);
        return messageLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initParam();
        initView(view);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (ContentActivity) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!SharePreUtils.getCheckID(getContext()).isEmpty()) {
            LogUtil.i(TAG, "get current id != null" + SharePreUtils.getCheckID(getContext()));
            presenter.getUserInfo(SharePreUtils.getCheckID(getContext()));
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadCastAction.USER_CHANGED);
        getContext().registerReceiver(receiver, filter);
        presenter.getBleAddress(activity.getBleAddress());
        presenter.queryAllUser(iUserDatabaseManager, this);

        ids = activity.checkUserIds.toArray(new String[activity.checkUserIds.size()]);
        presenter.queryCheckUserList(iUserDatabaseManager, this, ids);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        getContext().unregisterReceiver(receiver);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtil.i(TAG,"check hidden:"+hidden);
        if (SharePreUtils.getCheckID(getContext()) != null) {
            presenter.getUserInfo(SharePreUtils.getCheckID(getContext()));
            presenter.onUserChanged(); //refresh  // no use
        }
    }

    private void initParam() {
        iUserDatabaseManager = UserDatabaseManager.getInstance(getContext(), AppExecutors.getInstance());
        userCheckAdapter = new UserCheckAdapter();
        decoration = new UserListRecyclerDecoration();
        presenter = new UserCheckPresenter(this, iUserDatabaseManager);
        presenter.getBlueService(activity.getBlueService());
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BroadCastAction.USER_CHANGED)) {
                    presenter.onUserChanged();
                    presenter.getUserInfo(SharePreUtils.getCheckID(getContext()));
                }
            }
        };
    }

    private void initView(View view) {
        tvTime = view.findViewById(R.id.textUserCheckFragmentTimer);
        btStart = view.findViewById(R.id.btUserCheckFragmentStart);
        btStart.setOnClickListener(this);
        btLeg = view.findViewById(R.id.btUserCheckFragmentLeg);
        btNeck = view.findViewById(R.id.btUserCheckFragmentNeck);
        btLeg.setOnClickListener(this);
        btNeck.setOnClickListener(this);
        btAdd = view.findViewById(R.id.imgUserCheckFragmentAdd);
        btAdd.setOnClickListener(this);
        btCut = view.findViewById(R.id.imgUserCheckFragmentCut);
        btCut.setOnClickListener(this);
        btArm = view.findViewById(R.id.btUserCheckFragmentArm);
        btArmZero = view.findViewById(R.id.btUserCheckFragmentArmZero);
        btArmZero.setOnClickListener(this);
        btChest = view.findViewById(R.id.btUserCheckFragmentChest);
        btStomach = view.findViewById(R.id.btUserCheckFragmentStomach);
        btBack = view.findViewById(R.id.btUserCheckFragmentBack);
        btRear = view.findViewById(R.id.btUserCheckFragmentRear);
        btLegZero = view.findViewById(R.id.btUserCheckFragmentLegZero);
        btLegZero.setOnClickListener(this);
        btArm.setOnClickListener(this);
        btStomach.setOnClickListener(this);
        btBack.setOnClickListener(this);
        btChest.setOnClickListener(this);
        btRear.setOnClickListener(this);
        tvName = view.findViewById(R.id.textUserCheckFragmentName);
        tvId = view.findViewById(R.id.textUserCheckFragmentId);
        tvCompose = view.findViewById(R.id.textUserCheckFragmentCompose);
        tvMode = view.findViewById(R.id.textUserCheckFragmentMode);
        imgChangeBody1 = view.findViewById(R.id.imgUserCheckFragmentChangeBody1);
        imgChangeBody1.setOnClickListener(this);
        horizontalScrollView = view.findViewById(R.id.scrollViewUserCheckFragment);
        horizontalScrollView.setOnTouchListener(this);
        drawerLayout = view.findViewById(R.id.drawerLayoutUserCheckFragment);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                ids = activity.checkUserIds.toArray(new String[activity.checkUserIds.size()]);
                presenter.queryCheckUserList(iUserDatabaseManager, UserCheckFragment.this, ids);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        imgSlide = view.findViewById(R.id.imgUserCheckFragmentSlide);
        imgSlide.setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.recyclerUserCheckFragment);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(userCheckAdapter);
        recyclerView.addItemDecoration(decoration);

        imgSelectAll = view.findViewById(R.id.imgUserCheckFragmentSelectAll);
        imgSelectAll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btUserCheckFragmentStart:     // start and stop pause
                if (Utils.isFastDoubleClick()) {
                    LogUtil.i(TAG, "click is fast");
                } else {
                    if (ContentActivity.mState == ContentActivity.UART_PROFILE_CONNECTED) {
                        presenter.onCheckStartClick();
                    } else {
                        Toast.makeText(getContext(), "ble not connect", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.imgUserCheckFragmentAdd:
                if (ContentActivity.mState == ContentActivity.UART_PROFILE_CONNECTED) {
                    presenter.onCheckRateAdd();
                } else {
                    Toast.makeText(getContext(), "ble not connect", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imgUserCheckFragmentCut:
                if (ContentActivity.mState == ContentActivity.UART_PROFILE_CONNECTED) {
                    presenter.onCheckRateCut();
                } else {
                    Toast.makeText(getContext(), "ble not connect", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btUserCheckFragmentLeg:
                presenter.onCheckLegClick();
                break;
            case R.id.btUserCheckFragmentLegZero:
                presenter.onCheckLegZeroClick();
                break;
            case R.id.btUserCheckFragmentNeck:
                presenter.onCheckNeckClick();
                break;
            case R.id.btUserCheckFragmentArm:
                presenter.onCheckArmClick();
                break;
            case R.id.btUserCheckFragmentArmZero:
                presenter.onCheckArmZeroClick();
                break;
            case R.id.btUserCheckFragmentChest:
                presenter.onCheckChestClick();
                break;
            case R.id.btUserCheckFragmentStomach:
                presenter.onCheckStomachClick();
                break;
            case R.id.btUserCheckFragmentBack:
                presenter.onCheckBackClick();
                break;
            case R.id.btUserCheckFragmentRear:
                presenter.onCheckRearClick();
                break;
            case R.id.imgUserCheckFragmentChangeBody1:
                //LogUtil.i(TAG, "getxzx:" + horizontalScrollView.getScrollX() + " view:" + Utils.dpToPx(getContext(), 240));
                horizontalScrollView.smoothScrollTo(Utils.dpToPx(getContext(), 380), 0);
                break;
            case R.id.imgUserCheckFragmentSlide:
                drawerLayout.openDrawer(Gravity.LEFT);
            case R.id.imgUserCheckFragmentSelectAll:
                presenter.onCheckAll();
                break;
            default:
                break;
        }
    }

    @Override
    public void setButtonBackground(int i, boolean isCheck) {
        switch (i) {
            case 1:
                if (isCheck) {
                    btArm.setBackgroundResource(R.drawable.bg_bt_selcet);
                    btArmZero.setBackgroundResource(R.drawable.bg_bt_selcet);
                } else {
                    btArm.setBackgroundResource(R.drawable.bg_bt_noselect);
                    btArmZero.setBackgroundResource(R.drawable.bg_bt_noselect);
                }
                break;
            case 2:
                if (isCheck) {
                    btChest.setBackgroundResource(R.drawable.bg_bt_selcet);
                } else {
                    btChest.setBackgroundResource(R.drawable.bg_bt_noselect);
                }
                break;
            case 3:
                if (isCheck) {
                    btStomach.setBackgroundResource(R.drawable.bg_bt_selcet);
                } else {
                    btStomach.setBackgroundResource(R.drawable.bg_bt_noselect);
                }
                break;
            case 4:
                if (isCheck) {
                    btLeg.setBackgroundResource(R.drawable.bg_bt_selcet);
                    btLegZero.setBackgroundResource(R.drawable.bg_bt_selcet);
                } else {
                    btLeg.setBackgroundResource(R.drawable.bg_bt_noselect);
                    btLegZero.setBackgroundResource(R.drawable.bg_bt_noselect);
                }
                break;
            case 5:
                if (isCheck) {
                    btNeck.setBackgroundResource(R.drawable.bg_bt_selcet);
                } else {
                    btNeck.setBackgroundResource(R.drawable.bg_bt_noselect);
                }
                break;
            case 6:
                if (isCheck) {
                    btBack.setBackgroundResource(R.drawable.bg_bt_selcet);
                } else {
                    btBack.setBackgroundResource(R.drawable.bg_bt_noselect);
                }
                break;
            case 7:
                if (isCheck) {
                    btRear.setBackgroundResource(R.drawable.bg_bt_selcet);
                } else {
                    btRear.setBackgroundResource(R.drawable.bg_bt_noselect);
                }
                break;
            case 8:
                if (isCheck) {
                    imgSelectAll.setImageResource(R.drawable.icon_select_check_yes);
                } else {
                    imgSelectAll.setImageResource(R.drawable.icon_select_check_no);
                }
            default:
                break;
        }
    }

    @Override
    public void setTimeText(String text) {
        tvTime.setText(text);
    }

    //check finished to save check history
    @Override
    public void setStartText(String text) {
        //btStart.setText(text);
        CheckHistory checkHistory = new CheckHistory();
        checkHistory.setID(SharePreUtils.getCurrentID(getContext()));
        checkHistory.setNum(Utils.getDate());
        checkHistory.setMode(mode);
        checkHistory.setCompose(compose);
        checkHistory.setClassHour(5);
        checkHistory.setBalance(1200);
        presenter.onInsertCheckHistory(iUserDatabaseManager, checkHistory);
    }

    @Override
    public void refreshCheckButtonText(int armIo, int chestIo, int stomachIo, int legIo, int neckIo, int backIo, int rearIo) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG,"check hidden refresh");
                btArmZero.setText(armIo * 10 + "%");
                btChest.setText(String.format(getContext().getString(R.string.chest), chestIo * 10) + "%");
                btStomach.setText(String.format(getContext().getString(R.string.stomach), stomachIo * 10) + "%");
                btLegZero.setText(legIo * 10 + "%");
                btNeck.setText(String.format(getContext().getString(R.string.neck), neckIo * 10) + "%");
                btBack.setText(String.format(getContext().getString(R.string.backback), backIo * 10) + "%");
                btRear.setText(String.format(getContext().getString(R.string.rear), rearIo * 10) + "%");
            }
        });
    }

    @Override
    public void refreshStartButtonText(int status) {
        LogUtil.i(TAG, "test-status:" + ContentActivity.checkStatus);
        getActivity().runOnUiThread(new Runnable() {   //TODO 子线程中setText可能不会更新？
            @Override
            public void run() {
                if (status == 0) {
                    btStart.setBackgroundResource(R.drawable.icon_start);
                } else {
                    btStart.setBackgroundResource(R.drawable.icon_pause);
                }
            }
        });
    }

    @Override
    public void setInfoText(User user) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (user != null) {
                    tvName.setText(user.getName());
                    tvId.setText("ID:" + user.getID());
                    tvCompose.setText(Utils.getComposeText(getContext(), user.getCompose()));
                    tvMode.setText(Utils.getModeText(getContext(), user.getMode()));
                    presenter.onInit(user.getTime(), user.getRate(), user.getStrong());
                    time = user.getTime();
                    compose = user.getCompose();
                    mode = user.getMode();
                }
            }
        });
    }

    @Override
    public void onQueried(List<User> userList) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (horizontalScrollView.getScrollX() > Utils.dpToPx(getContext(), 120)) {
                    imgChangeBody1.setImageResource(R.drawable.icon_left_sliding);
                } else {
                    imgChangeBody1.setImageResource(R.drawable.icon_right_sliding);
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onQueriedCheckUser(List<User> users) {
        this.users = (ArrayList<User>) users;
        LogUtil.i(TAG, "check list:" + users.size());
        userCheckAdapter.setData(this.users);
    }
}
