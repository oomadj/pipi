package com.miraclink.dumbbell.content;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.miraclink.R;
import com.miraclink.base.BaseActivity;
import com.miraclink.dumbbell.content.course.CourseFragment;
import com.miraclink.dumbbell.content.homepage.HomeFragment;
import com.miraclink.dumbbell.content.mine.MineFragment;

public class BellActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivHome, ivCourse, ivMine;
    private TextView tvHome, tvCourse, tvMine;
    private LinearLayout layoutHome, layoutCourse, layoutMine;
    private HomeFragment homeFragment;
    private CourseFragment courseFragment;
    private MineFragment mineFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bell_activity_main);
        initParam();
        intiView();
        setTabSelect(0);
    }

    @Override
    protected void initParam() {
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void intiView() {
        ivHome = findViewById(R.id.imgBellActivityHome);
        ivCourse = findViewById(R.id.imgBellActivityCourse);
        ivMine = findViewById(R.id.imgBellActivityMine);
        tvHome = findViewById(R.id.textBellActivityHome);
        tvCourse = findViewById(R.id.textBellActivityCourse);
        tvMine = findViewById(R.id.textBellActivityMine);
        layoutHome = findViewById(R.id.layoutHome);
        layoutCourse = findViewById(R.id.layoutCourse);
        layoutMine = findViewById(R.id.layoutMine);
        layoutHome.setOnClickListener(this);
        layoutMine.setOnClickListener(this);
        layoutCourse.setOnClickListener(this);
    }

    private void setTabSelect(int i) {
        transaction = fragmentManager.beginTransaction();
        hideFragments();
        resetImageViewAndTextView();
        switch (i) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.layoutBellActivity, homeFragment);
                }
                transaction.show(homeFragment);
                ivHome.setImageResource(R.drawable.homepager_click);
                tvHome.setTextColor(getResources().getColor(R.color.check_text_blue));
                break;
            case 1:
                if (courseFragment == null) {
                    courseFragment = new CourseFragment();
                    transaction.add(R.id.layoutBellActivity, courseFragment);
                }
                transaction.show(courseFragment);
                ivCourse.setImageResource(R.drawable.course_click);
                tvCourse.setTextColor(getResources().getColor(R.color.check_text_blue));

                break;
            case 2:
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    transaction.add(R.id.layoutBellActivity, mineFragment);
                }
                transaction.show(mineFragment);
                ivMine.setImageResource(R.drawable.mine_click);
                tvMine.setTextColor(getResources().getColor(R.color.check_text_blue));

                break;
        }
        transaction.commit();
    }


    private void resetImageViewAndTextView() {
        ivHome.setImageResource(R.drawable.homepager_unclick);
        ivCourse.setImageResource(R.drawable.course_unclick);
        ivMine.setImageResource(R.drawable.mine_unclick);
        tvHome.setTextColor(getResources().getColor(R.color.black));
        tvCourse.setTextColor(getResources().getColor(R.color.black));
        tvMine.setTextColor(getResources().getColor(R.color.black));
    }

    private void hideFragments() {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (courseFragment != null) {
            transaction.hide(courseFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutHome:
                setTabSelect(0);
                break;
            case R.id.layoutCourse:
                setTabSelect(1);
                break;
            case R.id.layoutMine:
                setTabSelect(2);
                break;
            default:
                break;
        }
    }
}
