package com.blanke.viewscalecustom;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by blanke on 16-6-11.
 */
public class MyLayout extends FrameLayout {
    private final static String TAG = "MYLayout";
    private ViewDragHelper mDragger;
    private Point mLastPoint, mNextPoint;
    private int mState = -1;
    private int mMinDivWidth = 0;
    float baseValue;
    float mCurrentScale = 1;
    float last_x = -1;
    float last_y = -1;
    private ViewDragHelper.Callback mDragCallBack;

    public MyLayout(Context context) {
        this(context, null);
    }

    public MyLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViewDragHelper();
    }

    private void initViewDragHelper() {
        mLastPoint = new Point();
        mNextPoint = new Point();
        mDragCallBack = new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth() - child.getMeasuredWidth();
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return getMeasuredHeight() - child.getMeasuredHeight();
            }

            //手指释放的时候回调
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                Log.d(TAG, "onViewReleased  " + String.valueOf(xvel) + "," + String.valueOf(yvel));
                int mX = mNextPoint.x, mY = mNextPoint.y;
                boolean isMove = isConvertView(releasedChild);//是否存在覆盖view的情况
                if (!isMove) {
                    //水平超界检测
                    if (mNextPoint.x <= 0 || mNextPoint.x + releasedChild.getWidth() >= getWidth()) {
                        mX = Math.min(getWidth() - releasedChild.getWidth(), Math.max(0, mNextPoint.x));
                        isMove = true;
                    }
                    if (mNextPoint.y <= 0 || mNextPoint.y + releasedChild.getHeight() >= getHeight()) {
                        mY = Math.min(getHeight() - releasedChild.getHeight(), Math.max(0, mNextPoint.y));
                        isMove = true;
                    }
                } else {
                    mX = mLastPoint.x;
                    mY = mLastPoint.y;
                }
                if (isMove) {
                    Log.d(TAG, "onViewReleased  回到原来位置 ，当前位置 " + mNextPoint.toString() + " 目标位置：" + mLastPoint.toString());
                    mDragger.settleCapturedViewAt(mX, mY);
                    invalidate();
                }
            }


            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
//                Log.d(TAG, "onViewPositionChanged  " + String.valueOf(left) + "," + String.valueOf(top) + "," + String.valueOf(dx) + "," + String.valueOf(dy));
                if (mLastPoint == null && mState == 1) {
                    mLastPoint = new Point();
                    mLastPoint.set(left - dx, top - dy);
                }
                mNextPoint.set(left, top);
            }

            @Override
            public void onViewDragStateChanged(int state) {
                Log.d(TAG, "onViewDragStateChanged  " + state);
                mState = state;
                if (state == 1) {
                    mLastPoint = null;
                }
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
//                final int leftBound = getPaddingLeft();
//                final int rightBound = getWidth() - child.getWidth() - leftBound;
//
//                final int newLeft = Math.min(Math.max(left, leftBound), rightBound);

                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }
        };
        mDragger = ViewDragHelper.create(this, 1.0f, mDragCallBack);
    }

    private void addMinDivWidth(Rect rect) {
        rect.left -= mMinDivWidth;
        rect.top -= mMinDivWidth;
        rect.right += mMinDivWidth;
        rect.bottom += mMinDivWidth;
    }

    private boolean isConvertView(View view) {
        Rect sourceRect = new Rect();
        Rect tempRect = new Rect();
        view.getGlobalVisibleRect(sourceRect);
        addMinDivWidth(sourceRect);
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v != view) {
                v.getGlobalVisibleRect(tempRect);
                addMinDivWidth(tempRect);
                if (Rect.intersects(tempRect, sourceRect)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragger.shouldInterceptTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (mDragger.continueSettling(true)) {
            invalidate();
        }
    }

    private void chageViewSize(View v, int scale) {
        v.layout(v.getLeft() - scale, v.getTop() - scale, v.getRight() + scale, v.getBottom() + scale);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            baseValue = 0;
            last_x = event.getRawX();
            last_y = event.getRawY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (event.getPointerCount() == 2) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
                if (baseValue == 0) {
                    baseValue = value;
                } else {
                    if (value - baseValue >= 30 || value - baseValue <= -30) {
                        float scale = value - baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
                        Log.d("onTouchEvent", scale + "");
                        int d = (int) (scale / 200);
                        View view = getChildAt(0);
                        mLastPoint = new Point();
                        mLastPoint.set(view.getLeft(), view.getTop());
                        mNextPoint.set(view.getLeft() - d, view.getTop() - d);
                        chageViewSize(view, d);
                        mDragCallBack.onViewReleased(view, 0, 0);
                    }
                }
            } else if (event.getPointerCount() == 1) {
                float x = event.getRawX();
                float y = event.getRawY();
                x -= last_x;
                y -= last_y;
                if (x >= 10 || y >= 10 || x <= -10 || y <= -10)
//                    img_transport(x, y); //移动图片位置
                    last_x = event.getRawX();
                last_y = event.getRawY();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            baseValue = 0;
        }
        mDragger.processTouchEvent(event);
        return true;
    }
}
