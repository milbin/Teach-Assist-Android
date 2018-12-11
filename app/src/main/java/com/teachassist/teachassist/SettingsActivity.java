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
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;

public class SettingsActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefes";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String REMEMBERME = "REMEMBERME";
    public static ArrayList<String> courses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Intent intent = getIntent();
        courses =  intent.getStringArrayListExtra("key");



        //setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back button
        //load settings fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.settings_fragment,
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

        public static final String SHARED_PREFS = "settings";
        public static final String ALLNOTIFICATIONS = "ALLNOTIFICATIONS";
        public static final String NOTIFICATION1 = "NOTIFICATION1";
        public static final String NOTIFICATION2 = "NOTIFICATION2";
        public static final String NOTIFICATION3 = "NOTIFICATION3";
        public static final String NOTIFICATION4 = "NOTIFICATION4";


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Load the preferences from an XML resource
            setPreferencesFromResource(R.xml.settings, rootKey);

            //checkbox summaries
            Preference notificationSummary1 = findPreference("Notifications 1");
            Preference notificationSummary2 = findPreference("Notifications 2");
            Preference notificationSummary3 = findPreference("Notifications 3");
            Preference notificationSummary4 = findPreference("Notifications 4");
            notificationSummary1.setSummary("Disable Notifications for: " + courses.get(0));
            notificationSummary2.setSummary("Disable Notifications for: " + courses.get(1));
            notificationSummary3.setSummary("Disable Notifications for: " + courses.get(2));
            notificationSummary4.setSummary("Disable Notifications for: " + courses.get(3));
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            Preference all_notifications = getPreferenceManager().findPreference("All Notifications");
            all_notifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(Boolean.valueOf(newValue.toString())) {
                        editor.putBoolean(ALLNOTIFICATIONS, true);
                        editor.putBoolean(NOTIFICATION1, true);
                        editor.putBoolean(NOTIFICATION2, true);
                        editor.putBoolean(NOTIFICATION3, true);
                        editor.putBoolean(NOTIFICATION4, true);
                    }else{
                        editor.putBoolean(ALLNOTIFICATIONS, false);
                        editor.putBoolean(NOTIFICATION1, false);
                        editor.putBoolean(NOTIFICATION2, false);
                        editor.putBoolean(NOTIFICATION3, false);
                        editor.putBoolean(NOTIFICATION4, false);
                    }
                    editor.apply();
                    return true;
                }
            });

            Preference notifications1 = (Preference) findPreference("Notifications 1");
            notifications1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(Boolean.valueOf(newValue.toString())) {
                        editor.putBoolean(ALLNOTIFICATIONS, true);
                        editor.putBoolean(NOTIFICATION1, true);
                    }else {
                        editor.putBoolean(ALLNOTIFICATIONS, false);
                        editor.putBoolean(NOTIFICATION1, false);
                    }
                    editor.apply();
                    return true;
                }
            });
            Preference notifications2 = (Preference) findPreference("Notifications 2");
            notifications2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(Boolean.valueOf(newValue.toString())) {
                        editor.putBoolean(ALLNOTIFICATIONS, true);
                        editor.putBoolean(NOTIFICATION2, true);
                    }else {
                        editor.putBoolean(ALLNOTIFICATIONS, false);
                        editor.putBoolean(NOTIFICATION2, false);
                    }
                    editor.apply();
                    return true;
                }
            });
            Preference notifications3 = (Preference) findPreference("Notifications 3");
            notifications3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(Boolean.valueOf(newValue.toString())) {
                        editor.putBoolean(ALLNOTIFICATIONS, true);
                        editor.putBoolean(NOTIFICATION3, true);
                    }else {
                        editor.putBoolean(ALLNOTIFICATIONS, false);
                        editor.putBoolean(NOTIFICATION3, false);
                    }
                    editor.apply();
                    return true;
                }
            });
            Preference notifications4 = (Preference) findPreference("Notifications 4");
            notifications4.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(Boolean.valueOf(newValue.toString())) {
                        editor.putBoolean(ALLNOTIFICATIONS, true);
                        editor.putBoolean(NOTIFICATION4, true);
                    }else {
                        editor.putBoolean(ALLNOTIFICATIONS, false);
                        editor.putBoolean(NOTIFICATION4, false);
                    }
                    editor.apply();
                    return true;
                }
            });
        }

    }
}