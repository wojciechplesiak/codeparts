package com.example.plainviews.ui.fragment;

import com.example.plainviews.DeskClockFragment;
import com.example.plainviews.R;
import com.example.plainviews.widget.EmptyViewController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 */
public class FooFragment extends DeskClockFragment {

	private static final int SPAN_COUNT = 2;

	/**
	 * The public no-arg constructor required by all fragments.
	 */
	public FooFragment() {
	}

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
		// Inflate the layout for this fragment
		final View v = inflater.inflate(R.layout.one_fragment, container, false);

		ViewGroup mainLayout = (ViewGroup) v.findViewById(R.id.main);

		EmptyViewController emptyViewController = new EmptyViewController(mainLayout, v
				.findViewById(R.id
				.items_frame), v.findViewById(R.id.one_empty_view));
		emptyViewController.setEmpty(true);

		return v;
	}
}
