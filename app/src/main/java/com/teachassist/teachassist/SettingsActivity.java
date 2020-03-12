package com.teachassist.teachassist;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.view.MenuItem;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefes";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String REMEMBERME = "REMEMBERME";
    public static ArrayList<String> courses;
    Activity activity;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(sharedPreferences.getBoolean("lightThemeEnabled", true)){
            setTheme(R.style.LightTheme);
        }else{
            setTheme(R.style.DarkTheme);
        }
        setContentView(R.layout.settings);

        if(sharedPreferences.getBoolean("isPremiumUser", false)){
            findViewById(R.id.upgradeRL).setVisibility(View.GONE);
            findViewById(R.id.premiumThankYouView).setVisibility(View.VISIBLE);
        }

        Intent intent = getIntent();
        courses =  intent.getStringArrayListExtra("key");
        activity = this;
        context = this;


        //setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView)toolbar.findViewById(R.id.toolbar_title)).setText("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back);
        //load settings fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.settings_fragment,
                new PrefsFragment()).commit();

        findViewById(R.id.upgradeButton).setOnClickListener(new upgradeButtonClick());
    }

    public class upgradeButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            CheckIfUserIsPremium userIsPremiumClass = new CheckIfUserIsPremium();
            userIsPremiumClass.check(context, activity, true);
        }
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


                    final Context context = getContext();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Please Restart App")
                            .setMessage("Theme changes will not take effect until the app has been restarted.")
                            .setPositiveButton(R.string.restart_now, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent mStartActivity = new Intent(context, MainActivity.class);
                                    PendingIntent intent = PendingIntent.getActivity(context, 55555, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                    AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 200, intent);
                                    Runtime.getRuntime().exit(0);
                                }
                            })
                            .setNeutralButton(R.string.restart_later, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {}
                            }).show();
                    return true;
                }
            });
        }

    }
}