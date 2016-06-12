package com.blanke.viewscalecustom;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by blanke on 16-6-11.
 */
public class MyView extends View {
    float baseValue;
    float mCurrentScale = 1;
    float last_x = -1;
    float last_y = -1;

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("Myview","ontouchevent");
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
                    if (value - baseValue >= 10 || value - baseValue <= -10) {
                        float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
                        //缩放
                        Log.d("Myview", scale+"");
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
        }
        return true;
    }

}
