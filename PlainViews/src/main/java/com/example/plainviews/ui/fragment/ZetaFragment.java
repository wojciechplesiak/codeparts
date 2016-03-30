package com.example.plainviews.ui.fragment;

import com.example.plainviews.R;
import com.example.plainviews.widget.EmptyViewController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

/**
 * Sample fragment to be removed or edited with some proper implementation.
 */
public class ZetaFragment extends BaseFragment {

	public ZetaFragment() {
	}

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
		// Inflate the layout for this fragment
		final View v = inflater.inflate(R.layout.zeta_fragment, container, false);

		ViewGroup mainLayout = (ViewGroup) v.findViewById(R.id.main);

		final EmptyViewController emptyViewController = new EmptyViewController(mainLayout, v
				.findViewById(R.id.content_frame), v.findViewById(R.id.one_empty_view));
		emptyViewController.setEmpty(false);

		mainLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				emptyViewController.setEmpty(!emptyViewController.isEmpty());
			}
		});

		NumberPicker numberPicker = (NumberPicker)v.findViewById(R.id.number_picker);
		numberPicker.setMinValue(1);
		numberPicker.setMaxValue(3);
		numberPicker.setDisplayedValues(new String[] {"1CF", "HT", "OTHER"});

		return v;
	}
}
