/*
 * Copyright (C) 2009 The Android Open Source Project
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


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Settings for the Alarm Clock.
 */
public final class SettingsActivity extends BaseActivity {


    public static final String KEY_DEFAULT_NUMBER = "key_default_number";
    public static final String KEY_SWITCH_ONE = "key_switch_one";
    public static final String KEY_PROCESS_ID = "key_process_id";
    private static final String EMPTY = "";

    private PrefsFragment prefsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        setBackgroundColor(getResources().getColor(R.color.anthracite), false);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_item_default:
                restoreDefaults();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setPrefsFragment(PrefsFragment prefsFragment) {
        this.prefsFragment = prefsFragment;
    }

    private void restoreDefaults() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit()
                .putString(KEY_DEFAULT_NUMBER, EMPTY)
                .putBoolean(KEY_SWITCH_ONE, true)
                .putString(KEY_PROCESS_ID, EMPTY)
                .apply();
        if (prefsFragment != null) {
            prefsFragment.recreate();
        } else {
            recreate();
        }
    }

    public static class PrefsFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            if (getActivity() instanceof SettingsActivity) {
                ((SettingsActivity) getActivity()).setPrefsFragment(this);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            // By default, do not recreate the DeskClock activity
            getActivity().setResult(RESULT_CANCELED);
            refresh();
        }

        @Override
        public boolean onPreferenceChange(Preference pref, Object newValue) {
            switch (pref.getKey()) {
                case KEY_DEFAULT_NUMBER:
                case KEY_PROCESS_ID:
                    pref.setSummary((String) newValue);
                    break;
                case KEY_SWITCH_ONE:
                    final boolean switchOneEnabled = ((SwitchPreference) pref).isChecked();
                    final Preference processIdPref = findPreference(KEY_PROCESS_ID);
                    processIdPref.setEnabled(!switchOneEnabled);
                    break;
            }
            // Set result so DeskClock knows to refresh itself
            getActivity().setResult(RESULT_OK);
            return true;
        }

        @Override
        public boolean onPreferenceClick(Preference pref) {
            return false;
        }

        private void refresh() {
            final EditTextPreference defaultNumberPref = (EditTextPreference) findPreference
                    (KEY_DEFAULT_NUMBER);
            defaultNumberPref.setSummary(defaultNumberPref.getText());
            defaultNumberPref.setOnPreferenceChangeListener(this);

            final Preference switchOnePref = findPreference(KEY_SWITCH_ONE);
            final boolean switchOneEnabled = ((SwitchPreference) switchOnePref).isChecked();
            switchOnePref.setOnPreferenceChangeListener(this);

            final EditTextPreference processIdPref = (EditTextPreference) findPreference
                    (KEY_PROCESS_ID);
            processIdPref.setEnabled(switchOneEnabled);
            processIdPref.setSummary(processIdPref.getText());
            processIdPref.setOnPreferenceChangeListener(this);
        }

        private void recreate() {
            getPreferenceScreen().removeAll();
            addPreferencesFromResource(R.xml.settings);
            refresh();
        }
    }
}
