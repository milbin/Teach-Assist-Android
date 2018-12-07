package com.teachassist.teachassist;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.decimal4j.util.DoubleRounder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class MarksView extends AppCompatActivity {
    String username;
    String password;
    int subject_number;
    LinkedHashMap<String,List<HashMap<String,String>>> marks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marks_view);

        //get intents
        Intent intent = getIntent();
        username = intent.getStringExtra("username").replaceAll("\\s+","");
        password = intent.getStringExtra("password").replaceAll("\\s+","");
        subject_number = intent.getIntExtra("subject",0);
        new GetMarks().execute();

    }

    private class GetMarks extends AsyncTask<String, Integer, Float>{
        @Override
        protected void onPreExecute(){super.onPreExecute();}

        @Override
        protected Float doInBackground(String... temp){
            TA ta = new TA();
            System.out.println(username);
            System.out.println(password);
            ta.GetTAData(username,password);
            marks = ta.GetMarks(subject_number);
            System.out.println(marks);
            return 1f;

        }

        protected void onProgressUpdate(Integer... temp) {
            super.onProgressUpdate();
        }
        @Override
        protected void onPostExecute(Float temp) {super.onPostExecute(1f);}

    }
}
