package com.lb.pachong2.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.lb.pachong2.widget.media.IjkVideoView;

/**
 * Created by Administrator on 2018/3/25.
 */

public class KonIjkVideoView extends IjkVideoView {
    public KonIjkVideoView(Context context) {
        super(context);
    }

    public KonIjkVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KonIjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public KonIjkVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);

    }
}
