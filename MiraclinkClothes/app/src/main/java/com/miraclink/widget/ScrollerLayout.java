package com.miraclink.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewConfigurationCompat;

import com.miraclink.utils.LogUtil;

public class ScrollerLayout extends ConstraintLayout {
    private Scroller scroller;
    private int mTouchSlop;
    private View mView;

    //View的宽高
    private int mWidth;
    private int mHeight;
    //子view
    private int mChildWidht;

    //手机按下时的屏幕坐标
    private float mXDown;

    //手机当时所处的屏幕坐标
    private float mXMove;

    //上次触发ACTION_MOVE事件时的屏幕坐标
    private float mXLastMove;

    //界面可滚动的左边界
    private int leftBorder;

    //界面可滚动的右边界
    private int rightBorder;

    public int selectPosition = 1; //默认选择的值

    public interface OnSelectPositionClick{
        void onGetSelectPosition(int position);
    };

    private OnSelectPositionClick onSelectPositionClick;

    public void setOnSelectPositionClick(OnSelectPositionClick click){
        onSelectPositionClick = click;
    }

    public ScrollerLayout(Context context) {
        super(context);
    }

    public ScrollerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
    }

    public ScrollerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int weightSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        startW = weightSize;
        endW = heightSize;
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        int weight = Math.min(weightSize, heightSize);
        //LogUtil.i("SL","xzxsl -onmeasure");
        mView = getChildAt(0);
        if (mView != null) {
            ViewGroup.LayoutParams layoutParams = mView.getLayoutParams();

            //设置子view的width=height
            int w = Math.min(layoutParams.width, layoutParams.height);
            int wMeasureSpec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
            int hMeasureSpec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
            measureChild(mView, wMeasureSpec, hMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mView != null && changed) {
            mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
        }

        leftBorder = getChildAt(0).getLeft();
        rightBorder = getChildAt(getChildCount() - 1).getRight();
        //LogUtil.i("SC","left:"+left+"rirht:"+right);
        //xuzhixin add
        layoutLeft = left;
        layoutRight = right;
        //LogUtil.i("SL","xzxsl -on layout");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //初始化与尺寸相关的成员变量
        mWidth = w;
        mHeight = h;
        mChildWidht = mView.getMeasuredWidth();
        LogUtil.i("SL","xzxsl -on size changed");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = ev.getRawX();
                mXLastMove = mXDown;
                LogUtil.i("Sc", "xzx-on intercept-action down" + mXDown);
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = ev.getRawX();
                float diff = Math.abs(mXMove - mXDown);
                mXLastMove = mXMove;
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diff > mTouchSlop) {
                    return true;
                }
                LogUtil.i("Sc", "xzx-on intercept-action move");
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    int downx;
    int upx;

    int startW; //左边界
    int endW; //有边界

    int layoutLeft;
    int layoutRight;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mXMove = event.getRawX();
                int scrolledX = (int) (mXLastMove - mXMove);
//                if (getScrollX() + scrolledX < leftBorder) {
//                    scrollTo(leftBorder, 0);
//                    return true;
//                } else if (getScrollX() + getWidth() + scrolledX > rightBorder) {
//                    scrollTo(rightBorder - getWidth(), 0);
//                    return true;
//                }

                //scrollBy(scrolledX, 0);   //跟随滑动
                mXLastMove = mXMove;

                break;
            case MotionEvent.ACTION_DOWN:
                downx = (int) event.getRawX();
                break;
            case MotionEvent.ACTION_UP:
                upx = (int) event.getRawX();
                //scrollBy(-10,0);
                scollerToTouch(downx-upx);
                LogUtil.i("Sc", "xzx--action up  startW:"+startW+"RightBorder:"+rightBorder+"LeftBorder:"+leftBorder+" : downx:"+downx+":upx:"+upx);
                break;
        }
        return true;
    }


    int i = 6;
    int layoutLong; //控件的长度 = startW - rightBorder
    boolean isFirst = false;
    @Override
    public void computeScroll() {  //layoutLeft = 124 right 988 startW 864          layoutLong/6 = 135   *2/12 = 270
        if (scroller.computeScrollOffset()) {
            LogUtil.i("SL","xzxsl -compute scroll :"+scroller.getCurrX());
            layoutLong = -startW+rightBorder;
            if (scroller.getCurrX()<=(-startW+rightBorder)){
                scrollTo(-startW+rightBorder,0);
                onSelectPositionClick.onGetSelectPosition(10);
            }else if (scroller.getCurrX()>=0){
                scrollTo(0,0);
                onSelectPositionClick.onGetSelectPosition(0);
            }else{
                if (scroller.getCurrX()>=(layoutLong*3/20) && scroller.getCurrX()<(layoutLong/20)){
                    scrollTo(layoutLong/10,0);
                    onSelectPositionClick.onGetSelectPosition(1);
                }else if (scroller.getCurrX()>=(layoutLong*5/20) && scroller.getCurrX()<(layoutLong*3/20)){
                    scrollTo(layoutLong*2/10,0);
                    onSelectPositionClick.onGetSelectPosition(2);
                }else if(scroller.getCurrX()>=(layoutLong*7/20) && scroller.getCurrX()<(layoutLong*5/20)){
                    scrollTo(layoutLong*3/10,0);
                    onSelectPositionClick.onGetSelectPosition(3);
                }else if(scroller.getCurrX()>=(layoutLong*9/20) && scroller.getCurrX()<(layoutLong*7/20)){
                    scrollTo(layoutLong*4/10,0);
                    onSelectPositionClick.onGetSelectPosition(4);
                }else if (scroller.getCurrX()>=(layoutLong*11/20) && scroller.getCurrX()<(layoutLong*9/20)){
                    scrollTo(layoutLong*5/10,0);
                    onSelectPositionClick.onGetSelectPosition(5);
                }else if (scroller.getCurrX()>=(layoutLong*13/20) && scroller.getCurrX()<(layoutLong*11/20)){
                    scrollTo(layoutLong*6/10,0);
                    onSelectPositionClick.onGetSelectPosition(6);
                }else if (scroller.getCurrX()>=(layoutLong*15/20) && scroller.getCurrX()<(layoutLong*13/20)){
                    scrollTo(layoutLong*7/10,0);
                    onSelectPositionClick.onGetSelectPosition(7);
                }else if (scroller.getCurrX()>=(layoutLong*17/20) && scroller.getCurrX()<(layoutLong*15/20)){
                    scrollTo(layoutLong*8/10,0);
                    onSelectPositionClick.onGetSelectPosition(8);
                }else if (scroller.getCurrX()>=(layoutLong*19/20) && scroller.getCurrX()<(layoutLong*17/20)){
                    scrollTo(layoutLong*9/10,0);
                    onSelectPositionClick.onGetSelectPosition(9);
                }
                else if(scroller.getCurrX()>=(layoutLong/20)){
                    scrollTo(0,0);
                    onSelectPositionClick.onGetSelectPosition(0);
                }else if(scroller.getCurrX()<(layoutLong*19/20)){
                    scrollTo(-startW+rightBorder,0);
                    onSelectPositionClick.onGetSelectPosition(10);
                }
            }
            invalidate();
        }

    }

    private void scollerToTouch(int touchX) {
        scroller.startScroll(getScrollX(), getScrollY(), touchX, 0);
        invalidate();
    }

    public int getLayoutLong(){
        return -startW+rightBorder;
    }

    public void setSelectPosition(int i){
        switch (i){
            case 0:
                scrollTo(0,0);
                break;
            case 1:
                scrollTo(layoutLong/6,0);
                break;
            case 2:
                scrollTo(layoutLong*2/6,0);
                break;
            case 3:
                LogUtil.i("SL","xzxsl case 3");
                //scroller.startScroll(getScrollX(),getScrollY(),layoutLong/6,0);
                scollerToTouch(layoutLong/6);
                isFirst = true;
                invalidate();
                break;
            case 4:
                scrollTo(layoutLong*4/6,0);
                break;
            case 5:
                scrollTo(layoutLong*5/6,0);
                break;
            case 6:
                scrollTo(-startW+rightBorder,0);
                break;
            default:
                break;
        }
        invalidate();
    }

}
