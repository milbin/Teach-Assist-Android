package com.teachassist.teachassist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.gson.Gson;

public class SettingsActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefes";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String REMEMBERME = "REMEMBERME";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back button

        //load settings fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.dynamic_fragment_frame_layout,
                new PrefsFragment()).commit();


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }







    public static class PrefsFragment extends PreferenceFragmentCompat {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("sharedPrefes", Context.MODE_PRIVATE);
        public static final String ALLNOTIFICATIONS = "ALLNOTIFICATIONS";
        public static final String NOTIFICATION1 = "NOTIFICATION1";
        public static final String NOTIFICATION2 = "NOTIFICATION2";
        public static final String NOTIFICATION3 = "NOTIFICATION3";
        public static final String NOTIFICATION4 = "NOTIFICATION4";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Load the preferences from an XML resource
            setPreferencesFromResource(R.xml.settings, rootKey);
            Preference all_notifications = (Preference) findPreference("All Notifications");
            all_notifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences.Editor editor =   sharedPreferences.edit();
                    editor.putBoolean(ALLNOTIFICATIONS, false);
                    editor.putBoolean(NOTIFICATION1, false);
                    editor.putBoolean(NOTIFICATION2, false);
                    editor.putBoolean(NOTIFICATION3, false);
                    editor.putBoolean(NOTIFICATION4, false);

                    return true;
                }
            });

            Preference notifications1 = (Preference) findPreference("Notification1");
            notifications1.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences.Editor editor =   sharedPreferences.edit();
                    editor.putBoolean(ALLNOTIFICATIONS, true);
                    editor.putBoolean(NOTIFICATION1, false);

                    return true;
                }
            });
            Preference notifications2 = (Preference) findPreference("Notification2");
            notifications2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences.Editor editor =   sharedPreferences.edit();
                    editor.putBoolean(ALLNOTIFICATIONS, true);
                    editor.putBoolean(NOTIFICATION2, false);


                    return true;
                }
            });
            Preference notifications3 = (Preference) findPreference("Notification3");
            notifications3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences.Editor editor =   sharedPreferences.edit();
                    editor.putBoolean(ALLNOTIFICATIONS, true);
                    editor.putBoolean(NOTIFICATION3, false);

                    return true;
                }
            });
            Preference notifications4= (Preference) findPreference("Notification4");
            notifications4.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences.Editor editor =   sharedPreferences.edit();
                    editor.putBoolean(ALLNOTIFICATIONS, true);
                    editor.putBoolean(NOTIFICATION4, false);

                    return true;
                }
            });
        }
    }
}