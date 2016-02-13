/*
 * Copyright (C) 2015 The Android Open Source Project
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

import com.example.plainviews.widget.EmptyViewController;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment that displays a list of alarm time and allows interaction with them.
 */
public final class AlarmClockFragment extends DeskClockFragment {

    // This extra is used when receiving an intent to create an alarm, but no alarm details
    // have been passed in, so the alarm page should start the process of creating a new alarm.
//    public static final String ALARM_CREATE_NEW_INTENT_EXTRA = "deskclock.create.new";

    // This extra is used when receiving an intent to scroll to specific alarm. If alarm
    // can not be found, and toast message will pop up that the alarm has be deleted.
//    public static final String SCROLL_TO_ALARM_INTENT_EXTRA = "deskclock.scroll.to.alarm";

    // Views
    private ViewGroup mMainLayout;
//    private RecyclerView mRecyclerView;

    // Data
//    private long mScrollToAlarmId = Alarm.INVALID_ID;
//    private Loader mCursorLoader = null;

    // Controllers
//    private AlarmTimeAdapter mAlarmTimeAdapter;
//    private AlarmUpdateHandler mAlarmUpdateHandler;
    private EmptyViewController mEmptyViewController;
//    private AlarmTimeClickHandler mAlarmTimeClickHandler;
//    private LinearLayoutManager mLayoutManager;

//    @Override
//    public void processTimeSet(int hourOfDay, int minute) {
//        mAlarmTimeClickHandler.processTimeSet(hourOfDay, minute);
//    }

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
//        mCursorLoader = getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.alarm_clock, container, false);

//        mRecyclerView = (RecyclerView) v.findViewById(R.id.alarms_recycler_view);
//        mLayoutManager = new LinearLayoutManager(getActivity());
//        mRecyclerView.setLayoutManager(mLayoutManager);

        mMainLayout = (ViewGroup) v.findViewById(R.id.main);

//        mAlarmUpdateHandler = new AlarmUpdateHandler(getActivity(), this,
//                (ViewGroup) v.findViewById(R.id.undo_frame));
        mEmptyViewController = new EmptyViewController(mMainLayout, v.findViewById(R.id
                .alarms_dummy_view), v.findViewById(R.id.alarms_empty_view));
        mEmptyViewController.setEmpty(true);
//        mAlarmTimeClickHandler = new AlarmTimeClickHandler(this, savedState, mAlarmUpdateHandler,
//                this);
//        mAlarmTimeAdapter = new AlarmTimeAdapter(getActivity(), savedState,
//                mAlarmTimeClickHandler, this);
//        mRecyclerView.setAdapter(mAlarmTimeAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        final DeskClock activity = (DeskClock) getActivity();
        if (activity.getSelectedTab() == DeskClock.ALARM_TAB_INDEX) {
            setFabAppearance();
            setLeftRightButtonAppearance();
        }

//        // Check if another app asked us to create a blank new alarm.
//        final Intent intent = getActivity().getIntent();
//        if (intent.hasExtra(ALARM_CREATE_NEW_INTENT_EXTRA)) {
//            if (intent.getBooleanExtra(ALARM_CREATE_NEW_INTENT_EXTRA, false)) {
//                // An external app asked us to create a blank alarm.
//                startCreatingAlarm();
//            }
//
//            // Remove the CREATE_NEW extra now that we've processed it.
//            intent.removeExtra(ALARM_CREATE_NEW_INTENT_EXTRA);
//        } else if (intent.hasExtra(SCROLL_TO_ALARM_INTENT_EXTRA)) {
//            long alarmId = intent.getLongExtra(SCROLL_TO_ALARM_INTENT_EXTRA, Alarm.INVALID_ID);
//            if (alarmId != Alarm.INVALID_ID) {
//                setSmoothScrollStableId(alarmId);
//                if (mCursorLoader != null && mCursorLoader.isStarted()) {
//                    // We need to force a reload here to make sure we have the latest view
//                    // of the data to scroll to.
//                    mCursorLoader.forceLoad();
//                }
//            }
//
//            // Remove the SCROLL_TO_ALARM extra now that we've processed it.
//            intent.removeExtra(SCROLL_TO_ALARM_INTENT_EXTRA);
//        }
    }

//    @Override
//    public void smoothScrollTo(int position) {
//        mLayoutManager.scrollToPositionWithOffset(position, 20);
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        mAlarmTimeAdapter.saveInstance(outState);
//        mAlarmTimeClickHandler.saveInstance(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ToastMaster.cancelToast();
    }

    @Override
    public void onPause() {
        super.onPause();
        // When the user places the app in the background by pressing "home",
        // dismiss the toast bar. However, since there is no way to determine if
        // home was pressed, just dismiss any existing toast bar when restarting
        // the app.
//        mAlarmUpdateHandler.hideUndoBar(false, null);
    }


//    public void setLabel(Alarm alarm, String label) {
//        alarm.label = label;
//        mAlarmUpdateHandler.asyncUpdateAlarm(alarm, false, true);
//    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        return Alarm.getAlarmsCursorLoader(getActivity());
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, final Cursor data) {
//        mEmptyViewController.setEmpty(data.getCount() == 0);
//        mAlarmTimeAdapter.swapCursor(data);
//        if (mScrollToAlarmId != Alarm.INVALID_ID) {
//            scrollToAlarm(mScrollToAlarmId);
//            setSmoothScrollStableId(Alarm.INVALID_ID);
//        }
//    }
//
//    /**
//     * Scroll to alarm with given alarm id.
//     *
//     * @param alarmId The alarm id to scroll to.
//     */
//    private void scrollToAlarm(long alarmId) {
//        final int alarmCount = mAlarmTimeAdapter.getItemCount();
//        int alarmPosition = -1;
//        for (int i = 0; i < alarmCount; i++) {
//            long id = mAlarmTimeAdapter.getItemId(i);
//            if (id == alarmId) {
//                alarmPosition = i;
//                break;
//            }
//        }
//
//        if (alarmPosition >= 0) {
//            mAlarmTimeAdapter.expand(alarmPosition);
//        } else {
//            // Trying to display a deleted alarm should only happen from a missed notification for
//            // an alarm that has been marked deleted after use.
//            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
//                    R.string.missed_alarm_has_been_deleted, Toast.LENGTH_LONG);
//            ToastMaster.setToast(toast);
//            toast.show();
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> cursorLoader) {
//        mAlarmTimeAdapter.swapCursor(null);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case R.id.request_code_ringtone:
                // Extract the selected ringtone uri.
//                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
//                if (uri == null) {
//                    uri = Alarm.NO_RINGTONE_URI;
//                }
//
//                // Update the default ringtone for future new alarms.
//                DataModel.getDataModel().setDefaultAlarmRingtoneUri(uri);
//
//                // Set the ringtone uri on the alarm.
//                final Alarm alarm = mAlarmTimeClickHandler.getSelectedAlarm();
//                alarm.alert = uri;
//
//                // Save the change to alarm.
//                mAlarmUpdateHandler.asyncUpdateAlarm(alarm, false /* popToast */,
//                        true /* minorUpdate */);
                break;
            default:
                LogUtils.w("Unhandled request code in onActivityResult: " + requestCode);
        }
    }

//    @Override
//    public void setSmoothScrollStableId(long stableId) {
//        mScrollToAlarmId = stableId;
//    }

    @Override
    public void onFabClick(View view) {
//        mAlarmUpdateHandler.hideUndoBar(true, null);
        startCreatingAlarm();
    }

    @Override
    public void setFabAppearance() {
        if (mFab == null || getDeskClock().getSelectedTab() != DeskClock.ALARM_TAB_INDEX) {
            return;
        }
        mFab.setVisibility(View.VISIBLE);
        mFab.setImageResource(R.drawable.ic_add_white_24dp);
        mFab.setContentDescription(getString(R.string.button_alarms));
    }

    @Override
    public void setLeftRightButtonAppearance() {
        if (mLeftButton == null || mRightButton == null ||
                getDeskClock().getSelectedTab() != DeskClock.ALARM_TAB_INDEX) {
            return;
        }
        mLeftButton.setVisibility(View.INVISIBLE);
        mRightButton.setVisibility(View.INVISIBLE);
    }

    private void startCreatingAlarm() {
//        mAlarmTimeClickHandler.clearSelectedAlarm();
//        TimePickerCompat.showTimeEditDialog(this, null /* alarm */,
//                DateFormat.is24HourFormat(getActivity()));
    }
}
