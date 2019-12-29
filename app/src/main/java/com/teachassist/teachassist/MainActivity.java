package com.teachassist.teachassist;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.utils.EntryXIndexComparator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.decimal4j.util.DoubleRounder;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(sharedPreferences.getBoolean("lightThemeEnabled", false)){
            setTheme(R.style.LightTheme);
        }else{
            setTheme(R.style.DarkTheme);
        }
        setContentView(R.layout.activity_main);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("test", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        System.out.println("TOKEN "+token);
                    }
                });


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
        TextView ToolbarText =  findViewById(R.id.toolbar_title);
        ToolbarText.setText("Student: "+ username);
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setTitle("Student: " + username);

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
            TextView p = v.findViewById(R.id.Period);
            subjectNumber -= toSubtract;
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
            Crashlytics.log(Log.DEBUG, "subject Mark", subjectMark);
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
                drawer.closeDrawer(GravityCompat.START);


                SharedPreferences sharedPreferences = getSharedPreferences(CREDENTIALS, MODE_PRIVATE);
                SharedPreferences.Editor editor =   sharedPreferences.edit();
                editor.putString(USERNAME, "");
                editor.putString(PASSWORD, "");
                editor.putBoolean(REMEMBERME, false);
                editor.apply();

                //logout of firebase
                FirebaseAuth.getInstance().signOut();

                //register for notifications if not already registered
                SharedPreferences sharedPreferencesNotifications = getSharedPreferences("notifications", MODE_PRIVATE);
                String token = sharedPreferencesNotifications.getString("token", "");

                if(!token.equals("")) {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("purpose", "delete");
                        json.put("token", token);
                        json.put("auth", "TAAPPYRDSB123!PASSWORD");
                        new unregisterFromNotificationServer().execute(json);
                        SharedPreferences.Editor editorNotifications =   sharedPreferencesNotifications.edit();
                        editorNotifications.putBoolean("hasRegistered", false);
                        editorNotifications.apply();
                    }catch (Exception e){}

                }

                Intent myIntent = new Intent(MainActivity.this, login.class);
                startActivity(myIntent);
                finish();
                break;

            case R.id.nav_settings:
                drawer.closeDrawer(GravityCompat.START);
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                //add names of courses to list for settings summaries
                ArrayList list = new ArrayList<String>();


                if(response == null){
                    new AlertDialog.Builder(context)
                            .setTitle("Connection Error")
                            .setMessage("Something went wrong while trying to reach TeachAssist. Please check your internet connection and try again.")
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

            case R.id.nav_bug_report:
                Auth auth1 = new Auth();
                drawer.closeDrawer(GravityCompat.START);
                String mailtoBug = "mailto:taappyrdsb@gmail.com" +
                        "?subject=" + Uri.encode("") +
                        "&body=" + Uri.encode(" \n \n Support Token (please do not delete): \n" +
                        auth1.getSupportToken(username, password));

                Intent BugIntent = new Intent(Intent.ACTION_SENDTO);
                BugIntent.setData(Uri.parse(mailtoBug));

                try {
                    startActivity(BugIntent);
                } catch (ActivityNotFoundException e) {
                    showToast("No email app currently installed");
                }
                break;

            case R.id.nav_open_in:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://ta.yrdsb.ca/yrdsb/"));
                startActivity(intent);
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


            //register for notifications if not already registered
            final SharedPreferences sharedPreferencesNotifications = getSharedPreferences("notifications", MODE_PRIVATE);
            String token = sharedPreferencesNotifications.getString("token", "");
            boolean hasRegistered = sharedPreferencesNotifications.getBoolean("hasRegistered", false);
            System.out.println(hasRegistered +"HAS REGISTERED");
            if(!token.equals("") && !hasRegistered) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("username", username);
                    json.put("password", password);
                    json.put("purpose", "register");
                    json.put("token", token);
                    json.put("platform", "ANDROID");
                    json.put("auth", "TAAPPYRDSB123!PASSWORD");
                    SendRequest sr = new SendRequest();
                    /*if(sr.sendJsonNotifications("https://benjamintran.me/TeachassistAPI/", json.toString()) != null){
                        SharedPreferences.Editor editorNotifications =   sharedPreferencesNotifications.edit();
                        editorNotifications.putBoolean("hasRegistered", true);
                        editorNotifications.apply();
                    }*/

                }catch (Exception e){}
            } else if(token.equals("")){
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    return;
                                }
                                // Get new Instance ID token
                                String token = task.getResult().getToken();

                                SharedPreferences.Editor editorNotifications =   sharedPreferencesNotifications.edit();
                                editorNotifications.putString("token", token);
                                editorNotifications.apply();
                            }
                        });
            }

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
            if(response.size() == 0){
                findViewById(R.id.noCoursesTV).setVisibility(View.VISIBLE);
                TextView AverageInt = findViewById(R.id.AverageInt);
                AverageInt.setText("");
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
                    TextView EmptyCourse = relativeLayout.findViewById(R.id.EmptyCourse);
                    final View ProgressBarAverage = relativeLayout.findViewById(R.id.SubjectBar);
                    ProgressBarAverage.setVisibility(View.GONE);
                    EmptyCourse.setText(R.string.EmptyText);
                    relativeLayout.setClickable(false);
                }
                TextView SubjectAbrv = relativeLayout.findViewById(R.id.SubjectAbrv);
                SubjectAbrv.setText(SubjectAbrvString);
                TextView SubjectName = relativeLayout.findViewById(R.id.SubjectName);
                SubjectName.setText(SubjectNameString);
                TextView roomNumber  = relativeLayout.findViewById(R.id.RoomNumber);
                roomNumber.setText("— Rm " + RoomNumber );
                TextView period = relativeLayout.findViewById(R.id.Period);
                period.setText("Period "+ periodNum);

                Courses.add(relativeLayout);
                periodNum++;
            }

            dialog.dismiss();

            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            if(!sharedPreferences.getBoolean("hasShownPopup", false)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("hasShownPopup", true);
                editor.apply();
                // custom popup dialog
                final Dialog dialog1 = new Dialog(context);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog1.setContentView(R.layout.custom_dialog);
                Button dialogButton = (Button) dialog1.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                    }
                });
                dialog1.show();
            }
            RunTasks(response);
        }


        private void RunTasks(final LinkedHashMap<String, List<String>> response){
            final int animationTimeInMs = 10;
           //animate overall average
            new Thread(new Runnable(){
                public void run() {
                    TA ta = new TA();
                    int roundedAvg = (int) Math.round(ta.GetAverage(response));
                    final RingProgressBar ProgressBarAverage = findViewById(R.id.AverageBar);

                    try {
                        for (int i = 0; i < roundedAvg; i += 2) {
                            ProgressBarAverage.setProgress(i);
                            Thread.sleep(animationTimeInMs);
                        }
                        ProgressBarAverage.setProgress(roundedAvg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            //animate subjects
            int currentSubject = 0;
            for (final Map.Entry<String, List<String>> entry : response.entrySet()) {
                final int thisSubject=currentSubject;
                currentSubject++;

                new Thread(new Runnable(){
                    public void run() {
                        //get view & average
                        final View currentRL = Courses.get(thisSubject);
                        final RingProgressBar ProgressBarAverage = currentRL.findViewById(R.id.SubjectBar);
                        ProgressBarAverage.setVisibility(View.VISIBLE);
                        int roundedCourseAvg=0;
                        if(!entry.getKey().contains("NA")) {
                            roundedCourseAvg = (int)Float.parseFloat(entry.getValue().get(0));
                        }

                        //animate
                        try {
                            for (int i = 0; i < roundedCourseAvg; i += 2) {
                                ProgressBarAverage.setProgress(i);
                                Thread.sleep(animationTimeInMs);
                            }
                            ProgressBarAverage.setProgress(roundedCourseAvg);
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            if(Refresh.equals(true)) {
                SwipeRefresh.setRefreshing(false);
                Refresh = false;
            }
        }


    }
    private class unregisterFromNotificationServer extends AsyncTask<JSONObject, Object, Object[]> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Object[] doInBackground(JSONObject... params){
            try {
                //SendRequest sr = new SendRequest();
                //sr.sendJsonNotifications("https://benjamintran.me/TeachassistAPI/", params[0].toString());
            }catch (Exception e){}
            return null;

        }

        protected void onProgressUpdate(Object... params) {

        }

        protected void onPostExecute(Object... params) {


        }
    }

}
