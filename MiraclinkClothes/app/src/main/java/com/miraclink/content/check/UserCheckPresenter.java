package com.miraclink.content.check;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UserCheckPresenter implements UserCheckContract.Presenter, BaseCallback {
    private static final String TAG = UserCheckPresenter.class.getSimpleName();
    MyBlueService blueService;
    private MyCountDownTimer myCountDownTimer;
    private UserCheckContract.IView iView;
    private IUserDatabaseManager iUserDatabaseManager;
    private Context context;

    private int time;
    private int rate;
    private long pauseTime; //= 10 * 60 * 1000;    //init time

    private boolean isSelectAll = false;
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

    public Map<String, Integer> statusSave = new HashMap<>(); // 0 stop // 1 starting
    private Map<String, Long> timeSave = new HashMap<>();
    private Map<String, Integer> armIoSave = new HashMap<>();
    private Map<String, Integer> chestIoSave = new HashMap<>();
    private Map<String, Integer> stomachIoSave = new HashMap<>();
    private Map<String, Integer> neckIoSave = new HashMap<>();
    private Map<String, Integer> legIoSave = new HashMap<>();
    private Map<String, Integer> backIoSave = new HashMap<>();
    private Map<String, Integer> rearIoSave = new HashMap<>();

    private String lastAddress;
    private int replayCount = 100;

    public UserCheckPresenter(UserCheckContract.IView iView, IUserDatabaseManager iUserDatabaseManager, Context context) {
        this.iView = iView;
        this.iUserDatabaseManager = iUserDatabaseManager;
        this.context = context;
    }

    @Override
    public void getBlueService(MyBlueService service) {
        blueService = service;
        blueService.setCallback(this);
    }

    @Override
    public void getBleAddress(String string) {
        lastAddress = string;
    }

    @Override
    public void onDeviceChange(BluetoothGatt gatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        String value = ByteUtils.toHexString(bluetoothGattCharacteristic.getValue());
        //LogUtil.i(TAG, "xzx-onDeviceChange-on device change -values:" + value);
        if (gatt == blueService.idAndGatt.get(SharePreUtils.getCheckID(context))) {
            LogUtil.i(TAG, "xzx-onDeviceChange-true gatt ------------");

            if (ByteUtils.isEqual(bluetoothGattCharacteristic.getValue(), Utils.startBackByte)) {
                statusSave.put(SharePreUtils.getCheckID(context), 1);
                iView.refreshStartButtonText(1);
            } else if (ByteUtils.isEqual(bluetoothGattCharacteristic.getValue(), Utils.stopBackByte)) {
                statusSave.put(SharePreUtils.getCheckID(context), 0);
                iView.refreshStartButtonText(0);
            } else if (ByteUtils.isEqual(bluetoothGattCharacteristic.getValue(), Utils.addOrCutBackByte)) {
                iView.refreshCheckButtonText(armIo, chestIo, stomachIo, legIo, neckIo, backIo, rearIo);
            }
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
        if (!statusSave.containsKey(SharePreUtils.getCheckID(context))) {
            statusSave.put(SharePreUtils.getCheckID(context), 0);
        }
        if (0 == statusSave.get(SharePreUtils.getCheckID(context))) {
            blueService.writeRXCharacteristic(ByteUtils.getCmdStart(0x03, 0xE1, 0xE4), SharePreUtils.getCheckID(context));
            if (!hasChecking()) {
                // max time
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.i(TAG, "xzx user:pause time:" + pauseTime);
                        if (myCountDownTimer != null) {
                            myCountDownTimer.cancel();
                        }
                        // timer cmd
                        myCountDownTimer = new MyCountDownTimer(timeSave.get(SharePreUtils.getCheckID(context)), 1000);
                        myCountDownTimer.start();
                    }
                }, 200);
            }
        } else if (1 == statusSave.get(SharePreUtils.getCheckID(context))) {
            //  if (!hasChecking()) {
            if (myCountDownTimer != null) {
                myCountDownTimer.cancel();
            }
            // }
            blueService.writeRXCharacteristic(ByteUtils.getCmdStart(0x05, 0xE2, 0xE7), SharePreUtils.getCheckID(context));
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
    public void onCheckAll() {
        if (isSelectAll) {
            isSelectAll = false;
            isArmChecked = false;
            isChestChecked = false;
            isStomachChecked = false;
            isLegChecked = false;
            isNeckChecked = false;
            isBackChecked = false;
            isRearChecked = false;
            iView.setButtonBackground(1, false);
            iView.setButtonBackground(2, false);
            iView.setButtonBackground(3, false);
            iView.setButtonBackground(4, false);
            iView.setButtonBackground(5, false);
            iView.setButtonBackground(6, false);
            iView.setButtonBackground(7, false);
            iView.setButtonBackground(8, false);
        } else {
            isSelectAll = true;
            isArmChecked = true;
            isChestChecked = true;
            isStomachChecked = true;
            isLegChecked = true;
            isNeckChecked = true;
            isBackChecked = true;
            isRearChecked = true;
            iView.setButtonBackground(1, true);
            iView.setButtonBackground(2, true);
            iView.setButtonBackground(3, true);
            iView.setButtonBackground(4, true);
            iView.setButtonBackground(5, true);
            iView.setButtonBackground(6, true);
            iView.setButtonBackground(7, true);
            iView.setButtonBackground(8, true);
        }
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
        armIo = armIoSave.get(SharePreUtils.getCheckID(context));
        chestIo = chestIoSave.get(SharePreUtils.getCheckID(context));
        stomachIo = stomachIoSave.get(SharePreUtils.getCheckID(context));
        legIo = legIoSave.get(SharePreUtils.getCheckID(context));
        neckIo = neckIoSave.get(SharePreUtils.getCheckID(context));
        backIo = backIoSave.get(SharePreUtils.getCheckID(context));
        rearIo = rearIoSave.get(SharePreUtils.getCheckID(context));
        pauseTime = timeSave.get(SharePreUtils.getCheckID(context));

        if (!statusSave.containsKey(SharePreUtils.getCheckID(context))) {
            statusSave.put(SharePreUtils.getCheckID(context), 0);
        }
        iView.refreshStartButtonText(statusSave.get(SharePreUtils.getCheckID(context)));
        iView.refreshCheckButtonText(armIo, chestIo, stomachIo, legIo, neckIo, backIo, rearIo);
        iView.setTimeText(Utils.formatTime(pauseTime));
    }

    @Override
    public void onDisconnected() {
        myCountDownTimer.cancel();
        statusSave.put(SharePreUtils.getCheckID(context), 0);
        iView.refreshStartButtonText(statusSave.get(SharePreUtils.getCheckID(context)));
        blueService.close();

        //TODO rePlay Connected ---
        LogUtil.i(TAG, "on disconnected:" + lastAddress);
        if (lastAddress != null && blueService != null) {
            LogUtil.i(TAG, "is replay connect:" + replayCount);
            if (replayCount > 0) {
                replayCount--;
            }
        }
    }

    private void checkAdd() {
        if (isArmChecked) {
            if (0 <= armIo && armIo <= 9) {
                armIo++;
                armIoSave.put(SharePreUtils.getCheckID(context), armIo);
            }
        }
        if (isChestChecked) {
            if (0 <= chestIo && chestIo <= 9) {
                chestIo++;
                chestIoSave.put(SharePreUtils.getCheckID(context), chestIo);
            }
        }
        if (isStomachChecked) {
            if (0 <= stomachIo && stomachIo <= 9) {
                stomachIo++;
                stomachIoSave.put(SharePreUtils.getCheckID(context), stomachIo);
            }
        }
        if (isLegChecked) {
            if (0 <= legIo && legIo <= 9) {
                legIo++;
                legIoSave.put(SharePreUtils.getCheckID(context), legIo);
            }
        }
        if (isNeckChecked) {
            if (0 <= neckIo && neckIo <= 9) {
                neckIo++;
                neckIoSave.put(SharePreUtils.getCheckID(context), neckIo);
            }
        }
        if (isBackChecked) {
            if (0 <= backIo && backIo <= 9) {
                backIo++;
                backIoSave.put(SharePreUtils.getCheckID(context), backIo);
            }
        }
        if (isRearChecked) {
            if (0 <= rearIo && rearIo <= 9) {
                rearIo++;
                rearIoSave.put(SharePreUtils.getCheckID(context), rearIo);
            }
        }
        blueService.writeRXCharacteristic(ByteUtils.getRateCmd(armIo, chestIo, stomachIo, legIo, neckIo, backIo, rearIo, intToHex(rate)), SharePreUtils.getCheckID(context));
    }

    private void checkCut() {
        if (isArmChecked) {
            if (1 <= armIo && armIo <= 10) {
                armIo--;
                armIoSave.put(SharePreUtils.getCheckID(context), armIo);
            }
        }
        if (isChestChecked) {
            if (1 <= chestIo && chestIo <= 10) {
                chestIo--;
                chestIoSave.put(SharePreUtils.getCheckID(context), chestIo);
            }
        }
        if (isStomachChecked) {
            if (1 <= stomachIo && stomachIo <= 10) {
                stomachIo--;
                stomachIoSave.put(SharePreUtils.getCheckID(context), stomachIo);
            }
        }
        if (isLegChecked) {
            if (1 <= legIo && legIo <= 10) {
                legIo--;
                legIoSave.put(SharePreUtils.getCheckID(context), legIo);
            }
        }
        if (isNeckChecked) {
            if (1 <= neckIo && neckIo <= 10) {
                neckIo--;
                neckIoSave.put(SharePreUtils.getCheckID(context), neckIo);
            }
        }
        if (isBackChecked) {
            if (1 <= backIo && backIo <= 10) {
                backIo--;
                backIoSave.put(SharePreUtils.getCheckID(context), backIo);
            }
        }
        if (isRearChecked) {
            if (1 <= rearIo && rearIo <= 10) {
                rearIo--;
                rearIoSave.put(SharePreUtils.getCheckID(context), rearIo);
            }
        }
        blueService.writeRXCharacteristic(ByteUtils.getRateCmd(armIo, chestIo, stomachIo, legIo, neckIo, backIo, rearIo, intToHex(rate)), SharePreUtils.getCheckID(context));
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
    public void queryCheckUserList(IUserDatabaseManager iUserDatabaseManager, IUserDatabaseManager.QueryUserByCheckIDsCallback callback, String[] ids) {
        iUserDatabaseManager.queryCheckUserList(callback, ids);
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

        if (!timeSave.containsKey(SharePreUtils.getCheckID(context))){
            timeSave.put(SharePreUtils.getCheckID(context), pauseTime);
            armIoSave.put(SharePreUtils.getCheckID(context), armIo);
            chestIoSave.put(SharePreUtils.getCheckID(context), chestIo);
            stomachIoSave.put(SharePreUtils.getCheckID(context), stomachIo);
            legIoSave.put(SharePreUtils.getCheckID(context), legIo);
            neckIoSave.put(SharePreUtils.getCheckID(context), neckIo);
            backIoSave.put(SharePreUtils.getCheckID(context), backIo);
            rearIoSave.put(SharePreUtils.getCheckID(context), rearIo);
            iView.refreshCheckButtonText(armIo, chestIo, stomachIo, legIo, neckIo, backIo, rearIo); //refresh
        }
    }

    @Override
    public void onInsertCheckHistory(IUserDatabaseManager iUserDatabaseManager, CheckHistory history) {
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
                //blueService.writeRXCharacteristic(ByteUtils.getCmdStart(0x07, 0xE3, 0xEA), SharePreUtils.getCheckID(context));
                pauseTime = millisUntilFinished;
                //iView.setTimeText(Utils.formatTime(millisUntilFinished));

                Iterator iter = statusSave.keySet().iterator();
                while (iter.hasNext()) {
                    String s = (String) iter.next();
                    if (statusSave.get(s) == 1) {
                        blueService.writeRXCharacteristic(ByteUtils.getCmdStart(0x07, 0xE3, 0xEA), s);
                        timeSave.put(s, timeSave.get(s) - 1000);
                        if (s.equals(SharePreUtils.getCheckID(context))){
                            iView.setTimeText(Utils.formatTime(timeSave.get(s)));
                        }
                    }
                }
            }
        }

        @Override
        public void onFinish() {
            myCountDownTimer.cancel();
            blueService.writeRXCharacteristic(ByteUtils.getCmdStart(0x05, 0xE2, 0xE7), SharePreUtils.getCheckID(context));
            pauseTime = time * 60 * 1000;
            timeSave.put(SharePreUtils.getCheckID(context),pauseTime);
            iView.setTimeText("00:00");
            iView.setStartText("start");
            //TODO bt start status change

        }
    }

    //判断是否还有在检查中的
    private boolean hasChecking() {
//        Iterator iterator = statusSave.keySet().iterator();
//        while (iterator.hasNext()) {
//            if (statusSave.get(iterator.next()) == 1) {
//                return true;
//            }
//        }
//        return false;
        return statusSave.containsValue(1);
    }

    private long getMaxTime() {
        long max = 0;
        Iterator iterator = timeSave.keySet().iterator();
        while (iterator.hasNext()) {
        }
        return max;
    }
}
