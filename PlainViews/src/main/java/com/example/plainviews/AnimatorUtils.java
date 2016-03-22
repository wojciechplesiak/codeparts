package com.example.plainviews;

import com.example.plainviews.widget.CircleView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.text.TextUtils;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkNotNull;

public class AnimatorUtils {

	private static final TimeInterpolator REVEAL_INTERPOLATOR =
			PathInterpolatorCompat.create(0.0f, 0.0f, 0.2f, 1.0f);

	private static final int REVEAL_VIEW_FADE_IN_DURATION_MILLIS = 500;
	private static final int REVEAL_VIEW_FADE_OUT_DURATION_MILLIS = 300;
	private static final int REVEAL_VIEW_DISMISS_DELAY_MILLIS = 2000;

	private static final String EMPTY = "";

	public static final long ANIM_DURATION_SHORT = 266L;  // 8/30 frames long

	public static final Interpolator DECELERATE_ACCELERATE_INTERPOLATOR = new Interpolator() {
		@Override
		public float getInterpolation(float x) {
			return 0.5f + 4.0f * (x - 0.5f) * (x - 0.5f) * (x - 0.5f);
		}
	};

	public static final Property<View, Integer> BACKGROUND_ALPHA =
			new Property<View, Integer>(Integer.class, "background.alpha") {
				@Override
				public Integer get(View view) {
					return view.getBackground().getAlpha();
				}

				@Override
				public void set(View view, Integer value) {
					view.getBackground().setAlpha(value);
				}
			};

	public static final Property<ImageView, Integer> DRAWABLE_ALPHA =
			new Property<ImageView, Integer>(Integer.class, "drawable.alpha") {
				@Override
				public Integer get(ImageView view) {
					return view.getDrawable().getAlpha();
				}

				@Override
				public void set(ImageView view, Integer value) {
					view.getDrawable().setAlpha(value);
				}
			};

	public static final Property<ImageView, Integer> DRAWABLE_TINT =
			new Property<ImageView, Integer>(Integer.class, "drawable.tint") {
				@Override
				public Integer get(ImageView view) {
					return null;
				}

				@Override
				public void set(ImageView view, Integer value) {
					// Ensure the drawable is wrapped using DrawableCompat.
					final Drawable drawable = view.getDrawable();
					final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
					if (wrappedDrawable != drawable) {
						view.setImageDrawable(wrappedDrawable);
					}
					// Set the new tint value via DrawableCompat.
					DrawableCompat.setTint(wrappedDrawable, value);
				}
			};

	public static final ArgbEvaluator ARGB_EVALUATOR = new ArgbEvaluator();

	private static Method sAnimateValue;
	private static boolean sTryAnimateValue = true;


	public static void setAnimatedFraction(ValueAnimator animator, float fraction) {
		if (Utils.isLMR1OrLater()) {
			animator.setCurrentFraction(fraction);
			return;
		}

		if (sTryAnimateValue) {
			// try to set the animated fraction directly so that it isn't affected by the
			// internal animator scale or time (b/17938711)
			try {
				if (sAnimateValue == null) {
					sAnimateValue = ValueAnimator.class
							.getDeclaredMethod("animateValue", float.class);
					sAnimateValue.setAccessible(true);
				}

				sAnimateValue.invoke(animator, fraction);
				return;
			} catch (Exception e) {
				// something went wrong, don't try that again
				LogUtils.e("Unable to use animateValue directly", e);
				sTryAnimateValue = false;
			}
		}

		// if that doesn't work then just fall back to setting the current play time
		animator.setCurrentPlayTime(Math.round(fraction * animator.getDuration()));
	}

	public static void reverse(ValueAnimator... animators) {
		for (ValueAnimator animator : animators) {
			final float fraction = animator.getAnimatedFraction();
			if (fraction > 0.0f) {
				animator.reverse();
				setAnimatedFraction(animator, 1.0f - fraction);
			}
		}
	}

	public static void cancel(ValueAnimator... animators) {
		for (ValueAnimator animator : animators) {
			animator.cancel();
		}
	}

	public static ValueAnimator getScaleAnimator(View view, float... values) {
		return ObjectAnimator.ofPropertyValuesHolder(view,
				PropertyValuesHolder.ofFloat(View.SCALE_X, values),
				PropertyValuesHolder.ofFloat(View.SCALE_Y, values));
	}

	/**
	 * Helps to create a nice reveal animation with optional alert text.
	 * @param context
	 * @param source
	 * @param revealColor
	 * @return a builder for Reveal-Alert animation.
	 */
	public static RevealBuilder createReveal(@NonNull final Context context, final View
			source, final int revealColor) {
		return new RevealBuilder(context, source, revealColor);
	}






	/**
	 * Builder for Reveal-Alert animation.
	 */
	public static class RevealBuilder {

		private static int NO_SOURCE_OFFSET = 5;

		private Context context;
		private ViewGroup containerView;
		private View source;
		private int revealColor;
		private String title;
		private String info;
		private String canceledMessage;
		private boolean cancelable;
		private boolean dismissible;
		private OnRevealEndListener onRevealEndListener;

		private final Handler mHandler = new Handler();

		private final View.OnClickListener onClickAbsorber = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		};

		private RevealBuilder(@NonNull final Context context, final View source, final
		int revealColor) {
			this.context = checkNotNull(context);
			this.source = source;
			this.revealColor = revealColor;
		}

		/**
		 *
		 * @param containerView
		 * @return
		 */
		public RevealBuilder fitInContainer(@NonNull final ViewGroup containerView) {
			this.containerView = checkNotNull(containerView);
			return this;
		}

		/**
		 * Provided title and/or info message will be displayed after reveal animation ends and
		 * will stay on a screen for a little while.
		 * @param title
		 * @param info
		 * @return
		 */
		public RevealBuilder withText(@Nullable final String title, @Nullable final String
				info) {
			this.title = title;
			this.info = info;
			return this;
		}

		/**
		 * Set Glimpse cancellable, which will displayed custom canceled message.
		 *
		 * @param canceledMessage message to be displayed on Glimpse when it's cancelled by user
		 */
		public RevealBuilder setCancelableWithMessage(String canceledMessage) {
			this.cancelable = true;
			this.canceledMessage = canceledMessage;
			return this;
		}

		public RevealBuilder setCancelable(boolean cancelable) {
			this.cancelable = cancelable;
			return this;
		}

		public RevealBuilder setDismissible(boolean dismissible) {
			this.dismissible = dismissible;
			return this;
		}

		public RevealBuilder withRevealEndListener(OnRevealEndListener onRevealEndListener) {
			this.onRevealEndListener = onRevealEndListener;
			return this;
		}

		public Animator build() {
			if (containerView == null) {
				containerView = obtainContainerView();
			}

			final Rect sourceBounds = prepareSourceBounds();

			final int centerX = sourceBounds.centerX();
			final int centerY = sourceBounds.centerY();

			final int xMax = Math.max(centerX, containerView.getWidth() - centerX);
			final int yMax = Math.max(centerY, containerView.getHeight() - centerY);

			final float startRadius = Math.max(sourceBounds.width(), sourceBounds.height()) / 2.0f;
			final float endRadius = (float) Math.sqrt(xMax * xMax + yMax * yMax);

			final View alertView = prepareAlertView();
			final CircleView revealView = prepareRevealView(centerX, centerY);

			final Animator revealAnimator = prepareRevealAnimator(revealView, alertView,
					startRadius, endRadius);

			final ValueAnimator fadeAnimator = prepareFadeAnimation(revealView);

			final AnimatorSet alertAnimator = new AnimatorSet();
			alertAnimator.play(revealAnimator).before(fadeAnimator);
			alertAnimator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animator) {
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							prepareFadeAnimation(alertView).start();
						}
					}, REVEAL_VIEW_DISMISS_DELAY_MILLIS);
				}
			});

			if (cancelable) {
				cancelViewsOnClick(revealView, alertView, alertAnimator);
			}

			if (dismissible) {
				dismissViewOnClick(alertView, alertAnimator);
			}

			return alertAnimator;
		}

		private Rect prepareSourceBounds() {
			final Rect sourceBounds;
			if (source != null) {
				sourceBounds = new Rect(0, 0, source.getHeight(), source.getWidth());
				containerView.offsetDescendantRectToMyCoords(source, sourceBounds);
			} else {
				sourceBounds = new Rect(containerView.getWidth() / 2 - NO_SOURCE_OFFSET,
						containerView.getHeight() / 2 - NO_SOURCE_OFFSET, containerView.getWidth
						() / 2 + NO_SOURCE_OFFSET, containerView.getHeight() / 2 +
						NO_SOURCE_OFFSET);
			}
			return sourceBounds;
		}

		private View prepareAlertView() {
			final View alertView = LayoutInflater.from(context).inflate(R.layout.one_reveal_view,
					null);
			alertView.setBackgroundColor(revealColor);
			alertView.setOnClickListener(onClickAbsorber);
			containerView.addView(alertView);
			return alertView;
		}

		private CircleView prepareRevealView(int centerX, int centerY) {
			final CircleView revealView = new CircleView(context)
					.setCenterX(centerX)
					.setCenterY(centerY)
					.setFillColor(revealColor);
			containerView.addView(revealView);
			return revealView;
		}

		private Animator prepareRevealAnimator(final CircleView revealView, final View alertView, float
				startRadius, float endRadius) {
			final Animator revealAnimator = ObjectAnimator.ofFloat(revealView, CircleView
					.RADIUS, startRadius, endRadius);
			revealAnimator.setDuration(REVEAL_VIEW_FADE_IN_DURATION_MILLIS);
			revealAnimator.setInterpolator(REVEAL_INTERPOLATOR);
			revealAnimator.addListener(new AnimatorListenerAdapter() {

				@Override
				public void onAnimationEnd(Animator animator) {
					alertView.setVisibility(View.VISIBLE);
					if (!TextUtils.isEmpty(title)) {
						setText(alertView, R.id.reveal_view_title, title);
					}
					if (!TextUtils.isEmpty(info)) {
						setText(alertView, R.id.reveal_view_info, info);
					}
					if (onRevealEndListener != null) {
						onRevealEndListener.onRevealAnimationEnded();
					}
				}
			});
			return revealAnimator;
		}

		private void cancelViewsOnClick(final View revealView, final View alertView, final
		AnimatorSet alertAnimator) {
			revealView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					alertAnimator.cancel();

					setText(alertView, R.id.reveal_view_title, getCancelledMessage());
					setText(alertView, R.id.reveal_view_info, EMPTY);
					revealView.setVisibility(View.GONE);
					alertView.setVisibility(View.VISIBLE);
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							alertAnimator.play(prepareFadeAnimation(alertView));
							alertAnimator.start();
						}
					}, REVEAL_VIEW_DISMISS_DELAY_MILLIS);
					containerView.removeView(revealView);
				}

				private String getCancelledMessage() {
					return TextUtils.isEmpty(canceledMessage) ? context.getString(R.string
							.canceled) : canceledMessage;
				}
			});
		}

		private void dismissViewOnClick(final View view, final AnimatorSet alertAnimator) {
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					alertAnimator.cancel();
					alertAnimator.play(prepareFadeAnimation(view));
					alertAnimator.start();
				}
			});
		}

		private ViewGroup obtainContainerView() {
			if (context instanceof Activity) {
				return (ViewGroup) ((Activity) context).findViewById(android.R.id.content);
			}
			throw new IllegalArgumentException("Activity context required. To use RevealBuilder " +
					"without providing 'containerView' through #fitInContainer(ViewGroup) method" +
					" you must provide Activity context.");
		}

		private ValueAnimator prepareFadeAnimation(final View view) {
			final ValueAnimator fadeAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f);
			fadeAnimator.setDuration(REVEAL_VIEW_FADE_OUT_DURATION_MILLIS);
			fadeAnimator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					containerView.removeView(view);
				}
			});
			return fadeAnimator;
		}

		private void setText(View parentView, int textViewId, String text) {
			TextView textView = ((TextView) parentView.findViewById(textViewId));
			textView.setText(text);
			textView.setVisibility(View.VISIBLE);
		}
	}

	interface OnRevealEndListener {
		void onRevealAnimationEnded();
	}
}
