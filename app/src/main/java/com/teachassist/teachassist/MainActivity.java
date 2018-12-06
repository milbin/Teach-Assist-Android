package com.teachassist.teachassist;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;

import android.provider.Settings;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //String username = "335525168";
    //String password = "4a6349kc";

    //String username = login.getUser();
    //String password = login.getPass();
    String username;
    String password;

    Boolean Refresh = false;
    SwipeRefreshLayout SwipeRefresh;
    private DrawerLayout drawer;
    RelativeLayout relativeLayout;
    RelativeLayout relativeLayout1;
    RelativeLayout relativeLayout2;
    RelativeLayout relativeLayout3;
    LinkedHashMap<String, List<String>> response;
    List<String> removed = new ArrayList<>();
    ProgressDialog dialog;
    NavigationView navigationView;
    Menu menu;

    public static final String SHARED_PREFS = "credentials";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String REMEMBERME = "REMEMBERME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);


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



        //intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");


        //4 course relative layouts
        relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout1 = findViewById(R.id.relativeLayout1);
        relativeLayout2 = findViewById(R.id.relativeLayout2);
        relativeLayout3 = findViewById(R.id.relativeLayout3);



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
                        relativeLayout.setVisibility(View.VISIBLE);
                        relativeLayout1.setVisibility(View.VISIBLE);
                        relativeLayout2.setVisibility(View.VISIBLE);
                        relativeLayout3.setVisibility(View.VISIBLE);
                        String Username = username;
                        String Password = password;
                        new GetTaData().execute(Username, Password);

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


        String Username = username;
        String Password = password;
        new GetTaData().execute(Username, Password);





    }

    private void showToast(String text){
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    // on navigation drawer item selection
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.nav_logout:
                drawer.closeDrawer(Gravity.START);


                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor =   sharedPreferences.edit();
                editor.putString(USERNAME, "");
                editor.putString(PASSWORD, "");
                editor.putBoolean(REMEMBERME, false);

                Intent myIntent = new Intent(MainActivity.this, login.class);
                startActivity(myIntent);
                break;

            case R.id.nav_home:
                drawer.closeDrawer(Gravity.START);
                break;

            case R.id.nav_settings:
                drawer.closeDrawer(Gravity.START);
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

            case R.id.nav_email:
                drawer.closeDrawer(Gravity.START);

                String mailto = "mailto:Benjamintran0684@gmail.com";

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
                String mailtoBug = "mailto:Benjamintran0684@gmail.com";

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

                Intent myIntent = new Intent(MainActivity.this, EditActivity.class);
                Gson gson = new Gson();
                String list = gson.toJson(response);
                myIntent.putExtra("key", list); //Optional parameters
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                MainActivity.this.startActivityForResult(myIntent, 10101); //random int i set
                //relativeLayout.setVisibility(View.GONE);

                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    //show and hide menu so that app does not crash is user presses edit button before main view is fully loaded
    public void showMenu(boolean show){
        if(menu == null){
            System.out.println("NULL MENU");
            return;
        }
        menu.setGroupVisible(R.id.main_menu_group, show);
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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10101: // see MainActivity.this.startActivityForResult(myIntent, 10101); line(140)
                if (resultCode == Activity.RESULT_OK) {
                    removed = data.getStringArrayListExtra("list");
                    System.out.println(removed);
                    if(removed.isEmpty()){
                        //do nothing
                    }
                    else{
                        ArrayList Empty_course_list = new ArrayList();
                        double average = 0;

                        int counter = 0;
                        int y = 0;
                        for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                            if (!entry.getKey().contains("NA")) {
                                y++;
                                counter++;

                            }
                            else{
                                Empty_course_list.add(counter);
                                counter++;
                            }
                        }


                        int size = y - removed.size()+Empty_course_list.size();
                        List<Double> grades = new ArrayList<>();

                        System.out.println(removed);


                        if(removed.contains("0")){
                            relativeLayout.setVisibility(View.GONE);
                        }
                        else {
                            relativeLayout.setVisibility(View.VISIBLE);
                            int x = 0;
                            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                                if (x == 0) {
                                    grades.add(Double.parseDouble(entry.getValue().get(0)));
                                }
                                x++;
                            }
                        }
                        if(removed.contains("1")){
                            relativeLayout1.setVisibility(View.GONE);
                        }
                        else {
                            relativeLayout1.setVisibility(View.VISIBLE);
                            int x = 0;
                            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                                if (x == 1) {
                                    grades.add(Double.parseDouble(entry.getValue().get(0)));
                                }
                                x++;
                            }
                        }
                        if(removed.contains("2")){
                            relativeLayout2.setVisibility(View.GONE);
                        }
                        else {
                            relativeLayout2.setVisibility(View.VISIBLE);
                            int x = 0;
                            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                                if (x == 2) {
                                    grades.add(Double.parseDouble(entry.getValue().get(0)));
                                }
                                x++;
                            }
                        }
                        if(removed.contains("3")){
                            relativeLayout3.setVisibility(View.GONE);
                        }
                        else {
                            relativeLayout3.setVisibility(View.VISIBLE);
                            int x = 0;
                            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                                if (x == 3) {
                                    grades.add(Double.parseDouble(entry.getValue().get(0)));
                                }
                                x++;
                            }
                        }



                        for (double value : grades) {
                            average += value;

                        }
                        System.out.println(size);
                        average = DoubleRounder.round(average / size, 1);
                        Float Average = (float) average;
                        TextView AverageInt = findViewById(R.id.AverageInt);
                        AverageInt.setText(Average.toString() + "%");
                        final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.AverageBar);
                        ProgressBarAverage.setProgress(Math.round(Average));
                    }


                }

        }

    }



    private class GetTaData extends AsyncTask<String, Integer, LinkedHashMap<String, List<String>>>{


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected LinkedHashMap<String, List<String>> doInBackground(String... params){
            TA ta = new TA();
            String Username = params[0];
            String Password = params[1];

            response = ta.GetTAData(Username, Password);
            //ta.GetMarks("283003");

            return response;

        }

        protected void onProgressUpdate(Integer... progress) {}


        protected void onPostExecute(LinkedHashMap<String, List<String>> response) {
            // Set Average Text
            TA ta = new TA();
            double average = ta.GetAverage(response);
            Float Average = (float) average;
            TextView AverageInt = findViewById(R.id.AverageInt);
            AverageInt.setText(Average.toString()+"%");
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

            dialog.dismiss();
            RunTasks(response);

        }

        private void RunTasks(LinkedHashMap<String, List<String>> response){

                new Average().execute(response);
                new MainActivity.Subject().execute(response);
                new Subject1().execute(response);
                new Subject2().execute(response);
                new Subject3().execute(response);
                //hide menu
                showMenu(false);
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
                System.out.println(Mark);
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
            if(ProgressBarAverage.getVisibility() == View.INVISIBLE) {
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
                //TODO: invisibility lags behind, some text is cut off for a second before invisibility kicks in
            }
            //hide menu
            showMenu(true);


        }
    }




}

