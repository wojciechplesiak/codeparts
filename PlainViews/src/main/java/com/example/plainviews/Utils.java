/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.plainviews;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;

import java.util.Calendar;
import java.util.TimeZone;

public class Utils {

    public static void enforceMainLooper() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalAccessError("May only call from main thread.");
        }
    }

    public static void enforceNotMainLooper() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IllegalAccessError("May not call from main thread.");
        }
    }

    /**
     * @return {@code true} if the device is prior to {@link Build.VERSION_CODES#LOLLIPOP}
     */
    public static boolean isPreL() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * @return {@code true} if the device is {@link Build.VERSION_CODES#LOLLIPOP} or
     *      {@link Build.VERSION_CODES#LOLLIPOP_MR1}
     */
    public static boolean isLOrLMR1() {
        final int sdkInt = Build.VERSION.SDK_INT;
        return sdkInt == Build.VERSION_CODES.LOLLIPOP || sdkInt == Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    /**
     * @return {@code true} if the device is {@link Build.VERSION_CODES#LOLLIPOP} or later
     */
    public static boolean isLOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * @return {@code true} if the device is {@link Build.VERSION_CODES#LOLLIPOP_MR1} or later
     */
    public static boolean isLMR1OrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
    }

    /**
     * @return {@code true} if the device is {@link Build.VERSION_CODES#M} or later
     */
    public static boolean isMOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static long getTimeNow() {
        return SystemClock.elapsedRealtime();
    }

    /** Setup to find out when the quarter-hour changes (e.g. Kathmandu is GMT+5:45) **/
    public static long getAlarmOnQuarterHour() {
        final Calendar calendarInstance = Calendar.getInstance();
        final long now = System.currentTimeMillis();
        return getAlarmOnQuarterHour(calendarInstance, now);
    }

    static long getAlarmOnQuarterHour(Calendar calendar, long now) {
        //  Set 1 second to ensure quarter-hour threshold passed.
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 0);
        int minute = calendar.get(Calendar.MINUTE);
        calendar.add(Calendar.MINUTE, 15 - (minute % 15));
        long alarmOnQuarterHour = calendar.getTimeInMillis();

        // Verify that alarmOnQuarterHour is within the next 15 minutes
        long delta = alarmOnQuarterHour - now;
        if (0 >= delta || delta > 901000) {
            // Something went wrong in the calculation, schedule something that is
            // about 15 minutes. Next time , it will align with the 15 minutes border.
            alarmOnQuarterHour = now + 901000;
        }
        return alarmOnQuarterHour;
    }

    // Setup a thread that starts at the quarter-hour plus one second. The extra second is added to
    // ensure dates have changed.
    public static void setQuarterHourUpdater(Handler handler, Runnable runnable) {
        String timezone = TimeZone.getDefault().getID();
        if (handler == null || runnable == null || timezone == null) {
            return;
        }
        long runInMillis = getAlarmOnQuarterHour() - System.currentTimeMillis();
        // Ensure the delay is at least one second.
        if (runInMillis < 1000) {
            runInMillis = 1000;
        }
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, runInMillis);
    }

    // Stop the quarter-hour update thread
    public static void cancelQuarterHourUpdater(Handler handler, Runnable runnable) {
        if (handler == null || runnable == null) {
            return;
        }
        handler.removeCallbacks(runnable);
    }

    /**
     * For screensavers to dim the lights if necessary.
     */
    public static void dimClockView(boolean dim, View clockView) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setColorFilter(new PorterDuffColorFilter(
                        (dim ? 0x40FFFFFF : 0xC0FFFFFF),
                PorterDuff.Mode.MULTIPLY));
        clockView.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
    }

    // ---------------------------------------------------------------------------------------------
    /**
     * Returns a color integer associated with a particular resource ID.
     *
     * @param context
     * @param id
     * @return A single color value in the form 0xAARRGGBB or #Color.BLACK when context is null.
     * @throws Resources.NotFoundException if the given ID does not exist.
     */
    public static int getColor(Context context, int id) throws Resources.NotFoundException {
        if (context == null) {
            return Color.BLACK;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(id, context.getTheme());
        } else {
            return context.getResources().getColor(id);
        }
    }
}
