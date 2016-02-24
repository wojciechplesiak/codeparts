package com.example.plainviews.ui.fragment;

import com.example.plainviews.DeskClock;
import com.example.plainviews.DeskClockFragment;
import com.example.plainviews.R;
import com.example.plainviews.ToastMaster;
import com.example.plainviews.dataadapter.ItemsAdapter;
import com.example.plainviews.widget.EmptyViewController;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 */
public class OneFragment extends DeskClockFragment {

	private static final int SPAN_COUNT = 2;

	private ViewGroup mMainLayout;
	private RecyclerView mRecyclerView;

	private EmptyViewController mEmptyViewController;

	/**
	 * The public no-arg constructor required by all fragments.
	 */
	public OneFragment() {
	}

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
		// Inflate the layout for this fragment
		final View v = inflater.inflate(R.layout.one_fragment, container, false);

		mMainLayout = (ViewGroup) v.findViewById(R.id.main);

		setupRecyclerView(v);

		mEmptyViewController = new EmptyViewController(mMainLayout, v.findViewById(R.id
				.items_frame), v.findViewById(R.id.one_empty_view));
//		mEmptyViewController.setEmpty(true);

		return v;
	}

	private void setupRecyclerView(final View v) {
		mRecyclerView = (RecyclerView) v.findViewById(R.id.items_recycler_view);
		mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(SPAN_COUNT,
				StaggeredGridLayoutManager.VERTICAL));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setAdapter(new ItemsAdapter(getActivity()));
	}

	@Override
	public void onResume() {
		super.onResume();

		final DeskClock activity = (DeskClock) getActivity();
		if (activity.getSelectedTab() == DeskClock.ALARM_TAB_INDEX) {
			setFabAppearance();
			setLeftRightButtonAppearance();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ToastMaster.cancelToast();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
}
