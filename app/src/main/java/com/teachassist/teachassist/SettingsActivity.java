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
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
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
        public static final String NOTIFICATION5 = "NOTIFICATION5";
        public static final String NOTIFICATION6 = "NOTIFICATION6";
        public static final String NOTIFICATION7 = "NOTIFICATION7";
        public static final String NOTIFICATION8 = "NOTIFICATION8";
        public static final String NOTIFICATION9 = "NOTIFICATION9";
        public static final String NOTIFICATION10 = "NOTIFICATION10";
        public static final String NOTIFICATIONEXTRA = "NOTIFICATIONEXTRA";


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Load the preferences from an XML resource
            setPreferencesFromResource(R.xml.settings, rootKey);

            //checkbox summaries
            Preference notificationSummary1 = findPreference("Notifications 1");
            Preference notificationSummary2 = findPreference("Notifications 2");
            Preference notificationSummary3 = findPreference("Notifications 3");
            Preference notificationSummary4 = findPreference("Notifications 4");
            Preference notificationSummary5 = findPreference("Notifications 5");
            Preference notificationSummary6 = findPreference("Notifications 6");
            Preference notificationSummary7 = findPreference("Notifications 7");
            Preference notificationSummary8 = findPreference("Notifications 8");
            Preference notificationSummary9 = findPreference("Notifications 9");
            Preference notificationSummary10 = findPreference("Notifications 10");
            Preference notificationSummaryEXTRA = findPreference("Notifications EXTRA");
            notificationSummary1.setSummary("Disable Notifications for: " + courses.get(0));
            notificationSummary2.setSummary("Disable Notifications for: " + courses.get(1));
            if(courses.size() > 2) {
                notificationSummary3.setSummary("Disable Notifications for: " + courses.get(2));
            }
            if(courses.size() > 3) {
                notificationSummary4.setSummary("Disable Notifications for: " + courses.get(3));
            }
            if(courses.size() > 4) {
                notificationSummary5.setSummary("Disable Notifications for: " + courses.get(4));
            }
            if(courses.size() > 5) {
                notificationSummary6.setSummary("Disable Notifications for: " + courses.get(5));
            }
            if(courses.size() > 6) {
                notificationSummary7.setSummary("Disable Notifications for: " + courses.get(6));
            }
            if(courses.size() > 7) {
                notificationSummary8.setSummary("Disable Notifications for: " + courses.get(7));
            }
            if(courses.size() > 8) {
                notificationSummary9.setSummary("Disable Notifications for: " + courses.get(8));
            }
            if(courses.size() > 9) {
                notificationSummary10.setSummary("Disable Notifications for: " + courses.get(9));
            }
            if(courses.size() > 10) {
                notificationSummaryEXTRA.setSummary("Disable Notifications for Periods 11+  " );
            }
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
                        editor.putBoolean(NOTIFICATION5, true);
                        editor.putBoolean(NOTIFICATION6, true);
                        editor.putBoolean(NOTIFICATION7, true);
                        editor.putBoolean(NOTIFICATION8, true);
                        editor.putBoolean(NOTIFICATION9, true);
                        editor.putBoolean(NOTIFICATION10, true);
                        editor.putBoolean(NOTIFICATIONEXTRA, true);
                    }else{
                        editor.putBoolean(ALLNOTIFICATIONS, false);
                        editor.putBoolean(NOTIFICATION1, false);
                        editor.putBoolean(NOTIFICATION2, false);
                        editor.putBoolean(NOTIFICATION3, false);
                        editor.putBoolean(NOTIFICATION4, false);
                        editor.putBoolean(NOTIFICATION5, false);
                        editor.putBoolean(NOTIFICATION6, false);
                        editor.putBoolean(NOTIFICATION7, false);
                        editor.putBoolean(NOTIFICATION8, false);
                        editor.putBoolean(NOTIFICATION9, false);
                        editor.putBoolean(NOTIFICATION10, false);
                        editor.putBoolean(NOTIFICATIONEXTRA, false);
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
            if(courses.size() < 3){
                notifications3.setVisible(false);
            }
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
            if(courses.size() < 4){
                notifications4.setVisible(false);
            }
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
            Preference notifications5 = (Preference) findPreference("Notifications 5");
            if(courses.size() < 5){
                notifications5.setVisible(false);
            }
            notifications5.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(Boolean.valueOf(newValue.toString())) {
                        editor.putBoolean(ALLNOTIFICATIONS, true);
                        editor.putBoolean(NOTIFICATION5, true);
                    }else {
                        editor.putBoolean(ALLNOTIFICATIONS, false);
                        editor.putBoolean(NOTIFICATION5, false);
                    }
                    editor.apply();
                    return true;
                }
            });
            Preference notifications6 = (Preference) findPreference("Notifications 6");
            if(courses.size() < 6){
                notifications6.setVisible(false);
            }
            notifications6.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(Boolean.valueOf(newValue.toString())) {
                        editor.putBoolean(ALLNOTIFICATIONS, true);
                        editor.putBoolean(NOTIFICATION6, true);
                    }else {
                        editor.putBoolean(ALLNOTIFICATIONS, false);
                        editor.putBoolean(NOTIFICATION6, false);
                    }
                    editor.apply();
                    return true;
                }
            });
            Preference notifications7 = (Preference) findPreference("Notifications 7");
            if(courses.size() < 7){
                notifications7.setVisible(false);
            }
            notifications7.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(Boolean.valueOf(newValue.toString())) {
                        editor.putBoolean(ALLNOTIFICATIONS, true);
                        editor.putBoolean(NOTIFICATION7, true);
                    }else {
                        editor.putBoolean(ALLNOTIFICATIONS, false);
                        editor.putBoolean(NOTIFICATION7, false);
                    }
                    editor.apply();
                    return true;
                }
            });
            Preference notifications8 = (Preference) findPreference("Notifications 8");
            if(courses.size() < 8){
                notifications8.setVisible(false);
            }
            notifications8.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(Boolean.valueOf(newValue.toString())) {
                        editor.putBoolean(ALLNOTIFICATIONS, true);
                        editor.putBoolean(NOTIFICATION8, true);
                    }else {
                        editor.putBoolean(ALLNOTIFICATIONS, false);
                        editor.putBoolean(NOTIFICATION8, false);
                    }
                    editor.apply();
                    return true;
                }
            });
            Preference notifications9 = (Preference) findPreference("Notifications 9");
            if(courses.size() < 9){
                notifications9.setVisible(false);
            }
            notifications9.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(Boolean.valueOf(newValue.toString())) {
                        editor.putBoolean(ALLNOTIFICATIONS, true);
                        editor.putBoolean(NOTIFICATION9, true);
                    }else {
                        editor.putBoolean(ALLNOTIFICATIONS, false);
                        editor.putBoolean(NOTIFICATION9, false);
                    }
                    editor.apply();
                    return true;
                }
            });
            Preference notifications10 = (Preference) findPreference("Notifications 10");
            if(courses.size() < 10){
                notifications10.setVisible(false);
            }
            notifications10.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(Boolean.valueOf(newValue.toString())) {
                        editor.putBoolean(ALLNOTIFICATIONS, true);
                        editor.putBoolean(NOTIFICATION10, true);
                    }else {
                        editor.putBoolean(ALLNOTIFICATIONS, false);
                        editor.putBoolean(NOTIFICATION10, false);
                    }
                    editor.apply();
                    return true;
                }
            });

            Preference notificationsEXTRA = (Preference) findPreference("Notifications EXTRA");
            if(courses.size() < 11){
                notificationsEXTRA.setVisible(false);
            }
            notificationsEXTRA.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(Boolean.valueOf(newValue.toString())) {
                        editor.putBoolean(ALLNOTIFICATIONS, true);
                        editor.putBoolean(NOTIFICATIONEXTRA, true);
                    }else {
                        editor.putBoolean(ALLNOTIFICATIONS, false);
                        editor.putBoolean(NOTIFICATIONEXTRA, false);
                    }
                    editor.apply();
                    return true;
                }
            });
        }

    }
}