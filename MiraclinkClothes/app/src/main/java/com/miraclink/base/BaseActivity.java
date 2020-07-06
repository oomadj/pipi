package com.miraclink.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.miraclink.networks.NetworkController;

public abstract class BaseActivity extends AppCompatActivity {
    protected NetworkController networkController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkController = NetworkController.getInstance();
    }

    protected abstract void initParam();

    protected abstract void intiView();
}
