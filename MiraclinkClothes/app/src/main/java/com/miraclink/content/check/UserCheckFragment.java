package com.miraclink.content.check;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.miraclink.base.CheckCallback;
import com.miraclink.content.ContentActivity;
import com.miraclink.utils.LogUtil;
import com.miraclink.utils.Utils;


public class UserCheckFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = UserCheckFragment.class.getSimpleName();
    private Button btStart;
    private Button btLeg, btNeck, btArm, btChest, btStomach, btBack, btRear;
    private TextView tvTime;
    private Button btAdd, btCut;

    private CheckCallback checkCallback;

    public void setOnCheckClickListener(CheckCallback callback) {
        checkCallback = callback;
    }

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

    private void initParam() {
        LogUtil.i(TAG, "DPI" + getContext().getResources().getDisplayMetrics().densityDpi);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btUserCheckFragmentStart:     // start and stop pause
                if (Utils.isFastDoubleClick()) {
                    LogUtil.i(TAG, "click is fast");
                } else {
                    if (ContentActivity.mState == ContentActivity.UART_PROFILE_CONNECTED) {
                        checkCallback.onCheckStartClick();
                    } else {
                        Toast.makeText(getContext(), "ble not connected", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.btUserCheckFragmentAdd:
                checkCallback.onCheckRateAdd();
                break;
            case R.id.btUserCheckFragmentCut:
                checkCallback.onCheckRateCut();
                break;

            case R.id.btUserCheckFragmentLeg:
                checkCallback.onCheckLegClick();
                break;
            case R.id.btUserCheckFragmentNeck:
                checkCallback.onCheckNeckClick();
                break;
            case R.id.btUserCheckFragmentArm:
                checkCallback.onCheckArmClick();
                break;
            case R.id.btUserCheckFragmentChest:
                checkCallback.onCheckChestClick();
                break;
            case R.id.btUserCheckFragmentStomach:
                checkCallback.onCheckStomachClick();
                break;
            case R.id.btUserCheckFragmentBack:
                checkCallback.onCheckBackClick();
                break;
            case R.id.btUserCheckFragmentRear:
                checkCallback.onCheckRearClick();
                break;
            default:
                break;
        }
    }

    public TextView timeText() {
        return tvTime;
    }

    public Button startButton() {
        return btStart;
    }

    public void setButtonText(int status) {

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


    public void setButtonBackground(int i,boolean isSelect){
        switch (i){
            case 1:
                if (isSelect){
                    btArm.setBackgroundResource(R.drawable.bg_bt_selcet);
                }else {
                    btArm.setBackgroundResource(R.drawable.bg_bt_noselect);
                }
                break;
            case 2:
                if (isSelect){
                    btChest.setBackgroundResource(R.drawable.bg_bt_selcet);
                }else {
                    btChest.setBackgroundResource(R.drawable.bg_bt_noselect);
                }
                break;
            case 3:
                if (isSelect){
                    btStomach.setBackgroundResource(R.drawable.bg_bt_selcet);
                }else {
                    btStomach.setBackgroundResource(R.drawable.bg_bt_noselect);
                }
                break;
            case 4:
                if (isSelect){
                    btLeg.setBackgroundResource(R.drawable.bg_bt_selcet);
                }else {
                    btLeg.setBackgroundResource(R.drawable.bg_bt_noselect);
                }
                break;
            case 5:
                if (isSelect){
                    btNeck.setBackgroundResource(R.drawable.bg_bt_selcet);
                }else {
                    btNeck.setBackgroundResource(R.drawable.bg_bt_noselect);
                }
                break;
            case 6:
                if (isSelect){
                    btBack.setBackgroundResource(R.drawable.bg_bt_selcet);
                }else {
                    btBack.setBackgroundResource(R.drawable.bg_bt_noselect);
                }
                break;
            case 7:
                if (isSelect){
                    btRear.setBackgroundResource(R.drawable.bg_bt_selcet);
                }else {
                    btRear.setBackgroundResource(R.drawable.bg_bt_noselect);
                }
                break;
            default:
                break;
        }
    }

    public void refreshBtText(int armIo,int chestIo,int stomachIo,int legIo,int neckIo,int backIo,int rearIo) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btArm.setText(String.format(getContext().getString(R.string.arm), armIo*10) + "%");
                btChest.setText(String.format(getContext().getString(R.string.chest), chestIo*10) + "%");
                btStomach.setText(String.format(getContext().getString(R.string.stomach), stomachIo*10) + "%");
                btLeg.setText(String.format(getContext().getString(R.string.leg), legIo*10) + "%");
                btNeck.setText(String.format(getContext().getString(R.string.neck), neckIo*10) + "%");
                btBack.setText(String.format(getContext().getString(R.string.backback), backIo*10) + "%");
                btRear.setText(String.format(getContext().getString(R.string.rear), rearIo*10) + "%");
            }
        });
    }


}
