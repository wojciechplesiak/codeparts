package com.example.plainviews.ui.fragment;

import com.example.plainviews.DeskClock;
import com.example.plainviews.DeskClockFragment;
import com.example.plainviews.R;
import com.example.plainviews.ToastMaster;
import com.example.plainviews.widget.EmptyViewController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 */
public class OneFragment extends DeskClockFragment {

	private ViewGroup mMainLayout;
	private EmptyViewController mEmptyViewController;

	/** The public no-arg constructor required by all fragments. */
	public OneFragment() {}

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
//        mCursorLoader = getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
		// Inflate the layout for this fragment
		final View v = inflater.inflate(R.layout.alarm_clock, container, false);

		mMainLayout = (ViewGroup) v.findViewById(R.id.main);

		mEmptyViewController = new EmptyViewController(mMainLayout, v.findViewById(R.id
				.alarms_dummy_view), v.findViewById(R.id.alarms_empty_view));
		mEmptyViewController.setEmpty(true);

		return v;
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
