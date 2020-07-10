package com.miraclink.content.check;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.miraclink.R;
import com.miraclink.content.ContentActivity;
import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.database.UserDatabaseManager;
import com.miraclink.model.User;
import com.miraclink.utils.AppExecutors;
import com.miraclink.utils.BroadCastAction;
import com.miraclink.utils.LogUtil;
import com.miraclink.utils.SharePreUtils;
import com.miraclink.utils.Utils;


public class UserCheckFragment extends Fragment implements View.OnClickListener, UserCheckContract.IView {
    private static final String TAG = UserCheckFragment.class.getSimpleName();
    private Button btStart;
    private Button btLeg, btNeck, btArm, btChest, btStomach, btBack, btRear;
    private TextView tvTime;
    private Button btAdd, btCut;
    private TextView tvName, tvId;

    private UserCheckContract.Presenter presenter;
    ContentActivity activity;
    private IUserDatabaseManager iUserDatabaseManager;
    private BroadcastReceiver receiver;

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
        if (!SharePreUtils.getCurrentID(getContext()).isEmpty()) {
            LogUtil.i(TAG, "get current id != null" + SharePreUtils.getCurrentID(getContext()));
            presenter.getUserInfo(SharePreUtils.getCurrentID(getContext()));
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadCastAction.USER_CHANGED);
        filter.addAction(BroadCastAction.ACTION_GATT_DISCONNECTED);
        getContext().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        getContext().unregisterReceiver(receiver);
    }

    private void initParam() {
        iUserDatabaseManager = UserDatabaseManager.getInstance(getContext(), AppExecutors.getInstance());
        presenter = new UserCheckPresenter(this, iUserDatabaseManager);
        presenter.getBlueService(activity.getBlueService());
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BroadCastAction.USER_CHANGED)) {
                    presenter.onUserChanged();
                    presenter.getUserInfo(SharePreUtils.getCurrentID(getContext()));
                }else if (intent.getAction().equals(BroadCastAction.ACTION_GATT_DISCONNECTED)){
                    presenter.onDisconnected();
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
        btAdd = view.findViewById(R.id.btUserCheckFragmentAdd);
        btAdd.setOnClickListener(this);
        btCut = view.findViewById(R.id.btUserCheckFragmentCut);
        btCut.setOnClickListener(this);
        btArm = view.findViewById(R.id.btUserCheckFragmentArm);
        btChest = view.findViewById(R.id.btUserCheckFragmentChest);
        btStomach = view.findViewById(R.id.btUserCheckFragmentStomach);
        btBack = view.findViewById(R.id.btUserCheckFragmentBack);
        btRear = view.findViewById(R.id.btUserCheckFragmentRear);
        btArm.setOnClickListener(this);
        btStomach.setOnClickListener(this);
        btBack.setOnClickListener(this);
        btChest.setOnClickListener(this);
        btRear.setOnClickListener(this);
        tvName = view.findViewById(R.id.textUserCheckFragmentName);
        tvId = view.findViewById(R.id.textUserCheckFragmentId);
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
                        Toast.makeText(getContext(), "ble not connected", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.btUserCheckFragmentAdd:
                presenter.onCheckRateAdd();
                break;
            case R.id.btUserCheckFragmentCut:
                presenter.onCheckRateCut();
                break;
            case R.id.btUserCheckFragmentLeg:
                presenter.onCheckLegClick();
                break;
            case R.id.btUserCheckFragmentNeck:
                presenter.onCheckNeckClick();
                break;
            case R.id.btUserCheckFragmentArm:
                presenter.onCheckArmClick();
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
                } else {
                    btArm.setBackgroundResource(R.drawable.bg_bt_noselect);
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
                } else {
                    btLeg.setBackgroundResource(R.drawable.bg_bt_noselect);
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
            default:
                break;
        }
    }

    @Override
    public void setTimeText(String text) {
        tvTime.setText(text);
    }

    @Override
    public void setStartText(String text) {
        btStart.setText(text);
    }

    @Override
    public void refreshCheckButtonText(int armIo, int chestIo, int stomachIo, int legIo, int neckIo, int backIo, int rearIo) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btArm.setText(String.format(getContext().getString(R.string.arm), armIo * 10) + "%");
                btChest.setText(String.format(getContext().getString(R.string.chest), chestIo * 10) + "%");
                btStomach.setText(String.format(getContext().getString(R.string.stomach), stomachIo * 10) + "%");
                btLeg.setText(String.format(getContext().getString(R.string.leg), legIo * 10) + "%");
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
                    btStart.setText("start");
                } else {
                    btStart.setText("stop");
                }
            }
        });
    }

    @Override
    public void setInfoText(User user) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvName.setText(user.getName());
                tvId.setText(user.getID());
            }
        });
    }

}
