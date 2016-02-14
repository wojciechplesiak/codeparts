package com.example.plainviews;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
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
     * {@link BroadcastReceiver} to update the background color whenever the system time changes.
     */
    private BroadcastReceiver mOnTimeChangedReceiver;

    /**
     * {@link ColorDrawable} used to draw the window's background.
     */
    private ColorDrawable mBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int currentColor = getResources().getColor(R.color.default_background);
        final int backgroundColor = savedInstanceState == null ? currentColor
                : savedInstanceState.getInt(KEY_BACKGROUND_COLOR, currentColor);
        setBackgroundColor(backgroundColor, false /* animate */);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensure the background color is up-to-date.
//        setBackgroundColor(Utils.getCurrentHourColor(), true /* animate */);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop updating the background color when not active.
        if (mOnTimeChangedReceiver != null) {
            unregisterReceiver(mOnTimeChangedReceiver);
            mOnTimeChangedReceiver = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the background color so we can animate the change when the activity is restored.
        if (mBackground != null) {
            outState.putInt(KEY_BACKGROUND_COLOR, mBackground.getColor());
        }
    }

    /**
     * Sets the current background color to the provided value and animates the change if desired.
     *
     * @param color the ARGB value to set as the current background color
     * @param animate {@code true} if the change should be animated
     */
    protected void setBackgroundColor(int color, boolean animate) {
        if (mBackground == null) {
            mBackground = new ColorDrawable(color);
            getWindow().setBackgroundDrawable(mBackground);
        }

        if (mBackground.getColor() != color) {
            if (animate) {
                ObjectAnimator.ofObject(mBackground, "color", AnimatorUtils.ARGB_EVALUATOR, color)
                        .setDuration(BACKGROUND_COLOR_ANIMATION_DURATION)
                        .start();
            } else {
                mBackground.setColor(color);
            }
        }
    }
}
