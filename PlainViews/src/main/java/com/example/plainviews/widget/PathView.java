package com.example.plainviews.widget;

import com.example.plainviews.R;
import com.example.plainviews.Utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PathView extends View {

	private final static float FADE_FACTOR = 10.0f;
	private final static float PHASE_MIN = 0.0f;
	private final static float PHASE_MAX = 1.0f;
	private final static float ALPHA_MAX = 255.0f;
	private final static int REVEAL_DURATION = 2000;
	private final static int TEXT_SIZE_DEFAULT = 80;
	private final static int STROKE_WIDTH_DEFAULT = 2;
	private final static String EMPTY = "";

	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private List<PathItem> paths = new ArrayList<>(0);

	private int duration;
	private int pathColor;
	private float fadeFactor;
	private float textSize;
	private float strokeWidth;


	private float phase = 1.0f;

	private final Object svgLock = new Object();

	public PathView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PathView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PathView,
				defStyleAttr, 0);

		pathColor = a.getColor(R.styleable.PathView_pathColor, Utils.getColor(context, R.color
				.white));
		textSize = a.getDimensionPixelSize(R.styleable.PathView_textSize, TEXT_SIZE_DEFAULT);
		strokeWidth = a.getDimensionPixelSize(R.styleable.PathView_strokeWidth,
				STROKE_WIDTH_DEFAULT);
		duration = a.getInt(R.styleable.PathView_duration, REVEAL_DURATION);
		fadeFactor = a.getFloat(R.styleable.PathView_fadeFactor, FADE_FACTOR);

		a.recycle();

		setupPaint();
	}

	private void setupPaint() {
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(strokeWidth);
		paint.setColor(pathColor);
		paint.setTextSize(textSize);
	}


	private void updatePathsPhaseLocked() {
		final int count = paths.size();
		for (int i = 0; i < count; i++) {
			PathItem pathItem = paths.get(i);
			pathItem.path.reset();
			pathItem.measure.getSegment(0.0f, pathItem.length * phase, pathItem.path, true);
			// Required only for Android 4.4 and earlier
			pathItem.path.rLineTo(0.0f, 0.0f);
		}
	}

	/** Sets the current phase of the path.
	 *
	 * Note: do not remove this method although IDE marks is as not used. It is being called by
	 * animation. */
	public void setPhase(float phase) {
		this.phase = phase;
		synchronized (svgLock) {
			updatePathsPhaseLocked();
		}
		invalidate();
	}

	@Override
	protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		synchronized (svgLock) {
			if (paths.isEmpty()) {
				setupMockedPathsForViewport(w - getPaddingLeft() - getPaddingRight(),
						h - getPaddingTop() - getPaddingBottom());
				updatePathsPhaseLocked();
			}
		}
	}

	private void setupMockedPathsForViewport(final int width, final int height) {
		// TODO: (w.plesiak 2016-04-03) remove and add methods to set a text from outside
		paths.clear();

		Path path = new Path();
		path.addCircle(width / 2 + 70, 300, 120, Path.Direction.CW);
		paths.add(new PathItem(path, paint));
		paths.addAll(getStringPathItems("1CF", width / 2, 330));
		paths.addAll(getStringPathItems("Having fun with paths", 50, 700));
		paths.addAll(getStringPathItems("and text :)", 50, 830));
	}

	/**
	 * Sets paths to be drawn.
	 * @param rawPaths
	 */
	public void setPaths(List<Path> rawPaths) {
		paths.clear();
		for (Path path : rawPaths) {
			this.paths.add(new PathItem(path, paint));
		}
	}

	public List<Path> getStringPaths(String str, final int x, final int y) {
		List<Path> paths = new ArrayList<>();
		String[] letters = str.split(EMPTY);
		Region region = new Region();
		int offset = 0;
		for (int i = 1; i < letters.length; i++) {
			android.graphics.Path path = new android.graphics.Path();
			paint.getTextPath(letters[i], 0, 1, x + offset, y, path);
			region.setPath(path, PathItem.MAX_CLIP);
			offset += region.getBounds().width() + 20;
			paths.add(path);
		}
		return paths;
	}

	private List<PathItem> getStringPathItems(String str, final int x, final int y) {
		List<Path> paths = getStringPaths(str, x, y);
		List<PathItem> pathItems = new ArrayList<>();
		for (Path path : paths) {
			pathItems.add(new PathItem(path, paint));
		}
		return pathItems;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		synchronized (svgLock) {
			canvas.save();
			final int count = paths.size();
			for (int i = 0; i < count; i++) {
				PathItem pathItem = paths.get(i);

				// We use the fade factor to speed up the alpha animation
				int alpha = (int) (Math.min(phase * fadeFactor, PHASE_MAX) * ALPHA_MAX);
				pathItem.paint.setAlpha(alpha);

				canvas.drawPath(pathItem.path, pathItem.paint);
			}
			canvas.restore();
		}
	}

	public void reveal() {
		ObjectAnimator svgAnimator = ObjectAnimator.ofFloat(this, "phase", PHASE_MIN, PHASE_MAX);
		svgAnimator.setDuration(duration);
		svgAnimator.start();
	}

	public static class PathItem {
		public static final Region MAX_CLIP = new Region(
				Integer.MIN_VALUE, Integer.MIN_VALUE,
				Integer.MAX_VALUE, Integer.MAX_VALUE);

		final Path path;
		final Paint paint;
		final float length;
		final PathMeasure measure;

		PathItem(Path path, Paint paint) {
			this.path = path;
			this.paint = paint;

			measure = new PathMeasure(path, false);
			this.length = measure.getLength();
		}
	}
}
