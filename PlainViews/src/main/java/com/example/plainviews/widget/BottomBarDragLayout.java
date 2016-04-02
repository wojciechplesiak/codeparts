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
 * Custom layout used to slide in/out a BottomBar out of bottom of the screen. BottomBar view id
 * should be provided in xml attribute #bottomBarId.
 */
public class BottomBarDragLayout extends RelativeLayout {

	private static final String TAG = BottomBarDragLayout.class.getSimpleName();

	/**
	 * Default velocity to reach after which BottomBar will be expanded/collapsed automatically.
	 */
	private static final int EXPAND_VELOCITY = 800;

	/**
	 * If #autoDismiss is enabled this represents the default time after which BottomBar will be
	 * automatically collapsed.
	 */
	private static final int DEFAULT_DISMISS_DELAY_MS = 5000;

	/**
	 * Normal sensitivity level. It's a multiplier for how sensitive the view drag helper should
	 * be about detecting the start of a drag. Larger values are more sensitive. 1.0f is normal.
	 */
	private static final float DRAG_SENSITIVITY = 1.0f;
	private static final int NOT_SET = -1;


	/**
	 * Flag indicating the expanded state of the BottomBar.
	 */
	private boolean isExpanded;

	/**
	 * Flag indicating if auto-dismiss is already scheduled and waiting for execution.
	 */
	private boolean autoDismissScheduled;

	/**
	 * Current BottomBar's top position in pixels (counted from the top of the screen)
	 */
	private int currentTop;

	/**
	 * Maximum top position in pixels. For expanded state.
	 */
	private int maxTop = NOT_SET;

	/**
	 * Minimum top position in pixels. For collapsed state.
	 */
	private int minTop = NOT_SET;


	/**
	 * Flag indicating if BottomBar should be collapsed automatically after user stopped
	 * interacting with it.
	 */
	private boolean autoDismiss;

	/**
	 * Time after which BottomBar will be collapsed. Used only if #autoDismiss is enabled.
	 */
	private int dismissAfter;

	/**
	 * Velocity to reach after which BottomBar will be expanded/collapsed automatically.
	 */
	private int expandVelocity;

	/**
	 * A layout id of the BottomBar view.
	 */
	private int bottomBarId;

	/**
	 * Height of a visible part of a BottomBar. If not set by xml attribute height of the whole
	 * #bottomBarId view will be used.
	 */
	private int bottomBarHeight;

	/**
	 * When #showBottomBar or #hideBottomBar were called before BottomBar got drawn on a screen,
	 * this flag will make it expand/collapse when it's ready. True - expand, False - collapse,
	 * Null - do nothing.
	 */
	private Boolean expandWhenViewReady;


	/**
	 * View which is being dragged inside this layout - BottomBar view.
	 */
	private View dragView;

	/**
	 * Helper class allowing a user to drag and reposition views within their parent ViewGroup.
	 */
	private final ViewDragHelper dragHelper;

	/**
	 * Handler used to schedule BottomBar's automatic collapsing.
	 */
	private Handler dismissHandler;

	public BottomBarDragLayout(Context context) {
		this(context, null);
	}

	public BottomBarDragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BottomBarDragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		dragHelper = ViewDragHelper.create(this, DRAG_SENSITIVITY, new DragHelperCallback());

		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BottomBarDragLayout,
				defStyle, 0);

		autoDismiss = a.getBoolean(R.styleable.BottomBarDragLayout_autoDismiss, false);
		dismissAfter = a.getInt(R.styleable.BottomBarDragLayout_dismissAfter,
				DEFAULT_DISMISS_DELAY_MS);
		expandVelocity = a.getInt(R.styleable.BottomBarDragLayout_expandVelocity,
				EXPAND_VELOCITY);
		bottomBarId = a.getResourceId(R.styleable.BottomBarDragLayout_bottomBarId, NO_ID);
		bottomBarHeight = a.getDimensionPixelSize(R.styleable.BottomBarDragLayout_bottomBarHeight,
				NOT_SET);

		a.recycle();

		if (bottomBarId == NO_ID) {
			throw new IllegalStateException("BottomBar id needs to be provided through attribute");
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return dragHelper.shouldInterceptTouchEvent(event) || super.onInterceptTouchEvent(event);
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
		dragView = findViewById(bottomBarId);
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

	/**
	 * Informs that user interacted with BottomBar in other way than dragging it. Should be
	 * called when #autoDismiss is enabled to prevent automatic BottomBar collapse while user is
	 * clicking/interacting with it's internal views. This will reschedule auto-dismiss.
	 */
	public void onUserInteraction() {
		Log.i(TAG, "onUserInteraction()");
		scheduleOrCancelAutoDismiss();
	}

	private void init() {
		Log.i(TAG, "init()");
		setupMaxTop(dragView);
		setupMinTop();
		if (bottomBarHeight <= NOT_SET) {
			bottomBarHeight = dragView.getHeight();
		}
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
		Log.i(TAG, "Toggle bottom bar. Expand: " + expand);
		final int finalTop = expand ? maxTop : minTop;

		if (finalTop > 0) {
			setExpanded(expand);
			if (currentTop != finalTop) {
				dragHelper.smoothSlideViewTo(dragView, 0, finalTop);
				ViewCompat.postInvalidateOnAnimation(BottomBarDragLayout.this);
			} else {
				scheduleOrCancelAutoDismiss();
			}
		} else {
			Log.i(TAG, "View not ready yet. Bottom bar will be toggled when ready");
			expandWhenViewReady = expand;
		}
	}

	private void scheduleOrCancelAutoDismiss() {
		boolean expand = isExpanded();
		Log.d(TAG, "scheduleOrCancelAutoDismiss: " + expand);
		if (expand && autoDismiss) {
			if (autoDismissScheduled) {
				cancelAutoDismiss();
			}
			scheduleAutoDismiss();
		} else if (autoDismissScheduled) {
			cancelAutoDismiss();
		}

	}

	private void scheduleAutoDismiss() {
		Log.d(TAG, "scheduleAutoDismiss()");
		if (dismissHandler == null) {
			dismissHandler = new Handler();
		}
		dismissHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "Bottom bar auto dismiss fired..");
				hideBottomBar();
			}
		}, dismissAfter);
		autoDismissScheduled = true;
	}

	private void cancelAutoDismiss() {
		Log.d(TAG, "cancelAutoDismiss()");
		if (dismissHandler != null) {
			dismissHandler.removeCallbacksAndMessages(null);
		}
		autoDismissScheduled = false;
	}

	private void setExpanded(boolean expanded) {
		Log.d(TAG, "setExpanded = " + expanded);
		synchronized (BottomBarDragLayout.class) {
			isExpanded = expanded;
		}
	}

	private boolean isExpanded() {
		synchronized (BottomBarDragLayout.class) {
			return isExpanded;
		}
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

			final int finalTop = expand ? maxTop : minTop;

			if (dragHelper.settleCapturedViewAt(0, finalTop)) {
				ViewCompat.postInvalidateOnAnimation(BottomBarDragLayout.this);
			}

			setExpanded(expand);
			Log.w(TAG, "onViewReleased isExpanded = " + isExpanded);
		}

		@Override
		public void onViewDragStateChanged(int state) {
			super.onViewDragStateChanged(state);
			switch (state) {
				case ViewDragHelper.STATE_DRAGGING:
				case ViewDragHelper.STATE_SETTLING:
					if (autoDismissScheduled) {
						cancelAutoDismiss();
					}
					break;

				case ViewDragHelper.STATE_IDLE:
					Log.w(TAG, "onViewDragStateChanged isExpanded = " + isExpanded);
					scheduleOrCancelAutoDismiss();
					break;
			}
		}
	}
}
