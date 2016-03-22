/*
 * Copyright (C) 2015 The Android Open Source Project
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

import com.example.plainviews.widget.EmptyViewController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment that displays a list of alarm time and allows interaction with them.
 */
public final class AlarmClockFragment extends DeskClockFragment {

	// Views
	private ViewGroup mMainLayout;
	private EmptyViewController mEmptyViewController;

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
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
	public void onFabClick(View view) {
		startCreatingAlarm();
	}

	@Override
	public void setFabAppearance() {
		if (mFab == null || getDeskClock().getSelectedTab() != DeskClock.ALARM_TAB_INDEX) {
			return;
		}
		mFab.setVisibility(View.VISIBLE);
		mFab.setImageResource(R.drawable.ic_add_white_24dp);
		mFab.setContentDescription(getString(R.string.button_alarms));
	}

	@Override
	public void setLeftRightButtonAppearance() {
		if (mLeftButton == null || mRightButton == null ||
				getDeskClock().getSelectedTab() != DeskClock.ALARM_TAB_INDEX) {
			return;
		}
		mLeftButton.setVisibility(View.INVISIBLE);
		mRightButton.setVisibility(View.INVISIBLE);
	}

	private void startCreatingAlarm() {
	}
}
