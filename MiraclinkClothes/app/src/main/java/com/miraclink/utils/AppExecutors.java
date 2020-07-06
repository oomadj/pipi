package com.miraclink.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private final Executor mDiskIO;
    private final Executor mNetworkIO;
    private final Executor mMainThread;
    private static AppExecutors INSTANCE;

    public AppExecutors(Executor mDiskIO, Executor mNetworkIO, Executor mMainThread) {
        this.mDiskIO = mDiskIO;
        this.mNetworkIO = mNetworkIO;
        this.mMainThread = mMainThread;
    }

    public static AppExecutors getInstance(){
        if (INSTANCE == null){
            synchronized (AppExecutors.class){
                if (INSTANCE == null){
                    INSTANCE = new AppExecutors();
                }
            }
        }
        return INSTANCE;
    }

    public Executor diskIO() {
        return mDiskIO;
    }

    public Executor networkIO() {
        return mNetworkIO;
    }

    public Executor mainThread() {
        return mMainThread;
    }

    public AppExecutors(){
        this(Executors.newCachedThreadPool(),Executors.newFixedThreadPool(3),new MainThreadExecutor());
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable runnable) {
            mainThreadHandler.post(runnable);
        }
    }

}
