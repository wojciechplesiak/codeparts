package com.example.plainviews.dataadapter;

import com.example.plainviews.AnimatorUtils;
import com.example.plainviews.DrawerActivity;
import com.example.plainviews.R;
import com.example.plainviews.ui.glimpse.Glimpse;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Abstract ViewHolder for alarm time items.
 */
public class ItemsViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

	final TextView name;
	final TextView description;
	final View action;
	final View cardItem;

	public ItemsViewHolder(View itemView) {
		super(itemView);
		name = (TextView)itemView.findViewById(R.id.item_name);
		description = (TextView)itemView.findViewById(R.id.item_description);
		action =  itemView.findViewById(R.id.item_action);
		cardItem = itemView.findViewById(R.id.card_item);
	}

	public void clearData() {
	}

	public void bindItem(final Context context, String item) {
		name.setText(item);
		description.setText("Date and Time: " + System.currentTimeMillis());
		description.setOnCreateContextMenuListener(this);
		action.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AnimatorUtils.createReveal(context, action, context.getResources().getColor(R
						.color.color_accent))
						.fitInContainer((ViewGroup) cardItem)
						.withText("Message", "Sent")
						.setCancelable(true)
						.build()
						.start();
			}
		});
		cardItem.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				AnimatorUtils.createReveal(context, name, context.getResources().getColor(R
						.color.default_background))
						.withText("Message", "Sent")
						.build()
						.start();
				return true;
			}
		});
		cardItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Glimpse.error(context, "Something went wrong").show();
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo
			menuInfo) {
		menu.setHeaderTitle("Select The Action");
		menu.add(0, v.getId(), 0, "Call");//groupId, itemId, order, title
		menu.add(0, v.getId(), 0, "SMS");
	}

	private void hideBottomBar(Context context) {
		if (context instanceof DrawerActivity) {
			((DrawerActivity) context).hideBottomBar();
		}
	}
}
