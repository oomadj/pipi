package com.miraclink.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Scroller;

import androidx.annotation.Nullable;

public class ScrollerImageView extends androidx.appcompat.widget.AppCompatImageView {
    private Scroller scroller;
    private int mTouchSlop;

    public ScrollerImageView(Context context) {
        super(context);
    }

    public ScrollerImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
