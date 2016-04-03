package com.example.plainviews.widget;

import com.example.plainviews.DrawerActivity;
import com.example.plainviews.R;

import android.support.design.widget.TabLayout;

/**
 * BottomBar widget
 */
public class BottomBar {

	private DrawerActivity activity;

	private BottomBarDragLayout bottomBarDragLayout;
	private TabLayout tabLayout;

	public BottomBar(DrawerActivity activity) {
		this.activity = activity;

		setupBottomBarDragLayout();
		setupBottomBarTabs();
	}

	private void setupBottomBarDragLayout() {
		bottomBarDragLayout = (BottomBarDragLayout) activity.findViewById(R.id.bottom_bar_layout);
		if (bottomBarDragLayout == null) {
			throw new IllegalArgumentException("BottomBarDragLayout missing from parent activity");
		}
		bottomBarDragLayout.showBottomBar();
	}

	private void setupBottomBarTabs() {
		tabLayout = (TabLayout) activity.findViewById(R.id.bottom_bar_sliding_tabs);
		if (tabLayout == null) {
			throw new IllegalArgumentException("TabLayout missing from BottomBarDragLayout");
		}
	}

	/**
	 * Sets up tab layout listener to receive callbacks when user interacts with tabs.
	 * This method should be called after initial tabs are added to tab layout.
	 */
	public void initTabsListener() {
		tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
//				activity.onBottomBarTabSelected(tab.getPosition());
				bottomBarDragLayout.onUserInteraction();
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
				bottomBarDragLayout.onUserInteraction();
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {
				bottomBarDragLayout.onUserInteraction();
			}
		});
	}

	/**
	 * Hides bottom bar with animation.
	 * <p/>
	 * Note: there is this strange 'bug' when bottom bar disappears whenever onLayout is called
	 * inside BottomBarDragLayout (eg. due to view visibility change or view is added/removed).
	 * As a workaround call this method whenever you need to to hide th bottom bar with animation.
	 */
	public void hide() {
		if (bottomBarDragLayout != null) {
			bottomBarDragLayout.hideBottomBar();
		}
	}

	/**
	 * Shows bottom bar with animation.
	 */
	public void show() {
		if (bottomBarDragLayout != null) {
			bottomBarDragLayout.showBottomBar();
		}
	}

	/**
	 * Adds tab to BottomBar's tab layout. Don't forget to call #initTabsListener() when you are
	 * done
	 * initializing tabs.
	 */
	public void addTab(int iconResId, int contentDescriptionResId) {
		final TabLayout.Tab tab = tabLayout.newTab();
		tab.setIcon(iconResId).setContentDescription(contentDescriptionResId);
		tabLayout.addTab(tab);
	}

	/**
	 * Select given tab in tab layout. Does nothing if tab under given index does not exist
	 */
	public void selectTabAt(int index) {
		TabLayout.Tab tab = tabLayout.getTabAt(index);
		if (tab != null) {
			tab.select();
		}
	}
}