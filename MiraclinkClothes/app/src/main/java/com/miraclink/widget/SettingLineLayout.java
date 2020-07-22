package com.miraclink.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.miraclink.R;

public class SettingLineLayout extends ConstraintLayout {
    private TextView textFun;
    private TextView textInfo;
    private ImageView imgMore;
    private View topLine, bottomLine;

    public SettingLineLayout(Context context) {
        super(context);
    }

    public SettingLineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.custom_setting_line_layout, this);
        init();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingLineLayout);
        textFun.setText(typedArray.getString(R.styleable.SettingLineLayout_funText));
        boolean topLineIsVisible = typedArray.getBoolean(R.styleable.SettingLineLayout_topLineView, true);
        if (!topLineIsVisible) {
            topLine.setVisibility(View.GONE);
        }

        if (typedArray.getInt(R.styleable.SettingLineLayout_imageMoreSetting, 0) == 1) {
            imgMore.setImageResource(R.drawable.ic_store_line_more);
        }
    }

    private void init() {
        textFun = findViewById(R.id.textSettingLineLayoutFunction);
        textInfo = findViewById(R.id.textSettingLineLayoutInfo);
        imgMore = findViewById(R.id.imgSettingLineLayoutMore);
        topLine = findViewById(R.id.viewSettingLineLayoutTopLine);
        bottomLine = findViewById(R.id.viewSettingLineLayoutBottomLine);
    }

    public ImageView getImgMore(){
        return imgMore;
    }

    public TextView getTextInfo(){
        return textInfo;
    }
}
