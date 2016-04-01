package com.example.plainviews.widget;

import com.example.plainviews.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

/**
 * BottomBar widget.
 */
public class BottomBarDragLayout extends RelativeLayout {

	private static final String TAG = BottomBarDragLayout.class.getSimpleName();

	private static final int EXPAND_VELOCITY = 800;
	private static final int DEFAULT_DISMISS_DELAY_MS = 2000;
	private static final int DEFAULT_BAR_HEIGHT_PX = 200;
	private static final int NOT_SET = -1;

	private View dragView;

	private boolean isExpanded;
	private boolean autoDismissScheduled;
	private int currentTop;
	private int maxTop = NOT_SET;
	private int minTop = NOT_SET;

	private boolean autoDismiss;
	private int dismissAfter;
	private int expandVelocity;
	private int bottomBarId;
	private int bottomBarHeight;

	private Boolean expandWhenViewReady;

	private final ViewDragHelper dragHelper;

	private Handler dismissHandler;

	public BottomBarDragLayout(Context context) {
		this(context, null);
	}

	public BottomBarDragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BottomBarDragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		dragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());

		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BottomBarDragLayout,
				defStyle, 0);

		autoDismiss = a.getBoolean(R.styleable.BottomBarDragLayout_autoDismiss, false);
		dismissAfter = a.getInt(R.styleable.BottomBarDragLayout_dismissAfter,
				DEFAULT_DISMISS_DELAY_MS);
		expandVelocity = a.getInt(R.styleable.BottomBarDragLayout_expandVelocity,
				EXPAND_VELOCITY);
		bottomBarId = a.getResourceId(R.styleable.BottomBarDragLayout_bottomBarId, NO_ID);
		bottomBarHeight = a.getDimensionPixelSize(R.styleable.BottomBarDragLayout_bottomBarHeight,
				DEFAULT_BAR_HEIGHT_PX);

		a.recycle();

		if (bottomBarId == NO_ID) {
			throw new IllegalStateException("BottomBar id needs to be provided through attribute");
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (dragHelper.shouldInterceptTouchEvent(event)) {
			return true;
		}
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dragHelper.processTouchEvent(event);
		return true;
	}

	@Override
	public void computeScroll() {
		if (dragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		dragView = findViewById(R.id.bottom_bar);
		final ViewTreeObserver observer = dragView.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				dragView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				init();
			}
		});
	}

	/**
	 * Shows bottom bar with animation.
	 */
	public void showBottomBar() {
		toggle(true);
	}

	/**
	 * Hides bottom bar with animation.
	 */
	public void hideBottomBar() {
		toggle(false);
	}

	private void init() {
		setupMaxTop(dragView);
		setupMinTop();
		if (expandWhenViewReady != null) {
			toggle(expandWhenViewReady);
		}
	}

	private int setupMaxTop(View child) {
		if (maxTop < 0) {
			maxTop = getHeight() - child.getHeight();
		}
		return maxTop;
	}

	private int setupMinTop() {
		if (minTop < 0) {
			minTop = maxTop + bottomBarHeight;
		}
		return minTop;
	}

	private void toggle(boolean expand) {
		Log.i(TAG, "expand: " + expand);
		final int settleDestY = expand ? maxTop : minTop;

//		if (currentTop == settleDestY) {
//			Log.d(TAG, "Already on it's place");
//			return;
//		}

		if (settleDestY > 0) {
			dragHelper.smoothSlideViewTo(dragView, 0, settleDestY);
			ViewCompat.postInvalidateOnAnimation(BottomBarDragLayout.this);
			isExpanded = expand;
		} else {
			Log.i(TAG, "View not ready yet. Bottom bar will be toggled when ready");
			expandWhenViewReady = expand;
		}

		scheduleOrCancelAutoDismiss(expand);
	}

	private void scheduleOrCancelAutoDismiss(boolean expand) {
		if (expand && autoDismiss) {
			if (autoDismissScheduled) {
				cancelAutoDismiss();
			}
			scheduleAutoDismiss();
		} else {
			cancelAutoDismiss();
		}
	}

	private void scheduleAutoDismiss() {
		if (dismissHandler == null) {
			dismissHandler = new Handler();
		}
		dismissHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "Auto hiding bottom bar");
				hideBottomBar();
			}
		}, dismissAfter);
		autoDismissScheduled = true;
	}

	private void cancelAutoDismiss() {
		if (dismissHandler != null) {
			dismissHandler.removeCallbacksAndMessages(null);
		}
		autoDismissScheduled = false;
	}

	private class DragHelperCallback extends ViewDragHelper.Callback {

		@Override
		public boolean tryCaptureView(View view, int i) {
			return (view.getId() == bottomBarId);
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			return Math.min(Math.max(top, maxTop), minTop);
		}

		@Override
		public int getViewVerticalDragRange(View child) {
			return maxTop;
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			currentTop = top;
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if (currentTop == minTop || currentTop == maxTop) {
				return;
			}

			int deltaTop = currentTop - maxTop;

			boolean expand = false;
			if (yvel < -expandVelocity) {
				expand = true;
			} else if (yvel > expandVelocity) {
				expand = false;
			} else if (deltaTop < bottomBarHeight / 2) {
				expand = true;
			}

			final int settleDestY = expand ? maxTop : minTop;

			if (dragHelper.settleCapturedViewAt(0, settleDestY)) {
				ViewCompat.postInvalidateOnAnimation(BottomBarDragLayout.this);
			}

			isExpanded = expand;
		}

		@Override
		public void onViewDragStateChanged(int state) {
			super.onViewDragStateChanged(state);
			switch (state) {
				case ViewDragHelper.STATE_DRAGGING:
				case ViewDragHelper.STATE_SETTLING:
					cancelAutoDismiss();
					break;

				case ViewDragHelper.STATE_IDLE:
					scheduleOrCancelAutoDismiss(isExpanded);
					break;
			}
		}
	}
}
