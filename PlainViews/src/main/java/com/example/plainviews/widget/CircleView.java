package com.example.plainviews.widget;

import com.example.plainviews.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Property;
import android.view.Gravity;
import android.view.View;

/**
 * A {@link View} that draws primitive circles.
 */
public class CircleView extends View {

	/**
	 * A Property wrapper around the fillColor functionality handled by the
	 * {@link #setFillColor(int)} and {@link #getFillColor()} methods.
	 */
	public final static Property<CircleView, Integer> FILL_COLOR =
			new Property<CircleView, Integer>(Integer.class, "fillColor") {
				@Override
				public Integer get(CircleView view) {
					return view.getFillColor();
				}

				@Override
				public void set(CircleView view, Integer value) {
					view.setFillColor(value);
				}
			};

	/**
	 * A Property wrapper around the radius functionality handled by the
	 * {@link #setRadius(float)} and {@link #getRadius()} methods.
	 */
	public final static Property<CircleView, Float> RADIUS =
			new Property<CircleView, Float>(Float.class, "radius") {
				@Override
				public Float get(CircleView view) {
					return view.getRadius();
				}

				@Override
				public void set(CircleView view, Float value) {
					view.setRadius(value);
				}
			};
	private static final float HALF = 0.5f;
	private static final int LTR_LAYOUT_DIRECTION = 0;

	/**
	 * The {@link Paint} used to draw the circle.
	 */
	private final Paint circlePaint = new Paint();

	private int gravity;
	private float centerX;
	private float centerY;
	private float radius;

	public CircleView(Context context) {
		this(context, null /* attrs */);
	}

	public CircleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0 /* defStyleAttr */);
	}

	public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		final TypedArray a = context.obtainStyledAttributes(
				attrs, R.styleable.CircleView, defStyleAttr, 0 /* defStyleRes */);

		gravity = a.getInt(R.styleable.CircleView_android_gravity, Gravity.NO_GRAVITY);
		centerX = a.getDimension(R.styleable.CircleView_centerX, 0.0f);
		centerY = a.getDimension(R.styleable.CircleView_centerY, 0.0f);
		radius = a.getDimension(R.styleable.CircleView_radius, 0.0f);

		circlePaint.setColor(a.getColor(R.styleable.CircleView_fillColor, Color.WHITE));

		a.recycle();
	}

	@Override
	public void onRtlPropertiesChanged(int layoutDirection) {
		super.onRtlPropertiesChanged(layoutDirection);

		if (gravity != Gravity.NO_GRAVITY) {
			applyGravity(gravity);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (gravity != Gravity.NO_GRAVITY) {
			applyGravity(gravity);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// draw the circle, duh
		canvas.drawCircle(centerX, centerY, radius, circlePaint);
	}

	@Override
	public boolean hasOverlappingRendering() {
		// only if we have a background, which we shouldn't...
		return getBackground() != null && getBackground().getCurrent() != null;
	}

	/**
	 * @return the current {@link Gravity} used to align/size the circle
	 */
	public final int getGravity() {
		return gravity;
	}

	/**
	 * Describes how to align/size the circle relative to the view's bounds. Defaults to
	 * {@link Gravity#NO_GRAVITY}.
	 * <p/>
	 * Note: using {@link #setCenterX(float)}, {@link #setCenterY(float)}, or
	 * {@link #setRadius(float)} will automatically clear any conflicting gravity bits.
	 *
	 * @param gravity the {@link Gravity} flags to use
	 * @return this object, allowing calls to methods in this class to be chained
	 */
	public CircleView setGravity(int gravity) {
		if (this.gravity != gravity) {
			this.gravity = gravity;

			if (gravity != Gravity.NO_GRAVITY /*&& isLayoutDirectionResolved()*/) {
				applyGravity(gravity);
			}
		}
		return this;
	}

	/**
	 * @return the ARGB color used to fill the circle
	 */
	public final int getFillColor() {
		return circlePaint.getColor();
	}

	/**
	 * Sets the ARGB color used to fill the circle and invalidates only the affected area.
	 *
	 * @param color the ARGB color to use
	 * @return this object, allowing calls to methods in this class to be chained
	 */
	public CircleView setFillColor(int color) {
		if (circlePaint.getColor() != color) {
			circlePaint.setColor(color);

			// invalidate the current area
			invalidate(centerX, centerY, radius);
		}
		return this;
	}

	/**
	 * @return the x-coordinate of the center of the circle
	 */
	public final float getCenterX() {
		return centerX;
	}

	/**
	 * Sets the x-coordinate for the center of the circle and invalidates only the affected area.
	 *
	 * @param centerX the x-coordinate to use, relative to the view's bounds
	 * @return this object, allowing calls to methods in this class to be chained
	 */
	public CircleView setCenterX(float centerX) {
		final float oldCenterX = this.centerX;
		if (oldCenterX != centerX) {
			this.centerX = centerX;

			// invalidate the old/new areas
			invalidate(oldCenterX, centerY, radius);
			invalidate(centerX, centerY, radius);
		}

		// clear the horizontal gravity flags
		gravity &= ~Gravity.HORIZONTAL_GRAVITY_MASK;

		return this;
	}

	/**
	 * @return the y-coordinate of the center of the circle
	 */
	public final float getCenterY() {
		return centerY;
	}

	/**
	 * Sets the y-coordinate for the center of the circle and invalidates only the affected area.
	 *
	 * @param centerY the y-coordinate to use, relative to the view's bounds
	 * @return this object, allowing calls to methods in this class to be chained
	 */
	public CircleView setCenterY(float centerY) {
		final float oldCenterY = this.centerY;
		if (oldCenterY != centerY) {
			this.centerY = centerY;

			// invalidate the old/new areas
			invalidate(centerX, oldCenterY, radius);
			invalidate(centerX, centerY, radius);
		}

		// clear the vertical gravity flags
		gravity &= ~Gravity.VERTICAL_GRAVITY_MASK;

		return this;
	}

	/**
	 * @return the radius of the circle
	 */
	public final float getRadius() {
		return radius;
	}

	/**
	 * Sets the radius of the circle and invalidates only the affected area.
	 *
	 * @param radius the radius to use
	 * @return this object, allowing calls to methods in this class to be chained
	 */
	public CircleView setRadius(float radius) {
		final float oldRadius = this.radius;
		if (oldRadius != radius) {
			this.radius = radius;

			// invalidate the old/new areas
			invalidate(centerX, centerY, oldRadius);
			if (radius > oldRadius) {
				invalidate(centerX, centerY, radius);
			}
		}

		// clear the fill gravity flags
		if ((gravity & Gravity.FILL_HORIZONTAL) == Gravity.FILL_HORIZONTAL) {
			gravity &= ~Gravity.FILL_HORIZONTAL;
		}
		if ((gravity & Gravity.FILL_VERTICAL) == Gravity.FILL_VERTICAL) {
			gravity &= ~Gravity.FILL_VERTICAL;
		}

		return this;
	}

	/**
	 * Invalidates the rectangular area that circumscribes the circle defined by {@code centerX},
	 * {@code centerY}, and {@code radius}.
	 */
	private void invalidate(float centerX, float centerY, float radius) {
		invalidate((int) (centerX - radius - HALF), (int) (centerY - radius - HALF),
				(int) (centerX + radius + HALF), (int) (centerY + radius + HALF));
	}

	/**
	 * Applies the specified {@code gravity} and {@code layoutDirection}, adjusting the alignment
	 * and size of the circle depending on the resolved {@link Gravity} flags. Also invalidates the
	 * affected area if necessary.
	 *
	 * @param gravity the {@link Gravity} the {@link Gravity} flags to use
	 */
	private void applyGravity(int gravity) {
		final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, LTR_LAYOUT_DIRECTION);

		final float oldRadius = radius;
		final float oldCenterX = centerX;
		final float oldCenterY = centerY;

		switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
			case Gravity.LEFT:
				centerX = 0.0f;
				break;
			case Gravity.CENTER_HORIZONTAL:
			case Gravity.FILL_HORIZONTAL:
				centerX = getWidth() / 2.0f;
				break;
			case Gravity.RIGHT:
				centerX = getWidth();
				break;
		}

		switch (absoluteGravity & Gravity.VERTICAL_GRAVITY_MASK) {
			case Gravity.TOP:
				centerY = 0.0f;
				break;
			case Gravity.CENTER_VERTICAL:
			case Gravity.FILL_VERTICAL:
				centerY = getHeight() / 2.0f;
				break;
			case Gravity.BOTTOM:
				centerY = getHeight();
				break;
		}

		switch (absoluteGravity & Gravity.FILL) {
			case Gravity.FILL:
				radius = Math.min(getWidth(), getHeight()) / 2.0f;
				break;
			case Gravity.FILL_HORIZONTAL:
				radius = getWidth() / 2.0f;
				break;
			case Gravity.FILL_VERTICAL:
				radius = getHeight() / 2.0f;
				break;
		}

		if (oldCenterX != centerX || oldCenterY != centerY || oldRadius != radius) {
			invalidate(oldCenterX, oldCenterY, oldRadius);
			invalidate(centerX, centerY, radius);
		}
	}
}
