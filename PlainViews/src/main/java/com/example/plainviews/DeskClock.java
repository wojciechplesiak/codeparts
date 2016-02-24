/*
 * Copyright (C) 2009 The Android Open Source Project
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

import com.example.plainviews.ui.fragment.OneFragment;
import com.example.plainviews.widget.RtlViewPager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.design.widget.TabLayout.ViewPagerOnTabSelectedListener;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.ArraySet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * DeskClock clock view for desk docks.
 */
public class DeskClock extends BaseActivity {

    private static final String TAG = "DeskClock";

    // Alarm action for midnight (so we can update the date display).
    private static final String KEY_SELECTED_TAB = "selected_tab";

    // Request code used when SettingsActivity is launched.
    private static final int REQUEST_CHANGE_SETTINGS = 1;

    private static final int[] TAB_COLORS = new int[] {R.color.tab1_color, R.color.tab2_color,
            R.color.tab3_color, R.color.tab4_color, };

    public static final int ALARM_TAB_INDEX = 0;
    public static final int CLOCK_TAB_INDEX = 1;
    public static final int TIMER_TAB_INDEX = 2;
    public static final int STOPWATCH_TAB_INDEX = 3;

    private TabLayout mTabLayout;
    private Menu mMenu;
    private RtlViewPager mViewPager;
    private ImageView mFab;
    private ImageButton mLeftButton;
    private ImageButton mRightButton;

    private TabsAdapter mTabsAdapter;
    private int mSelectedTab;
    private boolean mActivityResumed;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        if (icicle != null) {
            mSelectedTab = icicle.getInt(KEY_SELECTED_TAB, ALARM_TAB_INDEX);
        } else {
            mSelectedTab = ALARM_TAB_INDEX;

            // Set the background color to initially match the theme value so that we can
            // smoothly transition to the dynamic color.
//            setBackgroundColor(getResources().getColor(R.color.tab5_color),
//                    false /* animate */);
        }

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActivityResumed = true;
    }

    @Override
    public void onPause() {
        mActivityResumed = false;
//        Utils.showInUseNotifications(this);
        super.onPause();
    }

    @Override
    public void onNewIntent(Intent newIntent) {
        super.onNewIntent(newIntent);
        LogUtils.d(TAG, "onNewIntent with intent: %s", newIntent);

        // update our intent so that we can consult it to determine whether or
        // not the most recent launch was via a dock event
        setIntent(newIntent);
    }

    private void initViews() {
        setContentView(R.layout.desk_clock);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mFab = (ImageView) findViewById(R.id.fab);
        mLeftButton = (ImageButton) findViewById(R.id.left_button);
        mRightButton = (ImageButton) findViewById(R.id.right_button);
        if (mTabsAdapter == null) {
            mViewPager = (RtlViewPager) findViewById(R.id.desk_clock_pager);
            // Keep all four tabs to minimize jank.
            mViewPager.setOffscreenPageLimit(3);
            // Set Accessibility Delegate to null so ViewPager doesn't intercept movements and
            // prevent the fab from being selected.
            mViewPager.setAccessibilityDelegate(null);
            mTabsAdapter = new TabsAdapter(this, mViewPager);
            createTabs();
            mTabLayout.setOnTabSelectedListener(new ViewPagerOnTabSelectedListener(mViewPager));
        }

        mFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedFragment().onFabClick(view);
            }
        });
        mLeftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedFragment().onLeftButtonClick(view);
            }
        });
        mRightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedFragment().onRightButtonClick(view);
            }
        });
    }

    @VisibleForTesting
    DeskClockFragment getSelectedFragment() {
        return (DeskClockFragment) mTabsAdapter.getItem(mSelectedTab);
    }

    private void createTabs() {
        final TabLayout.Tab alarmTab = mTabLayout.newTab();
        alarmTab.setIcon(R.drawable.ic_tab_alarm).setContentDescription(R.string.menu_alarm);
        mTabsAdapter.addTab(alarmTab, AlarmClockFragment.class, ALARM_TAB_INDEX);

        final Tab clockTab = mTabLayout.newTab();
        clockTab.setIcon(R.drawable.ic_tab_clock).setContentDescription(R.string.menu_clock);
        mTabsAdapter.addTab(clockTab, ClockFragment.class, CLOCK_TAB_INDEX);

        final Tab timerTab = mTabLayout.newTab();
        timerTab.setIcon(R.drawable.ic_tab_timer).setContentDescription(R.string.menu_timer);
        mTabsAdapter.addTab(timerTab, TimerFragment.class, TIMER_TAB_INDEX);

        final Tab stopwatchTab = mTabLayout.newTab();
        stopwatchTab.setIcon(R.drawable.ic_tab_stopwatch)
                .setContentDescription(R.string.menu_stopwatch);
        mTabsAdapter.addTab(stopwatchTab, OneFragment.class, STOPWATCH_TAB_INDEX);

        mTabLayout.getTabAt(mSelectedTab).select();
        mViewPager.setCurrentItem(mSelectedTab);
        mTabsAdapter.notifySelectedPage(mSelectedTab);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_TAB, mTabLayout.getSelectedTabPosition());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // We only want to show it as a menu in landscape, and only for clock/alarm fragment.
        mMenu = menu;
        // Clear the menu so that it doesn't get duplicate items in case onCreateOptionsMenu
        // was called multiple times.
        menu.clear();
        getMenuInflater().inflate(R.menu.desk_clock_menu, menu);
        // Always return true, regardless of whether we've inflated the menu, so
        // that when we switch tabs this method will get called and we can inflate the menu.
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateMenu(menu);
        return true;
    }

    private void updateMenu(Menu menu) {
        // Hide "lights out" for timer.
        MenuItem nightMode = menu.findItem(R.id.menu_item_night_mode);
        if (mTabLayout.getSelectedTabPosition() == CLOCK_TAB_INDEX) {
            nightMode.setVisible(true);
        } else {
            nightMode.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (processMenuClick(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Recreate the activity if any settings have been changed
        if (requestCode == REQUEST_CHANGE_SETTINGS && resultCode == RESULT_OK) {
            recreate();
        }
    }

    private boolean processMenuClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                startActivityForResult(new Intent(DeskClock.this, SettingsActivity.class),
                        REQUEST_CHANGE_SETTINGS);
                return true;
            case R.id.menu_item_night_mode:
//                startActivity(new Intent(DeskClock.this, ScreensaverActivity.class));
            default:
                break;
        }
        return true;
    }

    public void registerPageChangedListener(DeskClockFragment frag) {
        if (mTabsAdapter != null) {
            mTabsAdapter.registerPageChangedListener(frag);
        }
    }

    public void unregisterPageChangedListener(DeskClockFragment frag) {
        if (mTabsAdapter != null) {
            mTabsAdapter.unregisterPageChangedListener(frag);
        }
    }

    /**
     * Adapter for wrapping together the ActionBar's tab with the ViewPager
     */
    private class TabsAdapter extends FragmentPagerAdapter implements OnPageChangeListener {

        private static final String KEY_TAB_POSITION = "tab_position";

        final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, int position) {
                clss = _class;
                args = new Bundle();
                args.putInt(KEY_TAB_POSITION, position);
            }

            public int getPosition() {
                return args.getInt(KEY_TAB_POSITION, 0);
            }
        }

        private final List<TabInfo> mTabs = new ArrayList<>(4 /* number of fragments */);
        private final Context mContext;
        private final RtlViewPager mPager;
        // Used for doing callbacks to fragments.
        private final Set<String> mFragmentTags = new ArraySet<>(4 /* number of fragments */);

        public TabsAdapter(AppCompatActivity activity, RtlViewPager pager) {
            super(activity.getFragmentManager());
            mContext = activity;
            mPager = pager;
            mPager.setAdapter(this);
            mPager.setOnRTLPageChangeListener(this);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, mViewPager.getRtlAwareIndex(position));
        }

        @Override
        public Fragment getItem(int position) {
            // Because this public method is called outside many times,
            // check if it exits first before creating a new one.
            final String name = makeFragmentName(R.id.desk_clock_pager, position);
            Fragment fragment = getFragmentManager().findFragmentByTag(name);
            if (fragment == null) {
                TabInfo info = mTabs.get(position);
                fragment = Fragment.instantiate(mContext, info.clss.getName(), info.args);
                if (fragment instanceof TimerFragment) {
                    ((TimerFragment) fragment).setFabAppearance();
                    ((TimerFragment) fragment).setLeftRightButtonAppearance();
                }
            }
            return fragment;
        }

        /**
         * Copied from:
         * android/frameworks/support/v13/java/android/support/v13/app/FragmentPagerAdapter.java#94
         * Create unique name for the fragment so fragment manager knows it exist.
         */
        private String makeFragmentName(int viewId, int index) {
            return "android:switcher:" + viewId + ":" + index;
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        public void addTab(TabLayout.Tab tab, Class<?> clss, int position) {
            TabInfo info = new TabInfo(clss, position);
            mTabs.add(info);
            mTabLayout.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Do nothing
        }

        @Override
        public void onPageSelected(int position) {
            // Set the page before doing the menu so that onCreateOptionsMenu knows what page it is.
            mTabLayout.getTabAt(position).select();
            notifyPageChanged(position);
            setBackgroundColor(getResources().getColor(TAB_COLORS[position]), true);

            // Only show the overflow menu for alarm and world clock.
            if (mMenu != null) {
                // Make sure the menu's been initialized.
                mMenu.setGroupVisible(R.id.menu_items, true);
                onCreateOptionsMenu(mMenu);
            }
            mSelectedTab = position;

            if (mActivityResumed) {
                switch (mSelectedTab) {
                    case ALARM_TAB_INDEX:
//                        Events.sendAlarmEvent(R.string.action_show, R.string.label_deskclock);
                        break;
                    case CLOCK_TAB_INDEX:
//                        Events.sendClockEvent(R.string.action_show, R.string.label_deskclock);
                        break;
                    case TIMER_TAB_INDEX:
//                        Events.sendTimerEvent(R.string.action_show, R.string.label_deskclock);
                        break;
                    case STOPWATCH_TAB_INDEX:
//                        Events.sendStopwatchEvent(R.string.action_show, R.string.label_deskclock);
                        break;
                }
            }

            final DeskClockFragment f = (DeskClockFragment) getItem(position);
            if (f != null) {
                f.setFabAppearance();
                f.setLeftRightButtonAppearance();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // Do nothing
        }

        public void notifySelectedPage(int page) {
            notifyPageChanged(page);
        }

        private void notifyPageChanged(int newPage) {
            for (String tag : mFragmentTags) {
                final FragmentManager fm = getFragmentManager();
                DeskClockFragment f = (DeskClockFragment) fm.findFragmentByTag(tag);
                if (f != null) {
                    f.onPageChanged(newPage);
                }
            }
        }

        public void registerPageChangedListener(DeskClockFragment frag) {
            String tag = frag.getTag();
            if (mFragmentTags.contains(tag)) {
                LogUtils.wtf(TAG, "Trying to add an existing fragment " + tag);
            } else {
                mFragmentTags.add(frag.getTag());
            }
            // Since registering a listener by the fragment is done sometimes after the page
            // was already changed, make sure the fragment gets the current page
            frag.onPageChanged(mTabLayout.getSelectedTabPosition());
        }

        public void unregisterPageChangedListener(DeskClockFragment frag) {
            mFragmentTags.remove(frag.getTag());
        }

    }

    public int getSelectedTab() {
        return mSelectedTab;
    }

    public ImageView getFab() {
        return mFab;
    }

    public ImageButton getLeftButton() {
        return mLeftButton;
    }

    public ImageButton getRightButton() {
        return mRightButton;
    }
}
