package com.miraclink.content.check;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.CountDownTimer;
import android.os.Handler;

import com.miraclink.base.BaseCallback;
import com.miraclink.bluetooth.MyBlueService;
import com.miraclink.database.IUserDatabaseManager;
import com.miraclink.model.CheckHistory;
import com.miraclink.utils.ByteUtils;
import com.miraclink.utils.LogUtil;
import com.miraclink.utils.SharePreUtils;
import com.miraclink.utils.Utils;

public class UserCheckPresenter implements UserCheckContract.Presenter, BaseCallback {
    private static final String TAG = UserCheckPresenter.class.getSimpleName();
    public static int checkStatus = 0;   // 0 stop // 1 starting
    MyBlueService blueService;
    private MyCountDownTimer myCountDownTimer;
    private UserCheckContract.IView iView;
    private IUserDatabaseManager iUserDatabaseManager;

    private int time;
    private int rate;
    private long pauseTime; //= 10 * 60 * 1000;    //init time

    private boolean isLegChecked;
    private boolean isArmChecked;
    private boolean isChestChecked;
    private boolean isStomachChecked;
    private boolean isNeckChecked;
    private boolean isBackChecked;
    private boolean isRearChecked;
    private int armIo;  //TODO get io to user info
    private int chestIo;
    private int stomachIo;
    private int legIo;
    private int neckIo;
    private int backIo;
    private int rearIo;

    private String lastAddress;
    private int replayCount = 100;
    private boolean isReplay = false;

    public UserCheckPresenter(UserCheckContract.IView iView, IUserDatabaseManager iUserDatabaseManager) {
        this.iView = iView;
        this.iUserDatabaseManager = iUserDatabaseManager;
    }

    @Override
    public void getBlueService(MyBlueService service) {
        blueService = service;
        blueService.setCallback(this);
    }

    @Override
    public void getBleAddress(String string) {
        LogUtil.i(TAG, "last address:" + string);
        lastAddress = string;
    }

    @Override
    public void onDeviceChange(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        String value = ByteUtils.toHexString(bluetoothGattCharacteristic.getValue());
        LogUtil.i(TAG, "xzx--on device change -values:" + value);
        if (ByteUtils.isEqual(bluetoothGattCharacteristic.getValue(), Utils.startBackByte)) {
            checkStatus = 1;
            LogUtil.i(TAG, "checkStatus == 1");
            iView.refreshStartButtonText(checkStatus);
        } else if (ByteUtils.isEqual(bluetoothGattCharacteristic.getValue(), Utils.stopBackByte)) {
            checkStatus = 0;
            LogUtil.i(TAG, "checkStatus == 0");
            iView.refreshStartButtonText(checkStatus);
        } else if (ByteUtils.isEqual(bluetoothGattCharacteristic.getValue(), Utils.addOrCutBackByte)) {
            iView.refreshCheckButtonText(armIo, chestIo, stomachIo, legIo, neckIo, backIo, rearIo);
        }
    }

    @Override
    public void onDestroy() {
        if (myCountDownTimer != null) {
            myCountDownTimer.cancel();
        }
    }

    @Override
    public void getUserInfo(String id) {
        getUserInfoToDatabase(id);
    }

    @Override
    public void onCheckStartClick() {
        if (checkStatus == 0) {
            blueService.writeRXCharacteristic(ByteUtils.getCmdStart(0x03, 0xE1, 0xE4));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.i(TAG,"xzx user:pause time:"+pauseTime);
                    // timer cmd
                    myCountDownTimer = new MyCountDownTimer(pauseTime, 990);
                    myCountDownTimer.start();
                }
            }, 500);
        } else if (checkStatus == 1) {
            if (myCountDownTimer != null) {
                myCountDownTimer.cancel();
            }
            blueService.writeRXCharacteristic(ByteUtils.getCmdStart(0x05, 0xE2, 0xE7));
        }
    }

    @Override
    public void onCheckRateAdd() {
        checkAdd();
    }

    @Override
    public void onCheckRateCut() {
        checkCut();
    }

    @Override
    public void onCheckLegClick() {
        if (isLegChecked) {
            isLegChecked = false;
        } else {
            isLegChecked = true;
        }
        iView.setButtonBackground(4, isLegChecked);
    }

    @Override
    public void onCheckLegZeroClick() {
        if (isLegChecked) {
            isLegChecked = false;
        } else {
            isLegChecked = true;
        }
        iView.setButtonBackground(4, isLegChecked);
    }

    @Override
    public void onCheckArmClick() {
        if (isArmChecked) {
            isArmChecked = false;
        } else {
            isArmChecked = true;
        }
        iView.setButtonBackground(1, isArmChecked);
    }

    @Override
    public void onCheckArmZeroClick() {
        if (isArmChecked) {
            isArmChecked = false;
        } else {
            isArmChecked = true;
        }
        iView.setButtonBackground(1, isArmChecked);
    }

    @Override
    public void onCheckChestClick() {
        if (isChestChecked) {
            isChestChecked = false;
        } else {
            isChestChecked = true;
        }
        iView.setButtonBackground(2, isChestChecked);
    }

    @Override
    public void onCheckStomachClick() {
        if (isStomachChecked) {
            isStomachChecked = false;
        } else {
            isStomachChecked = true;
        }
        iView.setButtonBackground(3, isStomachChecked);
    }

    @Override
    public void onCheckNeckClick() {
        if (isNeckChecked) {
            isNeckChecked = false;
        } else {
            isNeckChecked = true;
        }
        iView.setButtonBackground(5, isNeckChecked);
    }

    @Override
    public void onCheckBackClick() {
        if (isBackChecked) {
            isBackChecked = false;
        } else {
            isBackChecked = true;
        }
        iView.setButtonBackground(6, isBackChecked);
    }

    @Override
    public void onCheckRearClick() {
        if (isRearChecked) {
            isRearChecked = false;
        } else {
            isRearChecked = true;
        }
        iView.setButtonBackground(7, isRearChecked);
    }

    @Override
    public void onUserChanged() {
        isArmChecked = true;
        isChestChecked = true;
        isStomachChecked = true;
        isLegChecked = true;
        isNeckChecked = true;
        isBackChecked = true;
        isRearChecked = true;
        iView.refreshCheckButtonText(armIo, chestIo, stomachIo, legIo, neckIo, backIo, rearIo);
        for (int i = 1; i <= 7; i++) {
            iView.setButtonBackground(i, true);
        }
    }

    //TODO rePlay Connected ---
    @Override
    public void onDisconnected() {
        myCountDownTimer.cancel();
        checkStatus = 0;
        iView.refreshStartButtonText(checkStatus);
        blueService.close();
        LogUtil.i(TAG, "on disconnected:" + lastAddress);
        if (lastAddress != null && blueService != null) {
            LogUtil.i(TAG, "is replay connect:" + replayCount);
            if (replayCount > 0) {
                replayCount--;
                //boolean isConnected = blueService.connect(lastAddress);
                //if (isConnected) {
                //    isReplay = true;
                //}
            }
        }
    }

    private void checkAdd() {
        if (isArmChecked) {
            if (0 <= armIo && armIo <= 9) {
                armIo++;
            }
        }
        if (isChestChecked) {
            if (0 <= chestIo && chestIo <= 9) {
                chestIo++;
            }
        }
        if (isStomachChecked) {
            if (0 <= stomachIo && stomachIo <= 9) {
                stomachIo++;
            }
        }
        if (isLegChecked) {
            if (0 <= legIo && legIo <= 9) {
                legIo++;
            }
        }
        if (isNeckChecked) {
            if (0 <= neckIo && neckIo <= 9) {
                neckIo++;
            }
        }
        if (isBackChecked) {
            if (0 <= backIo && backIo <= 9) {
                backIo++;
            }
        }
        if (isRearChecked) {
            if (0 <= rearIo && rearIo <= 9) {
                rearIo++;
            }
        }
        blueService.writeRXCharacteristic(ByteUtils.getRateCmd(armIo, chestIo, stomachIo, legIo, neckIo, backIo, rearIo, intToHex(rate)));
    }

    private void checkCut() {
        if (isArmChecked) {
            if (1 <= armIo && armIo <= 10) {
                armIo--;
            }
        }
        if (isChestChecked) {
            if (1 <= chestIo && chestIo <= 10) {
                chestIo--;
            }
        }
        if (isStomachChecked) {
            if (1 <= stomachIo && stomachIo <= 10) {
                stomachIo--;
            }
        }
        if (isLegChecked) {
            if (1 <= legIo && legIo <= 10) {
                legIo--;
            }
        }
        if (isNeckChecked) {
            if (1 <= neckIo && neckIo <= 10) {
                neckIo--;
            }
        }
        if (isBackChecked) {
            if (1 <= backIo && backIo <= 10) {
                backIo--;
            }
        }
        if (isRearChecked) {
            if (1 <= rearIo && rearIo <= 10) {
                rearIo--;
            }
        }
        blueService.writeRXCharacteristic(ByteUtils.getRateCmd(armIo, chestIo, stomachIo, legIo, neckIo, backIo, rearIo, intToHex(rate)));
    }

    private void getUserInfoToDatabase(String id) {
        iUserDatabaseManager.queryUserByID(user -> {
            iView.setInfoText(user);
        }, id);
    }

    //test --
    @Override
    public void queryAllUser(IUserDatabaseManager iUserDatabaseManager, IUserDatabaseManager.QueryAllUserCallback callback) {
        iUserDatabaseManager.queryAllUser(callback);
    }

    @Override
    public void onInit(int time, int rate, int strong) {
        this.time = time;
        this.rate = rate;
        pauseTime = time * 60 * 1000;
        LogUtil.i(TAG, "presenter user:" + strong + "time --:" + time + "rate:" + rate);

        armIo = strong / 10;
        chestIo = strong / 10;
        stomachIo = strong / 10;
        legIo = strong / 10;
        neckIo = strong / 10;
        backIo = strong / 10;
        rearIo = strong / 10;
    }

    @Override
    public void onInsertCheckHistory(IUserDatabaseManager iUserDatabaseManager,CheckHistory history) {
        iUserDatabaseManager.insertCheckHistory(history);
    }

    public int intToHex(int i) {
        int hex = 0;
        switch (i) {
            case 1:
                hex = 0x01;
                break;
            case 2:
                hex = 0x02;
                break;
            case 3:
                hex = 0x03;
                break;
            case 4:
                hex = 0x04;
                break;
            case 5:
                hex = 0x05;
                break;
            case 6:
                hex = 0x06;
                break;
            case 7:
                hex = 0x07;
                break;
            case 8:
                hex = 0x08;
                break;
            case 9:
                hex = 0x09;
                break;
            case 10:
                hex = 0x0a;
                break;
            default:
                break;

        }
        return hex;
    }

    //每秒钟发送一次心跳包
    class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (blueService != null) {
                LogUtil.i(TAG, "handler -+++++-write rx");
                blueService.writeRXCharacteristic(ByteUtils.getCmdStart(0x07, 0xE3, 0xEA));
                pauseTime = millisUntilFinished;
                iView.setTimeText(Utils.formatTime(millisUntilFinished));
            }
        }

        @Override
        public void onFinish() {
            myCountDownTimer.cancel();
            blueService.writeRXCharacteristic(ByteUtils.getCmdStart(0x05, 0xE2, 0xE7));
            pauseTime = time * 60 * 1000;
            iView.setTimeText("00:00");
            iView.setStartText("start");
            //TODO bt start status change

        }
    }
}
