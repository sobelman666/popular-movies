package com.sobelman.and.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * PreferenceFragment for app settings. Largely borrowed from Sunshine.
 */
public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);
            if (index > -1) {
                listPreference.setSummary(listPreference.getEntries()[index]);
            }
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general);

        SharedPreferences preferences = getPreferenceScreen().getSharedPreferences();
        String key = getString(R.string.pref_list_style);
        Preference listStylePreference =
                getPreferenceScreen().findPreference(key);
        String listStyleValue = preferences.getString(key, getString(R.string.pref_popular_value));
        setPreferenceSummary(listStylePreference, listStyleValue);
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
    }
}
