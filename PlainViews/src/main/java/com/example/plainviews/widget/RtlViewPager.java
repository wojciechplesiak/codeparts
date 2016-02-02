package com.example.plainviews.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

/**
 * Created by piha on 2016-02-02.
 */
public class RtlViewPager extends ViewPager {

    private OnPageChangeListener mListener;

    public RtlViewPager(Context context) {
        this(context, null /* attrs */);
    }

    public RtlViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {
                // Do nothing
            }

            @Override
            public void onPageSelected(int position) {
                if (mListener != null) {
                    mListener.onPageSelected(getRtlAwareIndex(position));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Do nothing
            }
        });
    }

    @Override
    public int getCurrentItem() {
        return getRtlAwareIndex(super.getCurrentItem());
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(getRtlAwareIndex(item));
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener unused) {
        throw new UnsupportedOperationException("Use setOnRTLPageChangeListener instead");
    }

    /**
     * Get a "RTL friendly" index. If the locale is LTR, the index is returned as is.
     * Otherwise it's transformed so view pager can render views using the new index for RTL. For
     * example, the second view will be rendered to the left of first view.
     *
     * @param index The logical index.
     */
    public int getRtlAwareIndex(int index) {
        if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) ==
                View.LAYOUT_DIRECTION_RTL) {
            return getAdapter().getCount() - index - 1;
        }
        return index;
    }

    /**
     * Sets a {@link OnPageChangeListener}. The listener will be called when a page is selected.
     */
    public void setOnRTLPageChangeListener(OnPageChangeListener listener) {
        mListener = listener;
    }
}
