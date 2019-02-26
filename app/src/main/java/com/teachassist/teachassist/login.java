package com.teachassist.teachassist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.LinkedHashMap;
import java.util.List;

public class login extends AppCompatActivity {
    EditText usernameInput;
    EditText passwordInput;
    Button submit_button;
    CheckBox checkbox;
    String username, password;

    public static final String SHARED_PREFS = "credentials";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String REMEMBERME = "REMEMBERME";

    private void submit_buttonClicked(){
        username = usernameInput.getText().toString();
        password = passwordInput.getText().toString();
        //username = "335525168";
        //password = "4a6349kc";

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        usernameInput = (EditText) findViewById(R.id.Username);
        passwordInput = (EditText) findViewById(R.id.Password);

        //change focus of EditText view on click
        usernameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    usernameInput.setHint("");
                else
                    usernameInput.setHint("Username");
            }
        });
        passwordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    passwordInput.setHint("");
                else
                    passwordInput.setHint("Password");
            }
        });



        checkbox = findViewById(R.id.checkbox);
        checkbox.setChecked(true);
        submit_button = (Button) findViewById(R.id.login_button);
        submit_button.setOnClickListener(new submit_buttonClick());

        // on enter press submit form
        passwordInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit_button.performClick();
                    return true;
                }
                return false;
            }
        });



    }

    class submit_buttonClick implements View.OnClickListener {

        @Override
        public void onClick(View v){
            submit_buttonClicked();


            String Username = username;
            String Password = password;


            new login.GetTaData().execute(Username, Password);





        }
    }

    private class GetTaData extends AsyncTask<String, Integer, LinkedHashMap<String, List<String>>> {
        ProgressDialog dialog = ProgressDialog.show(login.this, "",
                "Signing in...", true);


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected LinkedHashMap<String, List<String>> doInBackground(String... params){
            TA ta = new TA();
            String Username = params[0];
            String Password = params[1];
            LinkedHashMap<String, List<String>> response = ta.GetTAData(Username, Password);

            return response;

        }

        protected void onProgressUpdate(Integer... progress) {}


        protected void onPostExecute(LinkedHashMap<String, List<String>> response) {

            final EditText usernameInput = (EditText) findViewById(R.id.Username);
            final EditText passwordInput = (EditText) findViewById(R.id.Password);
            System.out.println(response);
            if (response == null || response.isEmpty()){
                dialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
                builder.setTitle("Sign in Failure")
                .setMessage("We could not reach TeachAssist, please check your Username, Password and Internet connection")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        usernameInput.getText().clear();
                        passwordInput.getText().clear();
                    }
                    }).show();


            }
            else {
                Crashlytics.setUserIdentifier(username);
                Crashlytics.setString("username", username);
                Crashlytics.setString("password", password);

                Crashlytics.log(Log.DEBUG, "username", username);
                Crashlytics.log(Log.DEBUG, "password", password);
                /*
                if(true) //compiler error without this line
                    throw new RuntimeException("This is a test crash");
                */
                if (checkbox.isChecked()) {
                    //add username and password to shared preferances
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor =   sharedPreferences.edit();
                    editor.putString(USERNAME, username);
                    editor.putString(PASSWORD, password);
                    editor.putBoolean(REMEMBERME, true);
                    editor.apply();


                }
                else{

                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor =   sharedPreferences.edit();
                    editor.putString(USERNAME, username);
                    editor.putString(PASSWORD, password);
                    editor.putBoolean(REMEMBERME, false);
                    editor.apply();

                }
                Intent myIntent = new Intent(login.this, MainActivity.class);
                myIntent.putExtra("username", username);
                myIntent.putExtra("password", password);
                startActivity(myIntent);
                dialog.dismiss();
                finish();
            }
        }
    }

}
