package net.spreha.herak.hotmovies;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;


public class SettingsActivity extends Activity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }







    public static class SettingsFragment extends PreferenceFragment  {
        private final String LOG_TAG = this.getClass().getSimpleName();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_general);

            Preference pref = findPreference(getString(R.string.pref_sort_key));

            pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString();

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
            /*if(value.getClass().equals(String.class)){
                String string = (String) value;
                SharedPreferences settings = getSharedPreferences("pref_general.xml", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(getString(R.string.pref_location_key), string);

                // Commit the edits!
                editor.commit();
            }*/

                        preference.setSummary(stringValue);
                    }
                    return true;
                }
            });

        }


    }







}
