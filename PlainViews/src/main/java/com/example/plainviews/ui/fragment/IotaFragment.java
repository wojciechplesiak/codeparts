package com.example.plainviews.ui.fragment;

import com.example.plainviews.R;
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
 * Sample fragment to be removed or edited with some proper implementation.
 */
public class IotaFragment extends BaseFragment {

	private static final int SPAN_COUNT = 2;

	public IotaFragment() {
	}

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {

		final View v = inflater.inflate(R.layout.iota_fragment, container, false);

		ViewGroup mainLayout = (ViewGroup) v.findViewById(R.id.main);

		setupRecyclerView(v);

		EmptyViewController emptyViewController = new EmptyViewController(mainLayout, v
				.findViewById(R.id.items_frame), v.findViewById(R.id.one_empty_view));
		emptyViewController.setEmpty(false);

		return v;
	}

	private void setupRecyclerView(final View v) {
		RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.items_recycler_view);
		recyclerView.setLayoutManager(new StaggeredGridLayoutManager(SPAN_COUNT,
				StaggeredGridLayoutManager.VERTICAL));
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setAdapter(new ItemsAdapter(getActivity()));
	}
}
