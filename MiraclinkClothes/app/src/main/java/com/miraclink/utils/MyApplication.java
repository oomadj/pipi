package com.miraclink.utils;

import android.app.Application;

import com.vise.baseble.ViseBle;

public class MyApplication extends Application {
    private static MyApplication application;

    public static MyApplication getInstance(){
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

//    private void init(){
//        ViseBle.config()
//                .setScanTimeout(10000)
//                .setConnectTimeout(10 * 1000)//连接超时时间
//                .setOperateTimeout(5 * 1000)//设置数据操作超时时间
//                .setConnectRetryCount(3)//设置连接失败重试次数
//                .setConnectRetryInterval(1000)//设置连接失败重试间隔时间
//                .setOperateRetryCount(3)//设置数据操作失败重试次数
//                .setOperateRetryInterval(1000)//设置数据操作失败重试间隔时间
//                .setMaxConnectCount(3);//设置最大连接设备数量
//        ViseBle.getInstance().init(this);
//    }


}
