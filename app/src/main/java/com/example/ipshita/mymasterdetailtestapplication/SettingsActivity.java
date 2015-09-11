package com.example.ipshita.mymasterdetailtestapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;

import java.util.Locale;

/**
 * Created by iroyb_000 on 10-09-2015.
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    private TwoStatePreference lockscreenEnable;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        addPreferencesFromResource(R.xml.pref_country);
        ListPreference countryListPreference = (ListPreference) findPreference(getString(R.string.preference_country_key));
        lockscreenEnable = (TwoStatePreference) findPreference(getString(R.string.preference_lockscreen_key));
        if (lockscreenEnable != null) {

            lockscreenEnable.setEnabled(true);
        }
        //Update the operations like storing, updating UI etc... on pref change.
        lockscreenEnable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference arg0, Object isLockScreenEnabled) {

                boolean isLockscreenControlON = ((Boolean) isLockScreenEnabled).booleanValue();


                SharedPreferences.Editor e = mPrefs.edit();
                e.putBoolean(getString(R.string.preference_lockscreen_key), isLockscreenControlON);
                e.commit();

                Intent intent = new Intent();
                intent.setAction(getString(R.string.action_lockscreen));

                sendBroadcast(intent);


                return true;
            }
        });
        if (null != countryListPreference) {
            // values is code
            // entries is country name
            countryListPreference.setEntryValues(Locale.getISOCountries());
            String[] entries = new String[countryListPreference.getEntryValues().length];
            for (int i = 0; i < countryListPreference.getEntryValues().length; i++) {
                entries[i] = new Locale("", Locale.getISOCountries()[i]).getDisplayCountry();
            }
            countryListPreference.setEntries(entries);
            countryListPreference.setDefaultValue(Locale.US);

        }

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.preference_country_key
        )));

    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }
}
