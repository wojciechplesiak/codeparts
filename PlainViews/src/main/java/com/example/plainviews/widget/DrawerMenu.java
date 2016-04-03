package com.example.plainviews.widget;

import com.example.plainviews.DrawerActivity;
import com.example.plainviews.R;
import com.example.plainviews.Utils;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Drawer menu widget
 */
public class DrawerMenu {

	private DrawerActivity activity;

	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;

	private String[] fragmentTitles;


	public DrawerMenu(DrawerActivity activity, String[] fragmentTitles) {
		this.activity = activity;
		this.fragmentTitles = fragmentTitles;

		setupDrawerLayout();
		setupDrawerList();
		setupDrawerToggle();
	}

	/**
	 * Opens drawer with animation
	 */
	public void open() {
		drawerLayout.openDrawer(drawerList);
	}

	/**
	 * Closes drawer with animation
	 */
	public void close() {
		drawerLayout.closeDrawer(drawerList);
	}

	/**
	 * Check if the drawer view is currently in an open state
	 */
	public boolean isOpen() {
		return drawerLayout.isDrawerOpen(drawerList);
	}

	/**
	 * Returns the currently selected item in drawer menu list
	 */
	public int getSelectedPosition() {
		return drawerList.getCheckedItemPosition();
	}

	/**
	 * Sets the checked state of the specified position
	 */
	public void setItemSelected(int position) {
		drawerList.setItemChecked(position, true);
	}

	/**
	 * Sets background color of drawer list
	 *
	 * @param color the color of the background
	 */
	public void setBackgroundColor(int color) {
		drawerList.setBackgroundColor(color);
	}

	public ActionBarDrawerToggle getDrawerToggle() {
		return drawerToggle;
	}

	private void setupDrawerLayout() {
		drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
		if (drawerLayout == null) {
			throw new IllegalArgumentException("DrawerLayout missing from parent activity");
		}

		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	}

	private void setupDrawerList() {
		drawerList = (ListView) activity.findViewById(R.id.left_drawer);
		if (drawerList == null) {
			throw new IllegalArgumentException("Drawer ListView missing from drawer layout");
		}

		drawerList.setAdapter(new ArrayAdapter<>(activity, R.layout.drawer_list_item,
				fragmentTitles));
		drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				activity.onDrawerItemSelected(position);
			}
		});
	}

	private void setupDrawerToggle() {
		drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				activity.invalidateOptionsMenu();
				int color = Utils.getColor(activity, Utils.FRAGMENT_COLORS[drawerList
						.getCheckedItemPosition()]);
				drawerList.setBackgroundColor(color);
			}

			public void onDrawerOpened(View drawerView) {
				activity.getToolbar().setTitle(activity.getTitle());
			}

			@Override
			public void onDrawerStateChanged(int newState) {
				super.onDrawerStateChanged(newState);
				if (newState == DrawerLayout.STATE_DRAGGING || newState == DrawerLayout
						.STATE_SETTLING) {
					activity.hideBottomBar();
				}
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);
	}
}
