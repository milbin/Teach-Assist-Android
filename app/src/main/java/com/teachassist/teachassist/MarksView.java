package com.teachassist.teachassist;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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

public class MarksView extends AppCompatActivity{
    String username;
    String password;
    int subject_number;
    String subject;
    LinkedHashMap<String,List<Map<String,String>>> marks;
    ProgressDialog dialog;
    NavigationView navigationView;
    private DrawerLayout drawer;
    public static final String SHARED_PREFS = "sharedPrefes";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String REMEMBERME = "REMEMBERME";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marks_view);

        //get intents
        Intent intent = getIntent();
        username = intent.getStringExtra("username").replaceAll("\\s+","");
        password = intent.getStringExtra("password").replaceAll("\\s+","");
        subject_number = intent.getIntExtra("subject",0);
        //progress dialog
        dialog = ProgressDialog.show(MarksView.this, "", "Loading...", true);
        new GetMarks().execute();

        //setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Marks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back button

    }
    private void showToast(String text){
        Toast.makeText(MarksView.this, text, Toast.LENGTH_SHORT).show();
    }

    private class GetMarks extends AsyncTask<String, Integer, LinkedHashMap<String,List<Map<String,String>>>>{
        @Override
        protected void onPreExecute(){super.onPreExecute();}

        @Override
        protected LinkedHashMap<String,List<Map<String,String>>> doInBackground(String... temp){
            TA ta = new TA();
            System.out.println(username);
            System.out.println(password);
            ta.GetTAData(username,password);
            marks = ta.GetMarks(subject_number);
            subject = ta.GetCourse(subject_number);
            return marks;

        }

        protected void onProgressUpdate(Integer... temp) {
            super.onProgressUpdate();
        }
        @Override
        protected void onPostExecute(LinkedHashMap<String,List<Map<String,String>>> marks) {
            //create table
            TableLayout ll = (TableLayout) findViewById(R.id.marks_table);

            LinkedHashMap<String,String> colors = new LinkedHashMap<>();
            colors.put("knowledge",Integer.toString(getResources().getColor(R.color.knowledge)));
            colors.put("thinking",Integer.toString(getResources().getColor(R.color.thinking)));
            colors.put("communication",Integer.toString(getResources().getColor(R.color.communication)));
            colors.put("application",Integer.toString(getResources().getColor(R.color.application)));
            colors.put("other",Integer.toString(getResources().getColor(R.color.other)));
            int i = 0;
            int rows = 0;
            System.out.println(marks + " <-- MARKS");
            TextView course = findViewById(R.id.subjectTitle);
            course.setText(subject);

            for (String key : marks.keySet()) {

                // add initial descriptive columns
                if (i == 0) {
                    TableRow row = new TableRow(MarksView.this);
                    TextView assignment = new TextView(MarksView.this);
                    assignment.setText("Assignment");
                    assignment.setPadding(5,10,10,5);
                    //assignment.setWidth(100);
                    row.addView(assignment);
                    for (Map<String, String> column : marks.get(key)) {
                        TextView mark_type = new TextView(MarksView.this);
                        String text = column.keySet().iterator().next();
                        text = text.replaceFirst(text.toCharArray()[0]+"",(text.toCharArray()[0]+"").toUpperCase());
                        mark_type.setText(text);
                        mark_type.setPadding(5,10,10,5);
                        //mark_type.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        //mark_type.requestLayout();
                        System.out.println("key = "+column.keySet().iterator().next());
                        mark_type.setBackgroundColor(Integer.parseInt(colors.get(column.keySet().iterator().next())));
                        mark_type.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        row.addView(mark_type);
                        rows++;
                    }
                    row.setBackgroundColor(255);
                    ll.addView(row,i);
                    i++;
                }

                //add marks data
                TableRow row = new TableRow(MarksView.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                TextView assignment = new TextView(MarksView.this);
                assignment.setText(key);
                assignment.setPadding(5,10,10,5);
                //assignment.setWidth(100);
                row.addView(assignment);
                for (Map<String, String> column : marks.get(key)){
                    TextView mark = new TextView(MarksView.this);
                    mark.setPadding(5,10,10,5);
                    String mark_key = column.keySet().iterator().next();
                    String text = column.get(mark_key);
                    mark.setText(text);
                    mark.setBackgroundColor(Integer.parseInt(colors.get(mark_key)));
                    row.addView(mark);
                }
                row.setBackgroundColor(255);
                ll.addView(row,i);
                i++;
            }
            dialog.dismiss();
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
}
