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
public class ThetaFragment extends DeskClockFragment {

	/**
	 * The public no-arg constructor required by all fragments.
	 */
	public ThetaFragment() {
	}

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
		// Inflate the layout for this fragment
		final View v = inflater.inflate(R.layout.theta_fragment, container, false);

		ViewGroup mainLayout = (ViewGroup) v.findViewById(R.id.main);

		final EmptyViewController emptyViewController = new EmptyViewController(mainLayout, v
				.findViewById(R.id.content_frame), v.findViewById(R.id.one_empty_view));
		emptyViewController.setEmpty(true);

		mainLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				emptyViewController.setEmpty(!emptyViewController.isEmpty());
			}
		});

		return v;
	}
}
