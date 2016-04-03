package com.example.plainviews.ui.fragment;

import com.example.plainviews.AnimatorUtils;
import com.example.plainviews.DrawerActivity;
import com.example.plainviews.R;
import com.example.plainviews.Utils;
import com.example.plainviews.ui.glimpse.Glimpse;
import com.example.plainviews.widget.EmptyViewController;
import com.example.plainviews.widget.PathView;

import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;

/**
 * Sample fragment to be removed or edited with some proper implementation.
 */
public class ZetaFragment extends BaseFragment {

	private final static int TOP_OFFSET = 200;

	private NumberPicker numberPicker;
	private PathView pathView;
	private ViewGroup sampleViewGroup;

	private boolean numberPickerFinishedMoving = true;

	public ZetaFragment() {
	}

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {

		final View v = inflater.inflate(R.layout.zeta_fragment, container, false);

		final ViewGroup mainLayout = (ViewGroup) v.findViewById(R.id.main);

		final EmptyViewController emptyViewController = new EmptyViewController(mainLayout, v
				.findViewById(R.id.content_frame), v.findViewById(R.id.one_empty_view));
		emptyViewController.setEmpty(false);

		mainLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				emptyViewController.setEmpty(!emptyViewController.isEmpty());
				((DrawerActivity) getActivity()).hideBottomBar();
			}
		});

		numberPicker = (NumberPicker) v.findViewById(R.id.number_picker);
		pathView = (PathView) v.findViewById(R.id.path_view);
		sampleViewGroup = (ViewGroup) v.findViewById(R.id.sampleViewGroup);

		setMockedDataToNumberPicker();
		setMockedPathView();

		return v;
	}

	private void setMockedPathView() {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;

		List<Path> paths = new ArrayList<>();
		Path path = new Path();
		path.addRect(width / 2 - 250, TOP_OFFSET, width / 2 + 250, TOP_OFFSET + 200, Path
				.Direction.CW);
		paths.add(path);

		paths.addAll(pathView.getStringPaths("PUSH", width / 2 - 120, TOP_OFFSET + 130));

		pathView.setPaths(paths);

		pathView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (numberPicker.getValue()) {
					case 0:
						Glimpse.error(getContext(), "Say hello to error Glimpse!").show();
						break;
					case 1:
						AnimatorUtils.createReveal(getContext(), pathView, Utils.getColor
								(getContext(), R.color.anthracite))
								.withText("Action", "Performed")
								.build()
								.start();
						break;
					case 2:
						AnimatorUtils.createReveal(getContext(), sampleViewGroup, Utils.getColor
								(getContext(), R.color.tab7_color))
								.fitInContainer(sampleViewGroup)
								.withText("Inner", "Reveal")
								.setCancelable(true)
								.build()
								.start();
						break;
				}
			}
		});
	}

	private void setMockedDataToNumberPicker() {
		numberPicker.setMinValue(0);
		numberPicker.setMaxValue(2);
		numberPicker.setDisplayedValues(new String[]{"GLIMPSE", "REVEAL", "INNER"});
		numberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
			@Override
			public void onScrollStateChange(NumberPicker view, int scrollState) {
				if (scrollState != SCROLL_STATE_IDLE) {
					pathView.setVisibility(View.GONE);
					numberPickerFinishedMoving = false;
				} else {
					pathView.setVisibility(View.VISIBLE);
					pathView.reveal();
					numberPickerFinishedMoving = true;
				}
			}
		});
		numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				if (numberPickerFinishedMoving) {
					pathView.setVisibility(View.VISIBLE);
					pathView.reveal();
				}
			}
		});
	}


}
