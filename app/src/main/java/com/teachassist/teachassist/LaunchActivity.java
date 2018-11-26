package com.teachassist.teachassist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LaunchActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Open file with username and password
        String filename = "Credentials.txt";
        final File path = getFilesDir();
        File file = new File(path, filename);
        ArrayList<String> credentials = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                System.out.println(line);
                credentials.add(line.split(":")[0]);
                credentials.add(line.split(":")[1]);
            }
            br.close();
        }
        catch (IOException e) {
            //TODO add proper error handling for corrupt file or smth
        }
    }
}
