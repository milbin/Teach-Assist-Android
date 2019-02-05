package com.teachassist.teachassist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.decimal4j.util.DoubleRounder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;
/*
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String username;
    String password;

    Boolean Refresh = false;
    SwipeRefreshLayout SwipeRefresh;
    private DrawerLayout drawer;
    RelativeLayout relativeLayout;
    RelativeLayout relativeLayout1;
    RelativeLayout relativeLayout2;
    RelativeLayout relativeLayout3;
    RelativeLayout relativeLayout4;
    RelativeLayout relativeLayout5;
    RelativeLayout relativeLayout6;
    LinkedHashMap<String, List<String>> response;
    LinkedHashMap<String, List<String>> settingsResponse;
    List<String> removed = new ArrayList<>();
    ProgressDialog dialog;
    NavigationView navigationView;
    Menu menu;
    Context context = this;
    String subjectMark;
    String subjectMark1;
    String subjectMark2;
    String subjectMark3;
    String subjectMark4;
    String subjectMark5;
    String subjectMark6;
    ImageButton trash;
    ImageButton trash1;
    ImageButton trash2;
    ImageButton trash3;
    ImageButton trash4;
    ImageButton trash5;
    ImageButton trash6;

    ArrayList<Integer> removedCourseIndexes = new ArrayList<>();
    Boolean isEditing = false;

    public static final String CREDENTIALS = "credentials";
    public static final String USERNAME = "USERNAME";
    public static final String RESPONSE = "RESPONSE";
    public static final String PASSWORD = "PASSWORD";
    public static final String REMEMBERME = "REMEMBERME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //progress dialog
        dialog = ProgressDialog.show(MainActivity.this, "",
                "Loading...", true);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto_mono);

        //hide progress bars so that they dont cover text view

        final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar);
        ProgressBarAverage.setVisibility(View.INVISIBLE);
        final RingProgressBar ProgressBarAverage1 =  findViewById(R.id.SubjectBar1);
        ProgressBarAverage1.setVisibility(View.INVISIBLE);
        final RingProgressBar ProgressBarAverage2 =  findViewById(R.id.SubjectBar2);
        ProgressBarAverage2.setVisibility(View.INVISIBLE);
        final RingProgressBar ProgressBarAverage3 =  findViewById(R.id.SubjectBar3);
        ProgressBarAverage3.setVisibility(View.INVISIBLE);
        final RingProgressBar ProgressBarAverage4 =  findViewById(R.id.SubjectBar4);
        ProgressBarAverage4.setVisibility(View.INVISIBLE);
        final RingProgressBar ProgressBarAverage5 =  findViewById(R.id.SubjectBar5);
        ProgressBarAverage5.setVisibility(View.INVISIBLE);
        final RingProgressBar ProgressBarAverage6 =  findViewById(R.id.SubjectBar6);
        ProgressBarAverage6.setVisibility(View.INVISIBLE);





        //intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        Crashlytics.setUserIdentifier(username);
        Crashlytics.setString("username", username);
        Crashlytics.setString("password", password);
        Crashlytics.log(Log.DEBUG, "username", username);
        Crashlytics.log(Log.DEBUG, "password", password);


        //4 course relative layouts

        relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.setOnClickListener(new subject_click());
        relativeLayout1 = findViewById(R.id.relativeLayout1);
        relativeLayout1.setOnClickListener(new subject_click());
        relativeLayout2 = findViewById(R.id.relativeLayout2);
        relativeLayout2.setOnClickListener(new subject_click());
        relativeLayout3 = findViewById(R.id.relativeLayout3);
        relativeLayout3.setOnClickListener(new subject_click());
        relativeLayout4 = findViewById(R.id.relativeLayout4);
        relativeLayout4.setOnClickListener(new subject_click());
        relativeLayout5 = findViewById(R.id.relativeLayout5);
        relativeLayout5.setOnClickListener(new subject_click());
        relativeLayout6 = findViewById(R.id.relativeLayout6);
        relativeLayout6.setOnClickListener(new subject_click());
        relativeLayout.setClickable(true);
        relativeLayout1.setClickable(true);
        relativeLayout2.setClickable(true);
        relativeLayout3.setClickable(true);
        relativeLayout4.setClickable(true);
        relativeLayout5.setClickable(true);
        relativeLayout6.setClickable(true);

        //trash buttons
        trash = findViewById(R.id.trash_can);
        trash1 = findViewById(R.id.trash_can1);
        trash2 = findViewById(R.id.trash_can2);
        trash3 = findViewById(R.id.trash_can3);
        trash4 = findViewById(R.id.trash_can4);
        trash5 = findViewById(R.id.trash_can5);
        trash6 = findViewById(R.id.trash_can6);




        if (Build.VERSION.SDK_INT <= 23) {
            //relativeLayout.setVisibility(View.GONE);
        }


            // Refresh
        SwipeRefresh = findViewById(R.id.swipeRefresh);
        SwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("REFRESH", "onRefresh called from MainActivity");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        Refresh = true;
                        TextView EmptyCourse = findViewById(R.id.EmptyCourse);
                        EmptyCourse.setText("");
                        TextView EmptyCourse1 = findViewById(R.id.EmptyCourse1);
                        EmptyCourse1.setText("");
                        TextView EmptyCourse2 = findViewById(R.id.EmptyCourse2);
                        EmptyCourse2.setText("");
                        TextView EmptyCourse3 = findViewById(R.id.EmptyCourse3);
                        EmptyCourse3.setText("");
                        TextView EmptyCourse4 = findViewById(R.id.EmptyCourse4);
                        EmptyCourse4.setText("");
                        TextView EmptyCourse5 = findViewById(R.id.EmptyCourse5);
                        EmptyCourse5.setText("");
                        TextView EmptyCourse6 = findViewById(R.id.EmptyCourse6);
                        EmptyCourse6.setText("");

                        relativeLayout.setVisibility(View.VISIBLE);
                        relativeLayout1.setVisibility(View.VISIBLE);
                        relativeLayout2.setVisibility(View.VISIBLE);
                        relativeLayout3.setVisibility(View.VISIBLE);
                        relativeLayout.setClickable(true);
                        relativeLayout1.setClickable(true);
                        relativeLayout2.setClickable(true);
                        relativeLayout3.setClickable(true);

                        trash.setVisibility(View.GONE);
                        trash1.setVisibility(View.GONE);
                        trash2.setVisibility(View.GONE);
                        trash3.setVisibility(View.GONE);
                        trash4.setVisibility(View.GONE);
                        trash5.setVisibility(View.GONE);
                        trash6.setVisibility(View.GONE);
                        removedCourseIndexes = new ArrayList<>();
                        new getTaData().execute();

                    }
                }
        );



        //setup toolbar for nav bar drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Nav bar Drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Setup toolbar text
        //  TextView ToolbarText =  findViewById(R.id.toolbar_title);
        //ToolbarText.setText("Student Report for: "+ username);
        getSupportActionBar().setTitle("Student: " + username);

        new getTaData().execute();





    }

    private void showToast(String text){
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    public class subject_click implements View.OnClickListener{

        @Override
        public void onClick(View v){
            Intent myIntent = new Intent(MainActivity.this, MarksViewMaterial.class);
            int subjectNumber = ((LinearLayout) v.getParent()).indexOfChild(v) -2;
            int toSubtract = 0;
            for (int i : removedCourseIndexes) {
                if (i < subjectNumber) {
                    toSubtract++;
                }
            }
            subjectNumber -= toSubtract;
            System.out.println(subjectNumber);
            String subjectMark = "";
            int counter = 0;
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                if(counter == subjectNumber){
                    subjectMark = entry.getValue().get(0);
                }
                counter++;
            }
            myIntent.putExtra("username", username);
            myIntent.putExtra("password", password);
            myIntent.putExtra("subject", subjectNumber+toSubtract);
            myIntent.putExtra("subject Mark", subjectMark);
            startActivity(myIntent);
            dialog.dismiss();

        }
    }

    public class onTrashClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
                TA ta = new TA();
                Double newAverage = -1.0;
                View rlParent = (View) v.getParent();
                String toRemove = "";
                rlParent.setVisibility(View.GONE);
                int toSubtract = 0;
                int courseNum = 0;

                if (rlParent == relativeLayout) {
                    courseNum = 0;
                } else if (rlParent == relativeLayout1) {
                    courseNum = 1;
                } else if (rlParent == relativeLayout2) {
                    courseNum = 2;
                } else if (rlParent == relativeLayout3) {
                    courseNum = 3;
                } else if (rlParent == relativeLayout4) {
                    courseNum = 4;
                }else if (rlParent == relativeLayout5) {
                    courseNum = 5;
                }else if (rlParent == relativeLayout6) {
                    courseNum = 6;
                }

                for (int i : removedCourseIndexes) {
                    if (i < courseNum) {
                        toSubtract++;
                    }
                }
                int counter = 0;
                for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                    if (counter == courseNum - toSubtract) {
                        toRemove = entry.getKey();
                    }
                    counter++;
                }
                removedCourseIndexes.add(courseNum);
                response.remove(toRemove);
                newAverage = ta.GetAverage(response);
                TextView AverageInt = findViewById(R.id.AverageInt);
                AverageInt.setText(newAverage.toString() + "%");
                final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.AverageBar);
                if (newAverage < 1) {
                    ProgressBarAverage.setProgress(1);
                } else {
                    ProgressBarAverage.setProgress((int) Math.round(newAverage));
                }

        }
    }



    // on navigation drawer item selection
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.nav_logout:
                drawer.closeDrawer(Gravity.START);


                SharedPreferences sharedPreferences = getSharedPreferences(CREDENTIALS, MODE_PRIVATE);
                SharedPreferences.Editor editor =   sharedPreferences.edit();
                editor.putString(USERNAME, "");
                editor.putString(PASSWORD, "");
                editor.putBoolean(REMEMBERME, false);
                editor.apply();

                Intent myIntent = new Intent(MainActivity.this, login.class);
                startActivity(myIntent);
                finish();
                break;

            case R.id.nav_home:
                drawer.closeDrawer(Gravity.START);
                break;

            case R.id.nav_settings:
                drawer.closeDrawer(Gravity.START);
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                //add names of courses to list for settings summaries
                ArrayList list = new ArrayList<String>();


                if(response == null){
                    new AlertDialog.Builder(context)
                            .setTitle("Connection Error")
                            .setMessage("Something went Wrong while trying to reach TeachAssist. Please check your internet connection and try again.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("MainActivity", "No internet connection");
                                }
                            })
                            .show();
                    break;
                }
                for (Map.Entry<String, List<String>> entry : settingsResponse.entrySet()) {
                    list.add(entry.getValue().get(1));
                }
                settingsIntent.putStringArrayListExtra("key", list); //Optional parameters

                startActivity(settingsIntent);
                break;

            case R.id.nav_email:
                drawer.closeDrawer(Gravity.START);

                String mailto = "mailto:TaAppYRDSB@gmail.com";

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(mailto));

                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {
                    showToast("No email app currently installed");
                }
                break;

            case R.id.nav_bug_report:
                drawer.closeDrawer(Gravity.START);
                String mailtoBug = "mailto:TaAppYRDSB@gmail.com";

                Intent BugIntent = new Intent(Intent.ACTION_SENDTO);
                BugIntent.setData(Uri.parse(mailtoBug));

                try {
                    startActivity(BugIntent);
                } catch (ActivityNotFoundException e) {
                    showToast("No email app currently installed");
                }
                break;

        }


        return false;
    }

    //2 methods below for edit button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_button, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                if(!isEditing) {
                    trash.setVisibility(View.VISIBLE);
                    trash1.setVisibility(View.VISIBLE);
                    trash2.setVisibility(View.VISIBLE);
                    trash3.setVisibility(View.VISIBLE);
                    trash4.setVisibility(View.VISIBLE);
                    trash5.setVisibility(View.VISIBLE);
                    trash6.setVisibility(View.VISIBLE);

                    trash.setOnClickListener(new onTrashClick());
                    trash1.setOnClickListener(new onTrashClick());
                    trash2.setOnClickListener(new onTrashClick());
                    trash3.setOnClickListener(new onTrashClick());
                    trash4.setOnClickListener(new onTrashClick());
                    trash5.setOnClickListener(new onTrashClick());
                    trash6.setOnClickListener(new onTrashClick());
                    isEditing = true;
                }else{
                    trash.setVisibility(View.GONE);
                    trash1.setVisibility(View.GONE);
                    trash2.setVisibility(View.GONE);
                    trash3.setVisibility(View.GONE);
                    trash4.setVisibility(View.GONE);
                    trash5.setVisibility(View.GONE);
                    trash6.setVisibility(View.GONE);
                    isEditing = false;
                }
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    //close drawer when back button pressed
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    // dismisses dialog so when user resume is smooth
    public void onPause() {
        super.onPause();
        dialog.dismiss();

    }
    public void onResume() {
        super.onResume();

    }




    private class getTaData extends AsyncTask<String, Integer, LinkedHashMap<String, List<String>>>{


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected LinkedHashMap<String, List<String>> doInBackground(String... params){
            TA ta = new TA();

            response = ta.GetTAData(username, password);

            if(response != null) {
                settingsResponse = (LinkedHashMap<String, List<String>>) response.clone(); //stores original response for settings intent, if not clicking on settings will raise exception when you delete courses
            }else{
                new AlertDialog.Builder(context)
                        .setTitle("Connection Error")
                        .setMessage("Something went Wrong while trying to reach TeachAssist. Please check your internet connection and try again.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("MainActivity", "No internet connection");
                            }
                        })
                        .show();
            }
            Gson gson = new Gson();
            String list = gson.toJson(response);
            SharedPreferences sharedPreferences = getSharedPreferences(RESPONSE, MODE_PRIVATE);
            SharedPreferences.Editor editor =   sharedPreferences.edit();
            editor.putString(RESPONSE, list);
            editor.apply();
            return response;

        }

        protected void onProgressUpdate(Integer... progress) {}


        protected void onPostExecute(LinkedHashMap<String, List<String>> response) {
            // Set Average Text
            TA ta = new TA();
            Double average = ta.GetAverage(response);
            //check if connected to internet
            if(average == null || response == null){
                new AlertDialog.Builder(context)
                        .setTitle("Connection Error")
                        .setMessage("Something went Wrong while trying to reach TeachAssist. Please check your internet connection and try again.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("MainActivity", "No internet connection");
                            }
                        })
                        .show();
                relativeLayout.setClickable(false);
                relativeLayout1.setClickable(false);
                relativeLayout2.setClickable(false);
                relativeLayout3.setClickable(false);
                relativeLayout4.setClickable(false);
                relativeLayout5.setClickable(false);
                relativeLayout6.setClickable(false);
                dialog.dismiss();
                return;
            }
            TextView AverageInt = findViewById(R.id.AverageInt);
            AverageInt.setText(String.valueOf(average)+"%");
            System.out.println(response);



            // Set Subject Text
            Float Mark = 0f;
            int counter = 0;
            String SubjectAbrvString = "";
            String SubjectNameString = "";
            String RoomNumber  = "";
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                if(counter == 0) {
                    if (!entry.getKey().contains("NA")) {
                        Mark = Float.parseFloat(entry.getValue().get(0));
                        TextView SubjectInt = findViewById(R.id.SubjectInt);
                        SubjectInt.setText(Mark.toString()+"%");
                        SubjectAbrvString =  entry.getValue().get(1);
                        SubjectNameString =  entry.getValue().get(2);
                        RoomNumber  = entry.getValue().get(3);
                        subjectMark = Mark.toString();
                    }
                    else {
                        SubjectAbrvString = entry.getValue().get(0);
                        SubjectNameString = entry.getValue().get(1);
                        RoomNumber = entry.getValue().get(2);
                    }
                }
                counter++;
            }

            TextView SubjectAbrv = findViewById(R.id.SubjectAbrv);
            SubjectAbrv.setText(SubjectAbrvString);
            TextView SubjectName = findViewById(R.id.SubjectName);
            SubjectName.setText(SubjectNameString);


            TextView roomNumber  = findViewById(R.id.RoomNumber);
            roomNumber .setText("Room " + RoomNumber );
            //Set Subject1 Text
            Float Mark1 = 0f;
            int counter1 = 0;
            String SubjectAbrvString1 = "";
            String SubjectNameString1 = "";
            String RoomNumber1 = "";
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                if(counter1 == 1) {
                    if (!entry.getKey().contains("NA")) {
                        Mark1 = Float.parseFloat(entry.getValue().get(0));
                        TextView SubjectInt1 = findViewById(R.id.SubjectInt1);
                        SubjectInt1.setText(Mark1.toString() + "%");
                        SubjectAbrvString1 = entry.getValue().get(1);
                        SubjectNameString1 = entry.getValue().get(2);
                        RoomNumber1 = entry.getValue().get(3);
                        subjectMark1 = Mark1.toString();
                    }
                    else{
                        SubjectAbrvString1 = entry.getValue().get(0);
                        SubjectNameString1 = entry.getValue().get(1);
                        RoomNumber1 = entry.getValue().get(2);
                    }
                }
                counter1++;
            }

            TextView SubjectAbrv1 = findViewById(R.id.SubjectAbrv1);
            SubjectAbrv1.setText(SubjectAbrvString1);
            TextView SubjectName1 = findViewById(R.id.SubjectName1);
            SubjectName1.setText(SubjectNameString1);
            TextView roomNumber1 = findViewById(R.id.RoomNumber1);
            roomNumber1.setText("Room " + RoomNumber1);


            //Set Subject2 Text
            Float Mark2 = 0f;
            int counter2 = 0;
            String SubjectAbrvString2 = "";
            String SubjectNameString2 = "";
            String RoomNumber2 = "";
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                if(counter2 == 2) {
                    if (!entry.getKey().contains("NA")) {
                        Mark2 = Float.parseFloat(entry.getValue().get(0));
                        TextView SubjectInt2 = findViewById(R.id.SubjectInt2);
                        SubjectInt2.setText(Mark2.toString()+"%");

                        SubjectAbrvString2 =  entry.getValue().get(1);
                        SubjectNameString2 =  entry.getValue().get(2);
                        RoomNumber2 = entry.getValue().get(3);
                        subjectMark2 = Mark2.toString();
                    }
                    else{
                        SubjectAbrvString2 =  entry.getValue().get(0);
                        SubjectNameString2 =  entry.getValue().get(1);
                        RoomNumber2 = entry.getValue().get(2);
                    }
                }
                counter2++;
            }

            TextView SubjectAbrv2 = findViewById(R.id.SubjectAbrv2);
            SubjectAbrv2.setText(SubjectAbrvString2);
            TextView SubjectName2 = findViewById(R.id.SubjectName2);
            SubjectName2.setText(SubjectNameString2);
            TextView roomNumber2 = findViewById(R.id.RoomNumber2);
            roomNumber2.setText("Room " + RoomNumber2);



            //Set Subject3 Text
            Float Mark3 = 0f;
            int counter3 = 0;
            String SubjectAbrvString3 = "";
            String SubjectNameString3 = "";
            String RoomNumber3 = "";
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                if(counter3 == 3) {
                    if (!entry.getKey().contains("NA")) {
                        Mark3 = Float.parseFloat(entry.getValue().get(0));
                        TextView SubjectInt3 = findViewById(R.id.SubjectInt3);
                        SubjectInt3.setText(Mark3.toString() + "%");

                        SubjectAbrvString3 = entry.getValue().get(1);
                        SubjectNameString3 = entry.getValue().get(2);
                        RoomNumber3 = entry.getValue().get(3);
                        subjectMark3 = Mark3.toString();
                    }
                    else{
                        SubjectAbrvString3 = entry.getValue().get(0);
                        SubjectNameString3 = entry.getValue().get(1);
                        RoomNumber3 = entry.getValue().get(2);
                    }
                }
                counter3++;
            }

            TextView SubjectAbrv3 = findViewById(R.id.SubjectAbrv3);
            SubjectAbrv3.setText(SubjectAbrvString3);
            TextView SubjectName3 = findViewById(R.id.SubjectName3);
            SubjectName3.setText(SubjectNameString3);
            TextView roomNumber3 = findViewById(R.id.RoomNumber3);
            roomNumber3.setText("Room " + RoomNumber3);


            if(response.size() > 4) {
                relativeLayout4.setVisibility(View.VISIBLE);
                //Set Subject4 Text
                Float Mark4 = 0f;
                int counter4 = 0;
                String SubjectAbrvString4 = "";
                String SubjectNameString4 = "";
                String RoomNumber4 = "";
                for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                    if (counter4 == 4) {
                        if (!entry.getKey().contains("NA")) {
                            Mark4 = Float.parseFloat(entry.getValue().get(0));
                            TextView SubjectInt4 = findViewById(R.id.SubjectInt4);
                            SubjectInt4.setText(Mark4.toString() + "%");

                            SubjectAbrvString4 = entry.getValue().get(1);
                            SubjectNameString4 = entry.getValue().get(2);
                            RoomNumber4 = entry.getValue().get(3);
                            subjectMark4 = Mark4.toString();
                        } else {
                            SubjectAbrvString4 = entry.getValue().get(0);
                            SubjectNameString4 = entry.getValue().get(1);
                            RoomNumber4 = entry.getValue().get(2);
                        }
                    }
                    counter4++;
                }

                TextView SubjectAbrv4 = findViewById(R.id.SubjectAbrv4);
                SubjectAbrv4.setText(SubjectAbrvString4);
                TextView SubjectName4 = findViewById(R.id.SubjectName4);
                SubjectName4.setText(SubjectNameString4);
                TextView roomNumber4 = findViewById(R.id.RoomNumber4);
                roomNumber4.setText("Room " + RoomNumber4);
            }else{
                relativeLayout4.setVisibility(View.GONE);
            }

            if(response.size() > 5) {
                relativeLayout5.setVisibility(View.VISIBLE);
                //Set Subject5 Text
                Float Mark5 = 0f;
                int counter5 = 0;
                String SubjectAbrvString5 = "";
                String SubjectNameString5 = "";
                String RoomNumber5 = "";
                for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                    if (counter5 == 5) {
                        if (!entry.getKey().contains("NA")) {
                            Mark5 = Float.parseFloat(entry.getValue().get(0));
                            TextView SubjectInt5 = findViewById(R.id.SubjectInt5);
                            SubjectInt5.setText(Mark5.toString() + "%");

                            SubjectAbrvString5 = entry.getValue().get(1);
                            SubjectNameString5 = entry.getValue().get(2);
                            RoomNumber5 = entry.getValue().get(3);
                            subjectMark5 = Mark5.toString();
                        } else {
                            SubjectAbrvString5 = entry.getValue().get(0);
                            SubjectNameString5 = entry.getValue().get(1);
                            RoomNumber5 = entry.getValue().get(2);
                        }
                    }
                    counter5++;
                }

                TextView SubjectAbrv5 = findViewById(R.id.SubjectAbrv5);
                SubjectAbrv5.setText(SubjectAbrvString5);
                TextView SubjectName5 = findViewById(R.id.SubjectName5);
                SubjectName5.setText(SubjectNameString5);
                TextView roomNumber5 = findViewById(R.id.RoomNumber5);
                roomNumber5.setText("Room " + RoomNumber5);
            }else{
                relativeLayout5.setVisibility(View.GONE);
            }

            if(response.size() > 6) {
                relativeLayout6.setVisibility(View.VISIBLE);
                //Set Subject6 Text
                Float Mark6 = 0f;
                int counter6 = 0;
                String SubjectAbrvString6 = "";
                String SubjectNameString6 = "";
                String RoomNumber6 = "";
                for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                    if (counter6 == 6) {
                        if (!entry.getKey().contains("NA")) {
                            Mark6 = Float.parseFloat(entry.getValue().get(0));
                            TextView SubjectInt6 = findViewById(R.id.SubjectInt6);
                            SubjectInt6.setText(Mark6.toString() + "%");

                            SubjectAbrvString6 = entry.getValue().get(1);
                            SubjectNameString6 = entry.getValue().get(2);
                            RoomNumber6 = entry.getValue().get(3);
                            subjectMark6 = Mark6.toString();
                        } else {
                            SubjectAbrvString6 = entry.getValue().get(0);
                            SubjectNameString6 = entry.getValue().get(1);
                            RoomNumber6 = entry.getValue().get(2);
                        }
                    }
                    counter6++;
                }

                TextView SubjectAbrv6 = findViewById(R.id.SubjectAbrv6);
                SubjectAbrv6.setText(SubjectAbrvString6);
                TextView SubjectName6 = findViewById(R.id.SubjectName6);
                SubjectName6.setText(SubjectNameString6);
                TextView roomNumber6 = findViewById(R.id.RoomNumber6);
                roomNumber6.setText("Room " + RoomNumber6);
            }else{
                relativeLayout6.setVisibility(View.GONE);
            }

            dialog.dismiss();
            RunTasks(response);

        }

        private void RunTasks(LinkedHashMap<String, List<String>> response){

            new MainActivity.Average().execute(response);
            new MainActivity.Subject().execute(response);
            new MainActivity.Subject1().execute(response);
            new MainActivity.Subject2().execute(response);
            new MainActivity.Subject3().execute(response);
            if(response.size() > 4) {
                new MainActivity.Subject4().execute(response);
            }
            if(response.size() > 5) {
                new MainActivity.Subject5().execute(response);
            }
            if(response.size() > 6) {
                new MainActivity.Subject6().execute(response);
            }
            if(Refresh.equals(true)) {
                SwipeRefresh.setRefreshing(false);
                Refresh = false;
            }

        }


    }
    //---------------------------------------------------------------------------------------------------------------------------------------
    private class Average extends AsyncTask<HashMap<String, List<String>>, Integer, Float>{
        @Override
        protected void onPreExecute(){


        }

        @Override
        protected Float doInBackground(HashMap<String, List<String>>... response){
            TA ta = new TA();
            double average = ta.GetAverage(response[0]);
            Float Average = (float) average;


            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.AverageBar);
                for (int i = 0; i < Math.round(Average); i+=4) {
                    publishProgress (i);
                    Thread.sleep(0, 50);


                }
                ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                    @Override
                    public void progressToComplete() {
                        // Progress reaches the maximum callback default Max value is 100
                        Toast.makeText(MainActivity.this, "100", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            return Average;

        }

        protected void onProgressUpdate(Integer... progress) {
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.AverageBar);
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Average) {
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.AverageBar);
            ProgressBarAverage.setProgress(Math.round(Average));

        }

    }
    //---------------------------------------------------------------------------------------------------------------------------------------
    private class Subject extends AsyncTask<HashMap<String, List<String>>, Integer, Float> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Float doInBackground(HashMap<String, List<String>>... response){
            TA ta = new TA();

            Float Mark = 0f;
            int counter = 0;
            for (Map.Entry<String, List<String>> entry : response[0].entrySet()) {
                if(counter == 0) {
                    if(!entry.getKey().contains("NA")) {
                        Mark = Float.parseFloat(entry.getValue().get(0));

                    }
                    else {
                        return -1f;

                    }

                }
                counter++;
            }

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar);
                for (int i = 0; i < Math.round(Mark); i+=4) {
                    publishProgress (i);
                    Thread.sleep(0, 50);


                }
                ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                    @Override
                    public void progressToComplete() {
                        // Progress reaches the maximum callback default Max value is 100
                        Toast.makeText(MainActivity.this, "100", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            return Mark;

        }

        protected void onProgressUpdate(Integer... progress) {
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar);
            if(ProgressBarAverage.getVisibility() == View.INVISIBLE){
                ProgressBarAverage.setVisibility(View.VISIBLE);
            }
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {
            if(Mark.equals(-1f)){
                TextView EmptyCourse = findViewById(R.id.EmptyCourse);
                final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar);
                EmptyCourse.setText(R.string.EmptyText);
                relativeLayout.setClickable(false);
            }


        }
    }
    //---------------------------------------------------------------------------------------------------------------------------------------

    private class Subject1 extends AsyncTask<HashMap<String, List<String>>, Integer, Float> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Float doInBackground(HashMap<String, List<String>>... response){
            TA ta = new TA();

            Float Mark = 0f;
            int counter = 0;
            for (Map.Entry<String, List<String>> entry : response[0].entrySet()) {
                if(counter == 1) {
                    if(!entry.getKey().contains("NA")) {
                        Mark = Float.parseFloat(entry.getValue().get(0));
                    }
                    else {
                        return -1f;

                    }

                }
                counter++;
            }

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar1);
                for (int i = 0; i < Math.round(Mark); i+=4) {
                    publishProgress (i);
                    Thread.sleep(0, 50);


                }
                ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                    @Override
                    public void progressToComplete() {
                        // Progress reaches the maximum callback default Max value is 100
                        Toast.makeText(MainActivity.this, "100", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            return Mark;

        }

        protected void onProgressUpdate(Integer... progress) {
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar1);
            if(ProgressBarAverage.getVisibility() == View.INVISIBLE){
                ProgressBarAverage.setVisibility(View.VISIBLE);
            }
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {
            if(Mark.equals(-1f)){
                TextView EmptyCourse = findViewById(R.id.EmptyCourse1);
                final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar1);
                EmptyCourse.setText(R.string.EmptyText);
                relativeLayout1.setClickable(false);
            }


        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------

    private class Subject2 extends AsyncTask<HashMap<String, List<String>>, Integer, Float> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Float doInBackground(HashMap<String, List<String>>... response){
            TA ta = new TA();

            Float Mark = 0f;
            int counter = 0;
            for (Map.Entry<String, List<String>> entry : response[0].entrySet()) {
                if(counter == 2) {
                    if(!entry.getKey().contains("NA")) {
                        Mark = Float.parseFloat(entry.getValue().get(0));

                    }
                    else {
                        return -1f;

                    }


                }
                counter++;
            }

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar2);
                for (int i = 0; i < Math.round(Mark); i+=4) {
                    publishProgress (i);
                    Thread.sleep(0, 50);


                }
                ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                    @Override
                    public void progressToComplete() {
                        // Progress reaches the maximum callback default Max value is 100
                        Toast.makeText(MainActivity.this, "100", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            return Mark;

        }

        protected void onProgressUpdate(Integer... progress) {
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar2);
            if(ProgressBarAverage.getVisibility() == View.INVISIBLE){
                ProgressBarAverage.setVisibility(View.VISIBLE);
            }
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {
            if(Mark.equals(-1f)){
                TextView EmptyCourse = findViewById(R.id.EmptyCourse2);
                final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar2);
                EmptyCourse.setText(R.string.EmptyText);
                relativeLayout2.setClickable(false);
            }


        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------

    private class Subject3 extends AsyncTask<HashMap<String, List<String>>, Integer, Float> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Float doInBackground(HashMap<String, List<String>>... response){
            TA ta = new TA();

            Float Mark = 0f;
            int counter = 0;
            for (Map.Entry<String, List<String>> entry : response[0].entrySet()) {
                if(counter == 3) {
                    if(!entry.getKey().contains("NA")) {
                        Mark = Float.parseFloat(entry.getValue().get(0));

                    }
                    else {
                        return -1f;

                    }
                }
                counter++;
            }

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar3);
                for (int i = 0; i < Math.round(Mark); i+=4) {
                    publishProgress (i);
                    Thread.sleep(0, 50);


                }
                ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                    @Override
                    public void progressToComplete() {
                        // Progress reaches the maximum callback default Max value is 100
                        Toast.makeText(MainActivity.this, "100", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            return Mark;

        }

        protected void onProgressUpdate(Integer... progress) {

            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar3);
            if(ProgressBarAverage.getVisibility() == View.INVISIBLE){
                ProgressBarAverage.setVisibility(View.VISIBLE);
            }
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {
            if(Mark.equals(-1f)){
                TextView EmptyCourse = findViewById(R.id.EmptyCourse3);
                final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar3);
                EmptyCourse.setText(R.string.EmptyText);
                relativeLayout3.setClickable(false);
            }




        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------

    private class Subject4 extends AsyncTask<HashMap<String, List<String>>, Integer, Float> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Float doInBackground(HashMap<String, List<String>>... response){
            TA ta = new TA();

            Float Mark = 0f;
            int counter = 0;
            for (Map.Entry<String, List<String>> entry : response[0].entrySet()) {
                if(counter == 4) {
                    if(!entry.getKey().contains("NA")) {
                        Mark = Float.parseFloat(entry.getValue().get(0));

                    }
                    else {
                        return -1f;

                    }
                }
                counter++;
            }

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar4);
                for (int i = 0; i < Math.round(Mark); i+=4) {
                    publishProgress (i);
                    Thread.sleep(0, 50);


                }
                ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                    @Override
                    public void progressToComplete() {
                        // Progress reaches the maximum callback default Max value is 100
                        Toast.makeText(MainActivity.this, "100", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            return Mark;

        }

        protected void onProgressUpdate(Integer... progress) {

            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar4);
            if(ProgressBarAverage.getVisibility() == View.INVISIBLE){
                ProgressBarAverage.setVisibility(View.VISIBLE);
            }
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {
            if(Mark.equals(-1f)){
                TextView EmptyCourse = findViewById(R.id.EmptyCourse4);
                final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar4);
                EmptyCourse.setText(R.string.EmptyText);
                relativeLayout4.setClickable(false);
            }




        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------

    private class Subject5 extends AsyncTask<HashMap<String, List<String>>, Integer, Float> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Float doInBackground(HashMap<String, List<String>>... response){
            TA ta = new TA();

            Float Mark = 0f;
            int counter = 0;
            for (Map.Entry<String, List<String>> entry : response[0].entrySet()) {
                if(counter == 5) {
                    if(!entry.getKey().contains("NA")) {
                        Mark = Float.parseFloat(entry.getValue().get(0));

                    }
                    else {
                        return -1f;

                    }
                }
                counter++;
            }

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar5);
                for (int i = 0; i < Math.round(Mark); i+=4) {
                    publishProgress (i);
                    Thread.sleep(0, 50);


                }
                ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                    @Override
                    public void progressToComplete() {
                        // Progress reaches the maximum callback default Max value is 100
                        Toast.makeText(MainActivity.this, "100", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            return Mark;

        }

        protected void onProgressUpdate(Integer... progress) {

            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar5);
            if(ProgressBarAverage.getVisibility() == View.INVISIBLE){
                ProgressBarAverage.setVisibility(View.VISIBLE);
            }
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {
            if(Mark.equals(-1f)){
                TextView EmptyCourse = findViewById(R.id.EmptyCourse5);
                final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar5);
                EmptyCourse.setText(R.string.EmptyText);
                relativeLayout5.setClickable(false);
            }




        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------

    private class Subject6 extends AsyncTask<HashMap<String, List<String>>, Integer, Float> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Float doInBackground(HashMap<String, List<String>>... response){
            TA ta = new TA();

            Float Mark = 0f;
            int counter = 0;
            for (Map.Entry<String, List<String>> entry : response[0].entrySet()) {
                if(counter == 6) {
                    if(!entry.getKey().contains("NA")) {
                        Mark = Float.parseFloat(entry.getValue().get(0));

                    }
                    else {
                        return -1f;

                    }
                }
                counter++;
            }

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar6);
                for (int i = 0; i < Math.round(Mark); i+=4) {
                    publishProgress (i);
                    Thread.sleep(0, 50);


                }
                ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                    @Override
                    public void progressToComplete() {
                        // Progress reaches the maximum callback default Max value is 100
                        Toast.makeText(MainActivity.this, "100", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            return Mark;

        }

        protected void onProgressUpdate(Integer... progress) {

            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar6);
            if(ProgressBarAverage.getVisibility() == View.INVISIBLE){
                ProgressBarAverage.setVisibility(View.VISIBLE);
            }
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {
            if(Mark.equals(-1f)){
                TextView EmptyCourse = findViewById(R.id.EmptyCourse6);
                final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar6);
                EmptyCourse.setText(R.string.EmptyText);
                relativeLayout6.setClickable(false);
            }




        }
    }


}
*/