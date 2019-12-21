package com.teachassist.teachassist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.appcompat.widget.Toolbar;
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

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Load the preferences from an XML resource
            setPreferencesFromResource(R.xml.settings, rootKey);

            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            Preference sharedPrefs = getPreferenceManager().findPreference("Light Theme Enabled");
            sharedPrefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(Boolean.valueOf(newValue.toString())) {
                        editor.putBoolean("lightThemeEnabled", true);
                    }else{
                        editor.putBoolean("lightThemeEnabled", false);
                    }
                    editor.apply();
                    return true;
                }
            });
        }

    }
}