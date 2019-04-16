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
import android.view.LayoutInflater;
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
import com.github.mikephil.charting.utils.EntryXIndexComparator;
import com.google.gson.Gson;

import org.decimal4j.util.DoubleRounder;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String username;
    String password;

    Boolean Refresh = false;
    SwipeRefreshLayout SwipeRefresh;
    private DrawerLayout drawer;
    LinkedHashMap<String, List<String>> response;
    LinkedHashMap<String, List<String>> settingsResponse;
    List<String> removed = new ArrayList<>();
    ProgressDialog dialog;
    NavigationView navigationView;
    Menu menu;
    Context context = (Context) this;
    String subjectMark;
    LinkedList<View> Courses = new LinkedList<View>();

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





        //intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        Crashlytics.setUserIdentifier(username);
        Crashlytics.setString("username", username);
        Crashlytics.setString("password", password);
        Crashlytics.log(Log.DEBUG, "username", username);
        Crashlytics.log(Log.DEBUG, "password", password);



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

                        LinearLayout linearLayout = findViewById(R.id.CourseLinearLayout);
                        for(View rl: Courses) {
                            linearLayout.removeViewAt(2);

                        }
                        removedCourseIndexes = new ArrayList<>();
                        isEditing = false;
                        Courses = new LinkedList<View>();
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
            System.out.println("CLICK");
            Intent myIntent = new Intent(MainActivity.this, MarksViewMaterial.class);
            int subjectNumber = ((LinearLayout) v.getParent()).indexOfChild(v) -2;
            int toSubtract = 0;
            for (int i : removedCourseIndexes) {
                if (i < subjectNumber) {
                    toSubtract++;
                }
            }
            TextView p = v.findViewById(R.id.Period);
            subjectNumber -= toSubtract;
            System.out.println(subjectNumber);
            System.out.println(p.getText());
            System.out.println(toSubtract + "toSubtract");
            String subjectMark = "";
            int counter = 0;
            System.out.println(subjectNumber);
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
            View rlParent = (View) v.getParent().getParent();
            String toRemove = "";
            rlParent.setVisibility(View.GONE);
            int toSubtract = 0;
            int courseNum = 0;

            int count = 0;
            for(View rl: Courses) {
                if (rlParent == rl) {
                    courseNum = count;
                }
                count++;
            }


            for (int i : removedCourseIndexes) {
                if (i < courseNum) {
                    toSubtract++;
                }
            }
            System.out.println(toSubtract);
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
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("MainActivity", "No internet connection");
                                    recreate();
                                }
                            })
                            .show();
                    break;
                }
                for (Map.Entry<String, List<String>> entry : settingsResponse.entrySet()) {
                    try {
                        list.add(entry.getValue().get(1));
                    }catch (IndexOutOfBoundsException e){
                        list.add(entry.getValue().get(0));
                    }
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
                    for(View rl: Courses) {
                        ImageButton trash = rl.findViewById(R.id.trash_can);
                        trash.setVisibility(View.VISIBLE);
                        trash.setOnClickListener(new onTrashClick());
                    }
                    isEditing = true;
                }else{
                    for(View rl: Courses) {
                        ImageButton trash = rl.findViewById(R.id.trash_can);
                        trash.setVisibility(View.GONE);
                        trash.setOnClickListener(new onTrashClick());
                    }
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
        TA ta = new TA();

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected LinkedHashMap<String, List<String>> doInBackground(String... params){


            response = ta.GetTAData(username, password);
            /*
            ArrayList list1 = new ArrayList<>(Arrays.asList("64.2", "AVI3M1-01", "Visual Arts", "169"));
            ArrayList list2 = new ArrayList<>(Arrays.asList("93.7", "SPH3U1-01", "Physics", "167"));
            ArrayList list3 = new ArrayList<>(Arrays.asList("80.6", "FIF3U1-01", "", "214"));
            ArrayList list4 = new ArrayList<>(Arrays.asList("87.1", "MCR3U1-01", "Functions and Relations", "142"));
            ArrayList list5 = new ArrayList<>(Arrays.asList("93.7", "SPH3U1-01", "Physics", "167"));
            ArrayList list6 = new ArrayList<>(Arrays.asList("80.6", "FIF3U1-01", "", "214"));
            ArrayList list7 = new ArrayList<>(Arrays.asList("87.1", "MCR3U1-01", "Functions and Relations", "142"));
            response = new LinkedHashMap<>();
            response.put("283098", list1);
            response.put("283004", list2);
            response.put("283001", list3);
            response.put("283152", list4);
            response.put("283003", list5);
            response.put("283005", list6);
            response.put("283006", list7);*/
            if(response != null) {
                settingsResponse = (LinkedHashMap<String, List<String>>) response.clone(); //stores original response for settings intent, if not clicking on settings will raise exception when you delete courses
            }else{
               return null;
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
            Double average = ta.GetAverage(response);
            //check if connected to internet
            if(average == null || response == null){
                new AlertDialog.Builder(context)
                        .setTitle("Connection Error")
                        .setMessage("Something went Wrong while trying to reach TeachAssist. Please check your internet connection and try again.")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("MainActivity", "No internet connection");
                                recreate();
                            }
                        })
                        .show();
                dialog.dismiss();
                return;
            }
            TextView AverageInt = findViewById(R.id.AverageInt);
            AverageInt.setText(String.valueOf(average)+"%");
            System.out.println(response);

            int periodNum = 1;
            LinearLayout linearLayout = findViewById(R.id.CourseLinearLayout);
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                View relativeLayout = LayoutInflater.from(context).inflate(R.layout.course_layout, null);
                linearLayout.addView(relativeLayout);
                relativeLayout.setOnClickListener(new subject_click());
                Float Mark = 0f;
                String SubjectAbrvString = "";
                String SubjectNameString = "";
                String RoomNumber  = "";
                if (!entry.getKey().contains("NA")) {
                    try {
                        Mark = Float.parseFloat(entry.getValue().get(0));
                    }catch (Exception e){}
                    TextView SubjectInt = relativeLayout.findViewById(R.id.SubjectInt);
                    if(Mark == 100.0){
                        SubjectInt.setText("100%");
                    }else {
                        SubjectInt.setText(Mark.toString() + "%");
                    }
                    try {
                        SubjectAbrvString = entry.getValue().get(1);
                    }catch (Exception e){}
                    try{
                        SubjectNameString =  entry.getValue().get(2);
                    }catch (Exception e){}
                    try{
                        RoomNumber  = entry.getValue().get(3);
                    }catch (Exception e){}
                    subjectMark = Mark.toString();
                }else {
                    try {
                        SubjectAbrvString = entry.getValue().get(0);
                    }catch (Exception e){}
                    try{
                        SubjectNameString =  entry.getValue().get(1);
                    }catch (Exception e){}
                    try{
                        RoomNumber  = entry.getValue().get(2);
                    }catch (Exception e){}
                }
                TextView SubjectAbrv = relativeLayout.findViewById(R.id.SubjectAbrv);
                SubjectAbrv.setText(SubjectAbrvString);
                TextView SubjectName = relativeLayout.findViewById(R.id.SubjectName);
                SubjectName.setText(SubjectNameString);
                TextView roomNumber  = relativeLayout.findViewById(R.id.RoomNumber);
                roomNumber.setText("Room " + RoomNumber );
                TextView period = relativeLayout.findViewById(R.id.Period);
                period.setText("Period "+periodNum+":");

                Courses.add(relativeLayout);
                periodNum++;
            }

            dialog.dismiss();
            RunTasks(response);

        }


        private void RunTasks(LinkedHashMap<String, List<String>> response){
            new MainActivity.Average().execute(response);
            int currentSubject = 0;
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                new MainActivity.Subject().execute(response, currentSubject);
                currentSubject++;
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
    private class Subject extends AsyncTask<Object, Object, Object[]> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Object[] doInBackground(Object... params){
            LinkedHashMap<String, List<String>> response =(LinkedHashMap<String, List<String>>) params[0];
            int currentSubject = (int) params[1];
            if(Courses.size() == 0){ //stop execution because if courses is equal to zero it means view has been refreshed and async task is now useless
                return new Object[]{1f, currentSubject};
            }
            View currentRL = Courses.get(currentSubject);
            Float Mark = 0f;
            int counter = 0;
            for (Map.Entry<String, List<String>> entry :response.entrySet()) {
                if(counter == currentSubject) {
                    if(!entry.getKey().contains("NA")) {

                        Mark = Float.parseFloat(entry.getValue().get(0));

                    }
                    else {
                        return new Object[]{-1f, currentSubject};

                    }

                }
                counter++;
            }

            //try {
                final RingProgressBar ProgressBarAverage = currentRL.findViewById(R.id.SubjectBar);
                ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                    @Override
                    public void progressToComplete() {
                        // Progress reaches the maximum callback default Max value is 100
                        Toast.makeText(MainActivity.this, "100", Toast.LENGTH_SHORT).show();
                    }
                });
                /*
                for (int i = 0; i < Math.round(Mark); i+=4) {
                    publishProgress (i, currentSubject);
                    Thread.sleep(0, 50);
                }*/
                publishProgress(Math.round(Mark), currentSubject);

            //}
            //catch (InterruptedException e){
            //    e.printStackTrace();
            //}
            return new Object[]{Mark, currentSubject};

        }

        protected void onProgressUpdate(Object... params) {
            int currentSubject = (int) params[1];
            View currentRL = Courses.get(currentSubject);
            final RingProgressBar ProgressBarAverage = (RingProgressBar) currentRL.findViewById(R.id.SubjectBar);
            if(ProgressBarAverage.getVisibility() == View.INVISIBLE){
                ProgressBarAverage.setVisibility(View.VISIBLE);
            }
            ProgressBarAverage.setProgress((int)params[0]);

        }

        protected void onPostExecute(Object... params) {
            if(params[0].equals(-1f)){
                int currentSubject = (int) params[1];
                View currentRL = Courses.get(currentSubject);
                TextView EmptyCourse = currentRL.findViewById(R.id.EmptyCourse);
                final RingProgressBar ProgressBarAverage = (RingProgressBar) currentRL.findViewById(R.id.SubjectBar);
                EmptyCourse.setText(R.string.EmptyText);
                currentRL.setClickable(false);
            }


        }
    }

}
