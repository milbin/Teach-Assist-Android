package com.teachassist.teachassist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import static com.teachassist.teachassist.MainActivity.CREDENTIALS;

public class LaunchActivity extends AppCompatActivity {
    String username;
    String password;
    boolean RemeberMe;
    public static final String CREDENTIALS = "credentials";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String REMEMBERME = "REMEMBERME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Open file with username and password
        SharedPreferences sharedPreferences = getSharedPreferences(CREDENTIALS, MODE_PRIVATE);
        username = sharedPreferences.getString(USERNAME, "");
        password = sharedPreferences.getString(PASSWORD, "");
        RemeberMe = sharedPreferences.getBoolean(REMEMBERME, false);




        if(!username.isEmpty() && !password.isEmpty() && RemeberMe) {

            final SharedPreferences sharedPreferencesNotifications = getSharedPreferences("notifications", MODE_PRIVATE);
            String token = sharedPreferencesNotifications.getString("token", "");
            //register with firebase, if user is already registered nothing will happen
            try {
                final FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                if(currentUser == null){
                    auth.createUserWithEmailAndPassword(username+"@"+password+".android", password);
                }
                if(!token.equals("") && !currentUser.getDisplayName().equals(token)){
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(token)
                            .build();
                    currentUser.updateProfile(profileUpdates);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
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
