package com.example.plainviews;

import com.example.plainviews.ui.fragment.EtaFragment;
import com.example.plainviews.ui.fragment.IotaFragment;
import com.example.plainviews.ui.fragment.ThetaFragment;
import com.example.plainviews.ui.fragment.ZetaFragment;
import com.example.plainviews.widget.BottomBarDragLayout;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Main activity with drawer.
 */
public class DrawerActivity extends BaseActivity {

	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	private BottomBarDragLayout bottomBarDragLayout;

	private CharSequence drawerTitle;
	private CharSequence title;
	private String[] fragmentTitles = new String[]{"ZETA", "ETA", "THETA", "IOTA"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_drawer_layout);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		bottomBarDragLayout = (BottomBarDragLayout) findViewById(R.id.bottom_bar_layout);
		bottomBarDragLayout.showBottomBar();

		title = drawerTitle = getTitle();
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer opens
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		drawerList.setAdapter(new ArrayAdapter<>(this,
				R.layout.drawer_list_item, fragmentTitles));
		drawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(title);
				invalidateOptionsMenu();
				int color = getResources().getColor(FRAGMENT_COLORS[drawerList
						.getCheckedItemPosition()]);
				drawerList.setBackgroundColor(color);
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(drawerTitle);
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerStateChanged(int newState) {
				super.onDrawerStateChanged(newState);
				if (newState == DrawerLayout.STATE_DRAGGING || newState == DrawerLayout
						.STATE_SETTLING) {
					bottomBarDragLayout.hideBottomBar();
				}
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
			int color = getResources().getColor(FRAGMENT_COLORS[drawerList
					.getCheckedItemPosition()]);
			drawerList.setBackgroundColor(color);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
		menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
			case R.id.action_websearch:
				// create intent to perform web search for this planet
//				Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//				intent.putExtra(SearchManager.QUERY, getSupportActionBar().getTitle());
//				// catch event that there's no activity to handle intent
//				if (intent.resolveActivity(getPackageManager()) != null) {
//					startActivity(intent);
//				} else {
//					Toast.makeText(this, "App not available", Toast.LENGTH_LONG).show();
//				}
				int posittion = drawerList.getCheckedItemPosition() + 1;
				if (posittion > 3) {
					posittion = 0;
				}
				selectItem(posittion);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
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
				throw new IllegalStateException("No more fragments to show!");
		}
		int color = getResources().getColor(FRAGMENT_COLORS[position]);
		setBackgroundColor(color, true);

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.setCustomAnimations(R.anim.dim_in, R.anim.dim_out)
				.replace(R.id.content_frame, fragment)
				.commit();

		// update selected item and title, then close the drawer
		drawerList.setItemChecked(position, true);
		setTitle(fragmentTitles[position]);
		drawerLayout.closeDrawer(drawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		this.title = title;
		getSupportActionBar().setTitle(this.title);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		drawerToggle.onConfigurationChanged(newConfig);
	}
}
