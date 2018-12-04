package com.teachassist.teachassist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LaunchActivity extends AppCompatActivity {
    String username;
    String password;
    boolean RemeberMe;
    public static final String SHARED_PREFS = "sharedPrefes";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String REMEMBERME = "REMEMBERME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Open file with username and password
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        username = sharedPreferences.getString(USERNAME, "");
        password = sharedPreferences.getString(PASSWORD, "");
        RemeberMe = sharedPreferences.getBoolean(REMEMBERME, false);
        System.out.println(username.isEmpty());
        System.out.println(password.isEmpty());
        System.out.println(RemeberMe);



        if(!username.isEmpty() && !password.isEmpty() && RemeberMe) {
            Intent myIntent = new Intent(LaunchActivity.this, MainActivity.class);
            myIntent.putExtra("username", username);
            myIntent.putExtra("password", password);
            startActivity(myIntent);
        }
        else{
            Intent myIntent = new Intent(LaunchActivity.this, login.class);
            startActivity(myIntent);
        }
        finish();
    }
}
