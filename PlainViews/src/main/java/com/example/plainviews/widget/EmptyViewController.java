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

package com.example.plainviews.widget;

import com.example.plainviews.Utils;

import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Controller that displays empty view and handles animation appropriately.
 */
public final class EmptyViewController {

	private static final int ANIMATION_DURATION = 300;

	private final Transition emptyViewTransition;
	private final ViewGroup mainLayout;
	private final View contentView;
	private final View emptyView;
	private boolean isEmpty;

	/**
	 * Constructor of the controller.
	 *
	 * @param contentView The view that should be displayed when empty view is hidden.
	 * @param emptyView   The view that should be displayed when main view is empty.
	 */
	public EmptyViewController(ViewGroup mainLayout, View contentView, View emptyView) {
		this.mainLayout = mainLayout;
		this.contentView = contentView;
		this.emptyView = emptyView;
		if (Utils.isLOrLater()) {
			emptyViewTransition = new TransitionSet()
					.setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
					.addTarget(contentView)
					.addTarget(emptyView)
					.addTransition(new Fade(Fade.OUT))
					.addTransition(new Fade(Fade.IN))
					.setDuration(ANIMATION_DURATION);
		} else {
			emptyViewTransition = null;
		}
	}

	/**
	 * Sets the state for the controller. If it's empty, it will display the empty view.
	 *
	 * @param isEmpty Whether or not the controller should transition into empty state.
	 */
	public void setEmpty(boolean isEmpty) {
		if (this.isEmpty == isEmpty) {
			return;
		}
		this.isEmpty = isEmpty;
		// State changed, perform transition.
		if (Utils.isLOrLater()) {
			TransitionManager.beginDelayedTransition(mainLayout, emptyViewTransition);
		}
		emptyView.setVisibility(this.isEmpty ? View.VISIBLE : View.GONE);
		contentView.setVisibility(this.isEmpty ? View.GONE : View.VISIBLE);
	}

	/**
	 * @return state of the controller.
	 */
	public boolean isEmpty() {
		return isEmpty;
	}
}
