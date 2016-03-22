package com.example.plainviews.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
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
public class DullView extends View {

	private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private List<DullPath> mPathsDull = new ArrayList<>(0);

	private int mSvgResource;
	private float mPhase = 1.0f;
	private float mFadeFactor = 10.0f;
	private int mDuration = 2000;

	private final Object mSvgLock = new Object();
	private Thread mLoader;
	private ObjectAnimator mSvgAnimator;

	public DullView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DullView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(2.0f);
		mPaint.setColor(0xffffffff);
		mPaint.setTextSize(80.0f);
	}

	private void updatePathsPhaseLocked() {
		final int count = mPathsDull.size();
		for (int i = 0; i < count; i++) {
			DullPath dullPath = mPathsDull.get(i);
			dullPath.path.reset();
			dullPath.measure.getSegment(0.0f, dullPath.length * mPhase, dullPath.path, true);
			// Required only for Android 4.4 and earlier
			dullPath.path.rLineTo(0.0f, 0.0f);
		}
	}

	public float getPhase() {
		return mPhase;
	}

	public void setPhase(float phase) {
		mPhase = phase;
		synchronized (mSvgLock) {
			updatePathsPhaseLocked();
		}
		invalidate();
	}

	public int getSvgResource() {
		return mSvgResource;
	}

	public void setSvgResource(int svgResource) {
		mSvgResource = svgResource;
	}

	@Override
	protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		synchronized (mSvgLock) {
			setupPathsForViewport(w - getPaddingLeft() - getPaddingRight(),
					h - getPaddingTop() - getPaddingBottom());
			updatePathsPhaseLocked();
		}
	}

	private void setupPathsForViewport(final int width, final int height) {
		mPathsDull.clear();

		Path path = new Path();
		path.addCircle(width / 2 + 70, 300, 120, Path.Direction.CW);
		mPathsDull.add(new DullPath(path, mPaint));
		mPathsDull.addAll(getStringPaths("1CF", width / 2, 330));
		mPathsDull.addAll(getStringPaths("Having fun with paths", 50, 700));
		mPathsDull.addAll(getStringPaths("and text :)", 50, 830));
	}

	private List<DullPath> getStringPaths(String str, final int x, final int y) {
		List<DullPath> paths = new ArrayList<>();
		String[] letters = str.split("");
		Region region = new Region();
		int offset = 0;
		for (int i = 1; i < letters.length; i++) {
			Path path = new Path();
			mPaint.getTextPath(letters[i], 0, 1, x + offset, y, path);
			paths.add(new DullPath(path, mPaint));
			region.setPath(path, DullPath.MAX_CLIP);
			offset += region.getBounds().width() + 20;
		}
		return paths;
	}



	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		synchronized (mSvgLock) {
			canvas.save();
			final int count = mPathsDull.size();
			for (int i = 0; i < count; i++) {
				DullPath dullPath = mPathsDull.get(i);

				// We use the fade factor to speed up the alpha animation
				int alpha = (int) (Math.min(mPhase * mFadeFactor, 1.0f) * 255.0f);
				dullPath.paint.setAlpha(alpha);

				canvas.drawPath(dullPath.path, dullPath.paint);
			}
			canvas.restore();
		}
	}

	public void reveal() {
		mSvgAnimator = ObjectAnimator.ofFloat(this, "phase", 0.0f, 1.0f);
		mSvgAnimator.setDuration(mDuration);
		mSvgAnimator.start();
	}

	public static class DullPath {
		public static final Region MAX_CLIP = new Region(
				Integer.MIN_VALUE, Integer.MIN_VALUE,
				Integer.MAX_VALUE, Integer.MAX_VALUE);

		final Path path;
		final Paint paint;
		final float length;
		final PathMeasure measure;

		DullPath(Path path, Paint paint) {
			this.path = path;
			this.paint = paint;

			measure = new PathMeasure(path, false);
			this.length = measure.getLength();
		}
	}
}
