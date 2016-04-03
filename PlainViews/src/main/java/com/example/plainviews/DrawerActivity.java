package com.example.plainviews;

import com.example.plainviews.ui.fragment.EtaFragment;
import com.example.plainviews.ui.fragment.IotaFragment;
import com.example.plainviews.ui.fragment.ThetaFragment;
import com.example.plainviews.ui.fragment.ZetaFragment;
import com.example.plainviews.widget.BottomBar;
import com.example.plainviews.widget.DrawerMenu;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Main activity with drawer.
 */
public class DrawerActivity extends BaseActivity {

	private static final int INITIAL_POSITION = 0;
	private static final int REQUEST_OPEN_SETTINGS = 1;

	private DrawerMenu drawerMenu;
	private BottomBar bottomBar;

	private CharSequence title;
	private String[] fragmentTitles = new String[]{"ZETA", "ETA", "THETA", "IOTA"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_drawer_layout);

		setupToolbar();
		setupDrawerMenu();
		setupBottomBar();

		title = getTitle();

		if (savedInstanceState == null) {
			selectItem(INITIAL_POSITION, false);
			int color = Utils.getFragmentColor(this, INITIAL_POSITION);
			drawerMenu.setBackgroundColor(color);
			bottomBar.show();
		}
	}

	private void setupToolbar() {
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getToolbar().setDisplayHomeAsUpEnabled(true);
		getToolbar().setHomeButtonEnabled(true);
	}

	private void setupDrawerMenu() {
		drawerMenu = new DrawerMenu(this, fragmentTitles);
	}

	private void setupBottomBar() {
		bottomBar = new BottomBar(this);
		createTabs();
	}

	private void createTabs() {
		if (bottomBar == null) {
			return;
		}

		bottomBar.addTab(R.drawable.ic_tab_alarm, R.string.tab_zeta);
		bottomBar.addTab(R.drawable.ic_tab_clock, R.string.tab_eta);
		bottomBar.addTab(R.drawable.ic_tab_timer, R.string.tab_theta);
		bottomBar.addTab(R.drawable.ic_tab_stopwatch, R.string.tab_iota);
		bottomBar.selectTabAt(INITIAL_POSITION);
		bottomBar.initTabsListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerMenu.getDrawerToggle().onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
			case R.id.menu_item_settings:
				startActivityForResult(new Intent(DrawerActivity.this, SettingsActivity.class),
						REQUEST_OPEN_SETTINGS);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		this.title = title;
		getToolbar().setTitle(this.title);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerMenu.getDrawerToggle().syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerMenu.getDrawerToggle().onConfigurationChanged(newConfig);
	}

	/**
	 * Hides bottom bar with animation.
	 * <p/>
	 * Note: there is this strange 'bug' when bottom bar disappears whenever onLayout is called
	 * inside BottomBarDragLayout (eg. due to view visibility change or view is added/removed).
	 * As a workaround call this method whenever you need to to hide th bottom bar with animation.
	 */
	public void hideBottomBar() {
		if (bottomBar != null) {
			bottomBar.hide();
		}
	}

	/**
	 * Called when new a tab on BottomBar was selected
	 */
	public void onBottomBarTabSelected(int position) {
		selectItem(position);
	}

	/**
	 * Called when an item on Drawer menu was selected
	 */
	public void onDrawerItemSelected(int position) {
		selectItem(position);
	}

	/**
	 * Gets ActionBar created in this activity
	 */
	public
	@NonNull
	ActionBar getToolbar() {
		return getSupportActionBar();
	}


	// TODO: (w.plesiak 2016-04-03) refactor this method. Create fragments in some organized way
	private void selectItem(int position) {
		selectItem(position, true);
	}

	private void selectItem(int position, boolean hideBottomBar) {
		Fragment fragment;
		switch (position) {
			case 0:
				fragment = new ZetaFragment();
				break;
			case 1:
				fragment = new EtaFragment();
				break;
			case 2:
				fragment = new ThetaFragment();
				break;
			case 3:
				fragment = new IotaFragment();
				break;
			default:
				Toast.makeText(this, "No more fragments to show", Toast.LENGTH_SHORT).show();
				return;
		}
		int color = Utils.getFragmentColor(this, position);
		setBackgroundColor(color, true);
		if (hideBottomBar) {
			hideBottomBar();
		}

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.setCustomAnimations(R.anim.dim_in, R.anim.dim_out)
				.replace(R.id.content_frame, fragment)
				.commit();

		// update selected item and title, then close the drawer
		drawerMenu.setItemSelected(position);
		setTitle(fragmentTitles[position]);
		drawerMenu.close();
	}
}
