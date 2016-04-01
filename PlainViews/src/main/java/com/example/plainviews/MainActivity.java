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

import com.example.plainviews.ui.fragment.BaseFragment;
import com.example.plainviews.ui.fragment.EtaFragment;
import com.example.plainviews.ui.fragment.IotaFragment;
import com.example.plainviews.ui.fragment.ThetaFragment;
import com.example.plainviews.ui.fragment.ZetaFragment;
import com.example.plainviews.widget.RtlViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.design.widget.TabLayout.ViewPagerOnTabSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.ArraySet;
import android.util.Log;
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
 * Main activity.
 */
public class MainActivity extends BaseActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	// Alarm action for midnight (so we can update the date display).
	private static final String KEY_SELECTED_TAB = "selected_tab";

	// Request code used when SettingsActivity is launched.
	private static final int REQUEST_CHANGE_SETTINGS = 1;

	public static final int ZETA_TAB_INDEX = 0;
	public static final int ETA_TAB_INDEX = 1;
	public static final int THETA_TAB_INDEX = 2;
	public static final int IOTA_TAB_INDEX = 3;

	private TabLayout tabLayout;
	private Menu menu;
	private RtlViewPager viewPager;
	private ImageView fab;
	private ImageButton leftButton;
	private ImageButton rightButton;

	private TabsAdapter tabsAdapter;
	private int selectedTab;
	private boolean activityResumed;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		if (icicle != null) {
			selectedTab = icicle.getInt(KEY_SELECTED_TAB, ZETA_TAB_INDEX);
		} else {
			selectedTab = ZETA_TAB_INDEX;
		}

		initViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		activityResumed = true;
	}

	@Override
	public void onPause() {
		activityResumed = false;
		super.onPause();
	}

	@Override
	public void onNewIntent(Intent newIntent) {
		super.onNewIntent(newIntent);
		Log.d("", "onNewIntent with intent: " + newIntent);

		// update our intent so that we can consult it to determine whether or
		// not the most recent launch was via a dock event
		setIntent(newIntent);
	}

	private void initViews() {
		setContentView(R.layout.main_activity);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
		fab = (ImageView) findViewById(R.id.fab);
		leftButton = (ImageButton) findViewById(R.id.left_button);
		rightButton = (ImageButton) findViewById(R.id.right_button);
		if (tabsAdapter == null) {
			viewPager = (RtlViewPager) findViewById(R.id.desk_clock_pager);
			// Keep all four tabs to minimize jank.
			viewPager.setOffscreenPageLimit(3);
			// Set Accessibility Delegate to null so ViewPager doesn't intercept movements and
			// prevent the fab from being selected.
			viewPager.setAccessibilityDelegate(null);
			tabsAdapter = new TabsAdapter(this, viewPager);
			createTabs();
			tabLayout.setOnTabSelectedListener(new ViewPagerOnTabSelectedListener(viewPager));
		}

		fab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getSelectedFragment().onFabClick(view);
			}
		});
		leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getSelectedFragment().onLeftButtonClick(view);
			}
		});
		rightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				getSelectedFragment().onRightButtonClick(view);
			}
		});
	}

	@VisibleForTesting
	BaseFragment getSelectedFragment() {
		return (BaseFragment) tabsAdapter.getItem(selectedTab);
	}

	private void createTabs() {
		final TabLayout.Tab alarmTab = tabLayout.newTab();
		alarmTab.setIcon(R.drawable.ic_tab_alarm).setContentDescription(R.string.menu_alarm);
		tabsAdapter.addTab(alarmTab, ZetaFragment.class, ZETA_TAB_INDEX);

		final Tab clockTab = tabLayout.newTab();
		clockTab.setIcon(R.drawable.ic_tab_clock).setContentDescription(R.string.menu_clock);
		tabsAdapter.addTab(clockTab, EtaFragment.class, ETA_TAB_INDEX);

		final Tab timerTab = tabLayout.newTab();
		timerTab.setIcon(R.drawable.ic_tab_timer).setContentDescription(R.string.menu_timer);
		tabsAdapter.addTab(timerTab, ThetaFragment.class, THETA_TAB_INDEX);

		final Tab stopwatchTab = tabLayout.newTab();
		stopwatchTab.setIcon(R.drawable.ic_tab_stopwatch)
				.setContentDescription(R.string.menu_stopwatch);
		tabsAdapter.addTab(stopwatchTab, IotaFragment.class, IOTA_TAB_INDEX);

		tabLayout.getTabAt(selectedTab).select();
		viewPager.setCurrentItem(selectedTab);
		tabsAdapter.notifySelectedPage(selectedTab);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_SELECTED_TAB, tabLayout.getSelectedTabPosition());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// We only want to show it as a menu in landscape, and only for clock/alarm fragment.
		this.menu = menu;
		// Clear the menu so that it doesn't get duplicate items in case onCreateOptionsMenu
		// was called multiple times.
		menu.clear();
		getMenuInflater().inflate(R.menu.menu, menu);
		// Always return true, regardless of whether we've inflated the menu, so
		// that when we switch tabs this method will get called and we can inflate the menu.
		return true;
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
				startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class),
						REQUEST_CHANGE_SETTINGS);
				return true;
			default:
				break;
		}
		return true;
	}

	public void registerPageChangedListener(BaseFragment frag) {
		if (tabsAdapter != null) {
			tabsAdapter.registerPageChangedListener(frag);
		}
	}

	public void unregisterPageChangedListener(BaseFragment frag) {
		if (tabsAdapter != null) {
			tabsAdapter.unregisterPageChangedListener(frag);
		}
	}

	/**
	 * Adapter for wrapping together the ActionBar's tab with the ViewPager
	 */
	private class TabsAdapter extends FragmentPagerAdapter implements
			OnPageChangeListener {

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
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mPager = pager;
			mPager.setAdapter(this);
			mPager.setOnRTLPageChangeListener(this);
		}


		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			return super.instantiateItem(container, viewPager.getRtlAwareIndex(position));
		}

		@Override
		public Fragment getItem(int position) {
			// Because this public method is called outside many times,
			// check if it exits first before creating a new one.
			final String name = makeFragmentName(R.id.desk_clock_pager, position);
			Fragment fragment = getSupportFragmentManager().findFragmentByTag(name);
			if (fragment == null) {
				TabInfo info = mTabs.get(position);
				fragment = Fragment.instantiate(mContext, info.clss.getName(), info.args);
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
			tabLayout.addTab(tab);
			notifyDataSetChanged();
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// Do nothing
		}

		@Override
		public void onPageSelected(int position) {
			// Set the page before doing the menu so that onCreateOptionsMenu knows what page it
			// is.
			tabLayout.getTabAt(position).select();
			notifyPageChanged(position);
			setBackgroundColor(getResources().getColor(FRAGMENT_COLORS[position]), true);

			// Only show the overflow menu for alarm and world clock.
			if (menu != null) {
				// Make sure the menu's been initialized.
				menu.setGroupVisible(R.id.menu_items, true);
				onCreateOptionsMenu(menu);
			}
			selectedTab = position;

			if (activityResumed) {
				switch (selectedTab) {
					case ZETA_TAB_INDEX:
						break;
					case ETA_TAB_INDEX:
						break;
					case THETA_TAB_INDEX:
						break;
					case IOTA_TAB_INDEX:
						break;
				}
			}

			final BaseFragment f = (BaseFragment) getItem(position);
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
				final FragmentManager fm = getSupportFragmentManager();
				BaseFragment f = (BaseFragment) fm.findFragmentByTag(tag);
				if (f != null) {
					f.onPageChanged(newPage);
				}
			}
		}

		public void registerPageChangedListener(BaseFragment frag) {
			String tag = frag.getTag();
			if (mFragmentTags.contains(tag)) {
				Log.w("", "Trying to add an existing fragment " + tag);
			} else {
				mFragmentTags.add(frag.getTag());
			}
			// Since registering a listener by the fragment is done sometimes after the page
			// was already changed, make sure the fragment gets the current page
			frag.onPageChanged(tabLayout.getSelectedTabPosition());
		}

		public void unregisterPageChangedListener(BaseFragment frag) {
			mFragmentTags.remove(frag.getTag());
		}

	}

	public int getSelectedTab() {
		return selectedTab;
	}

	public ImageView getFab() {
		return fab;
	}

	public ImageButton getLeftButton() {
		return leftButton;
	}

	public ImageButton getRightButton() {
		return rightButton;
	}
}
