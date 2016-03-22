package com.example.plainviews.ui.glimpse;

import com.example.plainviews.AnimatorUtils;
import com.example.plainviews.R;
import com.example.plainviews.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A brief, passing information view. Opens with animation and closes automatically after a while.
 * Non-persistent view.
 */
public class Glimpse {
	/**
	 * @param message
	 */
	public static Builder error(Context context, String message) {
		return new Builder(context, Variant.ERROR, message);
	}

	public static class Builder {

		private final Context context;
		private ViewGroup containerView;
		private View source;
		private int revealColor;
		private String title;
		private String info;
		private boolean cancelable;
		private Variant variant;

		private Builder(@NonNull final Context context, @NonNull final Variant variant, @NonNull
		final String message) {
			this.context = checkNotNull(context);
			this.variant = variant;
			this.title = checkNotNull(message);
		}

		/**
		 *
		 */
		public void show() {
			AnimatorUtils.createReveal(context, null, variant.getColor(context))
					.withText(title, info)
					.setCancelableWithMessage(info)
					.setDismissible(true)
					.build()
					.start();
		}
	}

	private enum Variant {
		ERROR(R.color.error_color);

		private int color;

		Variant(int color) {
			this.color = color;
		}

		public int getColor(Context context) {
			return Utils.getColor(context, color);
		}
	}
}
