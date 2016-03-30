/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.example.plainviews.ui.fragment;

import com.example.plainviews.MainActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Base fragment to be used as a superclass for all first level fragments.
 */
public class BaseFragment extends Fragment {

	protected ImageView fab;
	protected ImageButton leftButton;
	protected ImageButton rightButton;

	public void onPageChanged(int page) {
		// Do nothing here , only in derived classes
	}

	public void onFabClick(View view) {
		// Do nothing here , only in derived classes
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final Activity activity = getActivity();
		if (activity instanceof MainActivity) {
			final MainActivity mainActivityActivity = (MainActivity) activity;
			fab = mainActivityActivity.getFab();
			fab.setVisibility(View.GONE);
			leftButton = mainActivityActivity.getLeftButton();
			rightButton = mainActivityActivity.getRightButton();
		}
	}

	public void setFabAppearance() {
		// Do nothing here , only in derived classes
	}

	public void setLeftRightButtonAppearance() {
		// Do nothing here , only in derived classes
	}

	public void onLeftButtonClick(View view) {
		// Do nothing here , only in derived classes
	}

	public void onRightButtonClick(View view) {
		// Do nothing here , only in derived classes
	}

	protected final MainActivity getDeskClock() {
		return (MainActivity) getActivity();
	}

	protected final int getSelectedTab() {
		final MainActivity mainActivity = getDeskClock();
		return mainActivity == null ? -1 : mainActivity.getSelectedTab();
	}
}
