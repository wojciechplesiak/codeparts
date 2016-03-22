package com.example.plainviews.dataadapter;

import com.example.plainviews.R;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Data adapter for alarm time items.
 */
public final class ItemsAdapter extends RecyclerView.Adapter<ItemsViewHolder> {

	private final Context context;
	private final LayoutInflater inflater;

	List<String> items = new ArrayList<>();

	public ItemsAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		setHasStableIds(true);

		addMockItems();
	}

	private void addMockItems() {
		items.add("CAC");
		items.add("STB");
		items.add("CAT");
	}

	@Override
	public ItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View v = inflater.inflate(viewType, parent, false /* attachToRoot */);
		return new ItemsViewHolder(v);
	}

	@Override
	public int getItemViewType(int position) {
		return R.layout.one_item;
	}

	@Override
	public void onViewRecycled(ItemsViewHolder viewHolder) {
		super.onViewRecycled(viewHolder);
		viewHolder.clearData();
	}

	@Override
	public void onBindViewHolder(ItemsViewHolder viewHolder, int position) {
		viewHolder.bindItem(context, items.get(position));
	}

	@Override
	public int getItemCount() {
		return items.size();
	}
}
