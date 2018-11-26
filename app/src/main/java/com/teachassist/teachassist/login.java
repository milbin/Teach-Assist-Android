package com.teachassist.teachassist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.http.HEAD;

public class login extends AppCompatActivity {
    EditText usernameInput;
    EditText passwordInput;
    Button submit_button;
    CheckBox checkbox;

    String username, password;

    private void submit_buttonClicked(){
        username = usernameInput.getText().toString();
        password = passwordInput.getText().toString();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        usernameInput = (EditText) findViewById(R.id.editText1);
        passwordInput = (EditText) findViewById(R.id.editText2);

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

    }

    class submit_buttonClick implements View.OnClickListener {

        @Override
        public void onClick(View v){
            submit_buttonClicked();

            ProgressDialog dialog = ProgressDialog.show(login.this, "",
                    "Signing in...", true);
            String Username = username;
            String Password = password;


            new login.GetTaData().execute(Username, Password);


            Intent myIntent = new Intent(login.this, MainActivity.class);
            myIntent.putExtra("username", username);
            myIntent.putExtra("password", password);
            dialog.dismiss();
            startActivity(myIntent);


        }
    }

    private class GetTaData extends AsyncTask<String, Integer, LinkedHashMap<String, List<String>>> {


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


        protected void onPostExecute(LinkedHashMap<String, List<String>> response){
            if(checkbox.isChecked()){

                String filename = "Credentials.txt";
                String fileContents = username+":"+password;

                // Get the directory of private Ta app storage.
                final File path = getFilesDir();

                File file = new File(path, filename);

                FileOutputStream outputStream;
                try {
                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(fileContents.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        System.out.println(response);
        }
    }

}
