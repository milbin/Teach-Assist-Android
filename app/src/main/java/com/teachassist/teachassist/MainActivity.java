package com.teachassist.teachassist;

import android.support.annotation.Dimension;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import java.lang.reflect.Array;
import java.net.URLConnection;
import java.net.URL;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = findViewById(R.id.button);
        button.setLayoutParams(new LinearLayout.LayoutParams(10, 100));



        TA ta = new TA();
        String username = "335525291";
        String password = "6rx8836f";
        HashMap<String, List<String>> response = ta.GetTAData(username, password);
        double Average = ta.GetAverage(response);
        System.out.println(Average);


    }
}

