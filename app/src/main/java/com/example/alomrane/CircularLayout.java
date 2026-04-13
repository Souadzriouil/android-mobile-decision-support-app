package com.example.alomrane;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CircularLayout extends ViewGroup {
    private static final float PI = (float) Math.PI;
    private float angleStep;

    public CircularLayout(Context context) {
        super(context);
    }

    public CircularLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(size, size);
        angleStep = 2 * PI / getChildCount();

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(size / 2, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(size / 2, MeasureSpec.AT_MOST));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int radius = getWidth() / 2;

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            double angle = angleStep * i;
            int childLeft = (int) (radius + radius * Math.cos(angle) - child.getMeasuredWidth() / 2);
            int childTop = (int) (radius + radius * Math.sin(angle) - child.getMeasuredHeight() / 2);

            child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
        }
    }
}
