package com.example.plainviews.ui.fragment;

import com.example.plainviews.DeskClockFragment;
import com.example.plainviews.R;
import com.example.plainviews.widget.DullView;
import com.example.plainviews.widget.EmptyViewController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 */
public class EtaFragment extends DeskClockFragment {

	/**
	 * The public no-arg constructor required by all fragments.
	 */
	public EtaFragment() {
	}

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
		// Inflate the layout for this fragment
		final View v = inflater.inflate(R.layout.eta_fragment, container, false);

		ViewGroup mainLayout = (ViewGroup) v.findViewById(R.id.main);

		final EmptyViewController emptyViewController = new EmptyViewController(mainLayout, v
				.findViewById(R.id.content_frame), v.findViewById(R.id.one_empty_view));
		emptyViewController.setEmpty(false);

		final DullView dullView = (DullView)v.findViewById(R.id.path_view);
		mainLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dullView.reveal();
			}
		});

		return v;
	}
}
