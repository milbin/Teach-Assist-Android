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
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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
    LinkedHashMap<String,List<Map<String,List<String>>>> marks;
    ProgressDialog dialog;
    NavigationView navigationView;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marks_view);
        Crashlytics.setUserIdentifier(username);
        Crashlytics.setString("username", username);
        Crashlytics.setString("password", password);

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


    private class GetMarks extends AsyncTask<String, Integer, LinkedHashMap<String,List<Map<String,List<String>>>>>{
        LineChart lineChart;

        @Override
        protected void onPreExecute(){super.onPreExecute();}

        @Override
        protected LinkedHashMap<String,List<Map<String,List<String>>>> doInBackground(String... temp){
            TA ta = new TA();
            System.out.println(username);
            System.out.println(password);
            ta.GetTAData(username,password);
            System.out.println(subject_number);
            marks = ta.GetMarks(subject_number);
            System.out.println(subject_number);
            subject = ta.GetCourse(subject_number);
            return marks;

        }

        protected void onProgressUpdate(Integer... temp) {
            super.onProgressUpdate();
        }
        @Override
        protected void onPostExecute(LinkedHashMap<String,List<Map<String,List<String>>>> marks) {
            //create table
            TableLayout ll = (TableLayout) findViewById(R.id.marks_table);

            LinkedHashMap<String, String> colors = new LinkedHashMap<>();
            colors.put("knowledge", Integer.toString(getResources().getColor(R.color.knowledge)));
            colors.put("thinking", Integer.toString(getResources().getColor(R.color.thinking)));
            colors.put("communication", Integer.toString(getResources().getColor(R.color.communication)));
            colors.put("application", Integer.toString(getResources().getColor(R.color.application)));
            colors.put("other", Integer.toString(getResources().getColor(R.color.other)));
            int i = 0;
            int rows = 0;
            TextView course = findViewById(R.id.subjectTitle);
            course.setText(subject);

            /*
            for (String key : marks.keySet()) {
                //add assignment as title
                TableRow assignment_row = new TableRow(MarksView.this);
                TextView assignment = new TextView(MarksView.this);
                assignment.setText(key);
                assignment.setPadding(5,10,10,5);
                assignment.setBackgroundColor(getResources().getColor(R.color.White));
                assignment.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                assignment.setTextColor(getResources().getColor(R.color.TextColor));
                assignment_row.addView(assignment);
                ll.addView(assignment_row,i);
                i++;
                //add category dividers
                TableRow categories_row = new TableRow(MarksView.this);
                for (Map<String, String> column : marks.get(key)) {
                    TextView mark_type = new TextView(MarksView.this);
                    String text = column.keySet().iterator().next();
                    text = text.replaceFirst(text.toCharArray()[0]+"",(text.toCharArray()[0]+"").toUpperCase());
                    mark_type.setText(text);
                    mark_type.setPadding(5,10,10,5);
                    //mark_type.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    //mark_type.requestLayout();
                    //System.out.println("key = "+column.keySet().iterator().next());
                    mark_type.setBackgroundColor(Integer.parseInt(colors.get(column.keySet().iterator().next())));
                    mark_type.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    categories_row.addView(mark_type);
                }
                categories_row.setElevation(10);
                ll.addView(categories_row,i);
                i++;
                // add marks underneath
                TableRow marks_row = new TableRow(MarksView.this);
                for (Map<String, String> column : marks.get(key)){
                    TextView mark = new TextView(MarksView.this);
                    mark.setPadding(5,10,10,5);
                    String mark_key = column.keySet().iterator().next();
                    String text = column.get(mark_key);
                    mark.setText(text);
                    mark.setBackgroundColor(Integer.parseInt(colors.get(mark_key)));
                    marks_row.addView(mark);
                }
                ll.addView(marks_row,i);
                i++;

            }
            dialog.dismiss();
            */

            for (String key : marks.keySet()) {
                // add initial descriptive columns
                if (i == 0) {
                    TableRow row = new TableRow(MarksView.this);
                    TextView assignment = new TextView(MarksView.this);
                    assignment.setText("Assignment");
                    assignment.setPadding(5, 10, 10, 5);
                    //assignment.setWidth(100);
                    row.addView(assignment);
                    for (Map<String, List<String>> column : marks.get(key)) {
                        TextView mark_type = new TextView(MarksView.this);
                        String text = column.keySet().iterator().next();
                        text = text.replaceFirst(text.toCharArray()[0] + "", (text.toCharArray()[0] + "").toUpperCase());
                        mark_type.setText(text);
                        mark_type.setPadding(5, 10, 10, 5);
                        //mark_type.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        //mark_type.requestLayout();
                        mark_type.setBackgroundColor(Integer.parseInt(colors.get(column.keySet().iterator().next())));
                        mark_type.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        row.addView(mark_type);
                        rows++;
                    }
                    row.setBackgroundColor(255);
                    ll.addView(row, i);
                    i++;

                }

                //add marks data
                TableRow row = new TableRow(MarksView.this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                TextView assignment = new TextView(MarksView.this);
                assignment.setText(key);
                assignment.setPadding(5, 10, 10, 5);
                //assignment.setWidth(100);
                row.addView(assignment);
                for (Map<String, List<String>> column : marks.get(key)) {
                    TextView mark = new TextView(MarksView.this);
                    mark.setPadding(5, 10, 10, 5);
                    String mark_key = column.keySet().iterator().next();
                    String text = column.get(mark_key).get(0);
                    mark.setText(text);
                    mark.setBackgroundColor(Integer.parseInt(colors.get(mark_key)));
                    row.addView(mark);
                }
                row.setBackgroundColor(255);
                ll.addView(row, i);
                i++;
            }
            dialog.dismiss();

            //create graphs
            int graph_index = 0;
            LinearLayout graphs = (LinearLayout) findViewById(R.id.graphs);
            //go Through every subject
            for (Map<String, List<String>> column : marks.get(marks.keySet().iterator().next())) {
                String subject = column.keySet().iterator().next();

                //lineChart = (LineChart) findViewById(R.id.lineChart);
                lineChart = new LineChart(MarksView.this);
                lineChart.setMinimumHeight(500);
                //lineChart.setMinimumWidth(300);


                ArrayList<String> xAxis = new ArrayList<>();
                ArrayList<Entry> yAxis = new ArrayList<>();

                double x = 0;

                //determine the number of data points (marks given)
                int numDataPoints = 0;
                for (String key : marks.keySet()) {
                    try {
                        Float.parseFloat(marks.get(key).get(0).get(subject).get(0).split("=")[1].split("%")[0]);
                        numDataPoints++;
                    }
                    catch (Exception e) {
                    }
                }

                //add data points to graph
                int index = 0;
                for (String key : marks.keySet()) {
                    try {
                        float y = Float.parseFloat(marks.get(key).get(0).get(subject).get(0).split("=")[1].split("%")[0]);
                        x = x + (x / numDataPoints);
                        yAxis.add(new Entry(y, index));
                        xAxis.add(index, String.valueOf(x));
                        index++;
                    }
                    catch (Exception e) {

                    }
                }
                //convert Array to String[]
                String[] xaxis = new String[xAxis.size()];
                for (int z = 0; z < xAxis.size(); z++) {
                    xaxis[z] = xAxis.get(z);
                }
                //set the data sets to the graph
                ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
                LineDataSet lineDataSet = new LineDataSet(yAxis, subject+" Marks");
                lineDataSet.setColor(Integer.parseInt(colors.get(subject)));
                lineDataSets.add(lineDataSet);

                lineChart.setData(new LineData(xaxis, lineDataSets));

                //lineChart.setVisibleXRangeMaximum(numDataPoints);

                graphs.addView(lineChart,graph_index);
                graph_index++;
            }
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
