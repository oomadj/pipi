package com.miraclink.dumbbell.content;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.miraclink.R;
import com.miraclink.base.BaseActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btFreeLogin, btLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bell_activity_login);
        initParam();
        intiView();
    }

    protected void initParam() {

    }

    protected void intiView() {
        btLogin = findViewById(R.id.btLoginActivityLogin);
        btLogin.setOnClickListener(this);
        btFreeLogin = findViewById(R.id.btLoginActivityFreeLogin);
        btFreeLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btLoginActivityLogin:
                Toast.makeText(this, "use free login", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btLoginActivityFreeLogin:
                Intent intent = new Intent(LoginActivity.this, BellActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
