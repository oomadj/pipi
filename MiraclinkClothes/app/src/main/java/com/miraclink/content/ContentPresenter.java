package com.miraclink.content;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.CountDownTimer;
import android.os.Handler;

import com.miraclink.base.BaseCallback;
import com.miraclink.base.CheckCallback;
import com.miraclink.bluetooth.MyBlueService;
import com.miraclink.content.check.UserCheckFragment;
import com.miraclink.utils.ByteUtils;
import com.miraclink.utils.LogUtil;
import com.miraclink.utils.Utils;

public class ContentPresenter implements ContentContract.Presenter, BaseCallback , CheckCallback {
    private static final String TAG = ContentPresenter.class.getSimpleName();
    public static int checkStatus = 0;   // 0 stop // 1 starting
    UserCheckFragment checkFragment;
    MyBlueService blueService;
    private int ioRate = 5; //init rate
    private MyCountDownTimer myCountDownTimer;
    private long pauseTime = 2*60*1000;    //init time

    private boolean isLegChecked;
    private boolean isArmChecked;
    private boolean isChestChecked;
    private boolean isStomachChecked;
    private boolean isNeckChecked;
    private boolean isBackChecked;
    private boolean isRearChecked;
    private int armIo = ioRate;  //TODO get io to user info
    private int chestIo = ioRate;
    private int stomachIo = ioRate;
    private int legIo = ioRate;
    private int neckIo = ioRate;
    private int backIo = ioRate;
    private int rearIo = ioRate;

    public ContentPresenter() {
    }

    @Override
    public void onDeviceChange(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        String value = ByteUtils.toHexString(bluetoothGattCharacteristic.getValue());
        LogUtil.i(TAG, "xzx--on device change -values:" + value);
        if (ByteUtils.isEqual(bluetoothGattCharacteristic.getValue(), Utils.startBackByte)) {
            checkStatus = 1;
            LogUtil.i(TAG, "checkStatus == 1");
            checkFragment.setButtonText(checkStatus);
        } else if (ByteUtils.isEqual(bluetoothGattCharacteristic.getValue(), Utils.stopBackByte)) {
            checkStatus = 0;
            LogUtil.i(TAG, "checkStatus == 0");
            checkFragment.setButtonText(checkStatus);
        } else if (ByteUtils.isEqual(bluetoothGattCharacteristic.getValue(), Utils.addOrCutBackByte)) {
            checkFragment.refreshBtText(armIo,chestIo,stomachIo,legIo,neckIo,backIo,rearIo);
        }
    }

    @Override
    public void getCheckFragment(UserCheckFragment fragment) {
        checkFragment = fragment;
        checkFragment.setOnCheckClickListener(this);
    }

    @Override
    public void getBlueService(MyBlueService service) {
        blueService =service;
        blueService.setCallback(this);
    }

    @Override
    public void onCheckStartClick() {
        if (checkStatus == 0) {
            blueService.writeRXCharacteristic(ByteUtils.getCmdStart(0x03, 0xE1, 0xE4));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // timer cmd
                    myCountDownTimer = new MyCountDownTimer(pauseTime,1000);
                    myCountDownTimer.start();
                }
            }, 1000);
        } else if (checkStatus == 1) {
            if (myCountDownTimer != null){
                myCountDownTimer.cancel();
            }
            blueService.writeRXCharacteristic(ByteUtils.getCmdStart(0x05, 0xE2, 0xE7));
        }
    }

    @Override
    public void onCheckRateAdd() {
       // if (0 <= ioRate && ioRate <= 9){
       //     ioRate++;
       //     blueService.writeRXCharacteristic(ByteUtils.getRateCmd(ioRate,1));
       // }

        if (isArmChecked){
            if (0<=armIo && armIo <= 9){
                armIo ++;
            }
        }

        if (isChestChecked){
            if (0<= chestIo && chestIo <= 9){
                chestIo ++;
            }
        }

        if (isStomachChecked){
            if (0<= stomachIo && stomachIo <= 9){
                stomachIo ++;
            }
        }
        if (isLegChecked){
            if (0<= legIo && legIo <= 9){
                legIo++;
            }
        }
        if (isNeckChecked){
            if (0<= neckIo && neckIo <=9){
                neckIo++;
            }
        }
        if (isBackChecked){
            if (0<=backIo && backIo<= 9){
                backIo++;
            }
        }
        if (isRearChecked){
            if (0<=rearIo && rearIo <=9){
                rearIo ++;
            }
        }
        blueService.writeRXCharacteristic(ByteUtils.getRateCmd(armIo,chestIo,stomachIo,legIo,neckIo,backIo,rearIo,1));
    }

    @Override
    public void onCheckRateCut() {
//        if (1 <= ioRate && ioRate <= 10) {
//            ioRate--;
//            blueService.writeRXCharacteristic(ByteUtils.getRateCmd(ioRate,1));
//        }
        if (isArmChecked){
            if (1<=armIo && armIo <= 10){
                armIo --;
            }
        }

        if (isChestChecked){
            if (1<= chestIo && chestIo <= 10){
                chestIo --;
            }
        }
        if (isStomachChecked){
            if (1<= stomachIo && stomachIo<=10){
                stomachIo --;
            }
        }
        if (isLegChecked){
            if (1<= legIo && legIo <= 10){
                legIo --;
            }
        }
        if (isNeckChecked){
            if (1<=neckIo && neckIo <=10){
                neckIo --;
            }
        }
        if (isBackChecked){
            if (1<= backIo && backIo <=10){
                backIo --;
            }
        }
        if (isRearChecked){
            if (1<= rearIo && rearIo <=10){
                rearIo --;
            }
        }
        blueService.writeRXCharacteristic(ByteUtils.getRateCmd(armIo,chestIo,stomachIo,legIo,neckIo,backIo,rearIo,1));
    }

    @Override
    public void onCheckLegClick() {
        if (isLegChecked){
            isLegChecked = false;
        }else {
            isLegChecked = true;
        }
        checkFragment.setButtonBackground(4,isLegChecked);
    }

    @Override
    public void onCheckArmClick() {
        if (isArmChecked){
            isArmChecked = false;
        }else {
            isArmChecked = true;
        }
        checkFragment.setButtonBackground(1,isArmChecked);
    }

    @Override
    public void onCheckChestClick() {
        if (isChestChecked){
            isChestChecked = false;
        }else {
            isChestChecked = true;
        }
        checkFragment.setButtonBackground(2,isChestChecked);
    }

    @Override
    public void onCheckStomachClick() {
        if (isStomachChecked){
            isStomachChecked = false;
        }else {
            isStomachChecked = true;
        }
        checkFragment.setButtonBackground(3,isStomachChecked);
    }

    @Override
    public void onCheckNeckClick() {
        if (isNeckChecked){
            isNeckChecked = false;
        }else {
            isNeckChecked = true;
        }
        checkFragment.setButtonBackground(5,isNeckChecked);
    }

    @Override
    public void onCheckBackClick() {
        if (isBackChecked){
            isBackChecked = false;
        }else {
            isBackChecked = true;
        }
        checkFragment.setButtonBackground(6,isBackChecked);
    }

    @Override
    public void onCheckRearClick() {
        if (isRearChecked){
            isRearChecked = false;
        }else {
            isRearChecked = true;
        }
        checkFragment.setButtonBackground(7,isRearChecked);
    }

    //每秒钟发送一次心跳包
    class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (blueService != null) {
                blueService.writeRXCharacteristic(ByteUtils.getCmdStart(0x07, 0xE3, 0xEA));
                checkFragment.timeText().setText(Utils.formatTime(millisUntilFinished));
                pauseTime = millisUntilFinished;
            }
        }

        @Override
        public void onFinish() {
            myCountDownTimer.cancel();
            blueService.writeRXCharacteristic(ByteUtils.getCmdStart(0x05, 0xE2, 0xE7));
            pauseTime = 2*60*1000;
            checkFragment.timeText().setText("00:00");
            checkFragment.startButton().setText("start");
            //TODO bt start status change
        }
    }
}
