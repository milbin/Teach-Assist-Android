package com.teachassist.teachassist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.view.MenuItem;
import android.preference.PreferenceManager;
import android.widget.TextView;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefes";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String REMEMBERME = "REMEMBERME";
    public static ArrayList<String> courses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(sharedPreferences.getBoolean("lightThemeEnabled", false)){
            setTheme(R.style.LightTheme);
        }else{
            setTheme(R.style.DarkTheme);
        }
        setContentView(R.layout.settings);
        Intent intent = getIntent();
        courses =  intent.getStringArrayListExtra("key");



        //setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView)toolbar.findViewById(R.id.toolbar_title)).setText("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back);
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Please Restart App")
                            .setMessage("Theme changes will not take effect until the app is restarted")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {}
                            }).show();
                    return true;
                }
            });
        }

    }
}