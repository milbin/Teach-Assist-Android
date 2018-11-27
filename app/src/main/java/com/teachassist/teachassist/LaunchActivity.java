package com.teachassist.teachassist;

import android.content.Intent;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Open file with username and password
        String filename = "Credentials.txt";
        final File path = getFilesDir();
        File file = new File(path, filename);
        if(file.length() != 0) {
            ArrayList<String> credentials = new ArrayList<>();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    System.out.println(line + "HERE 1");
                    credentials.add(line.split(":")[0]);
                    credentials.add(line.split(":")[1]);
                }
                br.close();
            } catch (IOException e) {
                //TODO add proper error handling for corrupt file or smth
            }
            username = credentials.get(0);
            password = credentials.get(1);
            Intent myIntent = new Intent(LaunchActivity.this, MainActivity.class);
            myIntent.putExtra("username", username);
            myIntent.putExtra("password", password);
            startActivity(myIntent);
        }
        else{

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    System.out.println(line + "HERE<-----");
                }
                br.close();
            } catch (IOException e) {
                //TODO add proper error handling for corrupt file or smth
            }
            Intent myIntent = new Intent(LaunchActivity.this, login.class);
            startActivity(myIntent);
        }
        finish();
    }
}
