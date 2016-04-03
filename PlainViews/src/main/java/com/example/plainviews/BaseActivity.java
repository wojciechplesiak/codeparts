package com.example.plainviews;

import android.animation.ObjectAnimator;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Base activity class that changes with window's background color dynamically based on the
 * current hour.
 */
public class BaseActivity extends AppCompatActivity {

	/**
	 * Key used to save/restore the current background color from the saved instance state.
	 */
	private static final String KEY_BACKGROUND_COLOR = "background_color";

	/**
	 * Duration in millis to animate changes to the background color.
	 */
	private static final long BACKGROUND_COLOR_ANIMATION_DURATION = 2000L;

	/**
	 * {@link ColorDrawable} used to draw the window's background.
	 */
	private ColorDrawable background;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final int currentColor = Utils.getColor(this, R.color.default_background);
		final int backgroundColor = savedInstanceState == null ? currentColor
				: savedInstanceState.getInt(KEY_BACKGROUND_COLOR, currentColor);
		setBackgroundColor(backgroundColor, false);
	}

	/**
	 * Sets the current background color to the provided value and animates the change if desired.
	 *
	 * @param color   the ARGB value to set as the current background color
	 * @param animate {@code true} if the change should be animated
	 */
	protected void setBackgroundColor(int color, boolean animate) {
		if (background == null) {
			background = new ColorDrawable(color);
			getWindow().setBackgroundDrawable(background);
		}

		if (background.getColor() != color) {
			if (animate) {
				ObjectAnimator.ofObject(background, "color", AnimatorUtils.ARGB_EVALUATOR, color)
						.setDuration(BACKGROUND_COLOR_ANIMATION_DURATION)
						.start();
			} else {
				background.setColor(color);
			}
		}
	}
}
