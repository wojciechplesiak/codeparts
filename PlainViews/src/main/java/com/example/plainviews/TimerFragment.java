/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.plainviews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

public class TimerFragment extends DeskClockFragment implements OnSharedPreferenceChangeListener {
    public static final long ANIMATION_TIME_MILLIS = DateUtils.SECOND_IN_MILLIS / 3;

    private static final int PAGINATION_DOTS_COUNT = 4;

    private boolean mTicking = false;
    private ImageButton mCancel;
    private ViewGroup mContentView;
    private View mTimerView;
    private ImageView[] mPageIndicators = new ImageView[PAGINATION_DOTS_COUNT];
    private Transition mDeleteTransition;
    private SharedPreferences mPrefs;

//    private final ViewPager.OnPageChangeListener mOnPageChangeListener =
//            new ViewPager.SimpleOnPageChangeListener() {
//                @Override
//                public void onPageSelected(int position) {
//                    highlightPageIndicator(position);
////                    TimerFragment.this.setTimerViewFabIcon(getCurrentTimer());
//                }
//            };

//    private final Runnable mClockTick = new Runnable() {
//        boolean mVisible = true;
//        final static int TIME_PERIOD_MS = 1000;
//        final static int TIME_DELAY_MS = 20;
//        final static int SPLIT = TIME_PERIOD_MS / 2;
//
//        @Override
//        public void run() {
//        }
//    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.timer_fragment, container, false);
        mContentView = (ViewGroup) view;
        mTimerView = view.findViewById(R.id.timer_view);
        mPageIndicators[0] = (ImageView) view.findViewById(R.id.page_indicator0);
        mPageIndicators[1] = (ImageView) view.findViewById(R.id.page_indicator1);
        mPageIndicators[2] = (ImageView) view.findViewById(R.id.page_indicator2);
        mPageIndicators[3] = (ImageView) view.findViewById(R.id.page_indicator3);
        mCancel = (ImageButton) view.findViewById(R.id.timer_cancel);
        mDeleteTransition = new AutoTransition();
        mDeleteTransition.setDuration(ANIMATION_TIME_MILLIS / 2);
        mDeleteTransition.setInterpolator(new AccelerateDecelerateInterpolator());

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Context context = getActivity();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof DeskClock) {
            DeskClock activity = (DeskClock) getActivity();
            activity.registerPageChangedListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof DeskClock) {
            ((DeskClock) getActivity()).unregisterPageChangedListener(this);
        }
        mPrefs.unregisterOnSharedPreferenceChangeListener(this);
        stopClockTicks();
    }





    @Override
    public void onPageChanged(int page) {
    }

    // Stops the ticks that animate the timers.
    private void stopClockTicks() {
        if (mTicking) {
            mTicking = false;
        }
    }

    @Override
    public void onFabClick(View view) {
    }

    @Override
    public void setFabAppearance() {
        final DeskClock activity = (DeskClock) getActivity();
        if (mFab == null) {
            return;
        }

        if (activity.getSelectedTab() != DeskClock.TIMER_TAB_INDEX) {
            mFab.setVisibility(View.VISIBLE);
            return;
        }
    }

    @Override
    public void setLeftRightButtonAppearance() {
        final DeskClock activity = (DeskClock) getActivity();
        if (mLeftButton == null || mRightButton == null ||
                activity.getSelectedTab() != DeskClock.TIMER_TAB_INDEX) {
            return;
        }

        mLeftButton.setEnabled(true);
        mRightButton.setEnabled(true);
//        mLeftButton.setVisibility(mLastView != mTimerView ? View.GONE : View.VISIBLE);
//        mRightButton.setVisibility(mLastView != mTimerView ? View.GONE : View.VISIBLE);
        mLeftButton.setImageResource(R.drawable.ic_delete);
        mLeftButton.setContentDescription(getString(R.string.timer_delete));
        mRightButton.setImageResource(R.drawable.ic_add_timer);
        mRightButton.setContentDescription(getString(R.string.timer_add_timer));
    }

    @Override
    public void onRightButtonClick(View view) {
    }

    @Override
    public void onLeftButtonClick(View view) {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
    }
}
