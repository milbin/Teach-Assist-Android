package com.teachassist.teachassist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.net.URLConnection;
import java.net.URL;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    private void GetTAData(String Username, String Password) {
        String url = "https://ta.yrdsb.ca/live/index.php";
        String charset = "UTF-8";
        String query = "";
        URLConnection connection = new URL(url + "?" + query).openConnection();
        connection.setDoOutput(true); // Triggers POST.
        connection.setRequestProperty("Accept-Charset", charset);
        InputStream response = connection.getInputStream();




    }

}
