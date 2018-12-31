package com.teachassist.teachassist;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

import static com.teachassist.teachassist.LaunchActivity.CREDENTIALS;
import static com.teachassist.teachassist.LaunchActivity.PASSWORD;
import static com.teachassist.teachassist.LaunchActivity.USERNAME;


public class EditActivity extends AppCompatActivity {

    LinkedHashMap<String, List<String>> response;
    ArrayList<String> removed = new ArrayList();
    FloatingActionButton fab;
    Boolean fab_animated = false;








    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);
        //get username and password
        SharedPreferences sharedPreferences = getSharedPreferences(CREDENTIALS, MODE_PRIVATE);
        String username = sharedPreferences.getString(USERNAME, "");
        String password = sharedPreferences.getString(PASSWORD, "");
        Crashlytics.setUserIdentifier(username);
        Crashlytics.setString("username", username);
        Crashlytics.setString("password", password);
        Crashlytics.log(Log.DEBUG, "username", username);
        Crashlytics.log(Log.DEBUG, "password", password);
        Intent intent = getIntent();

        // get params
        String str=  intent.getStringExtra("key");
        Gson gson = new Gson();
        Type entityType = new TypeToken< LinkedHashMap<String, List<String>>>(){}.getType();
        response = gson.fromJson(str, entityType);

        //hide progress bars so that they dont cover text view
        final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar);
        ProgressBarAverage.setVisibility(View.INVISIBLE);
        final RingProgressBar ProgressBarAverage1 =  findViewById(R.id.SubjectBar1);
        ProgressBarAverage1.setVisibility(View.INVISIBLE);
        final RingProgressBar ProgressBarAverage2 =  findViewById(R.id.SubjectBar2);
        ProgressBarAverage2.setVisibility(View.INVISIBLE);
        final RingProgressBar ProgressBarAverage3 =  findViewById(R.id.SubjectBar3);
        ProgressBarAverage3.setVisibility(View.INVISIBLE);


        //FAB
        fab = findViewById(R.id.fab);
        fab.setOnDragListener(dragListener);




        //setup toolbar for delete button and set title
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        RelativeLayout relativeLayout1 = findViewById(R.id.relativeLayout1);
        RelativeLayout relativeLayout2 = findViewById(R.id.relativeLayout2);
        RelativeLayout relativeLayout3 = findViewById(R.id.relativeLayout3);

        relativeLayout.setOnLongClickListener(longClickListener );
        relativeLayout1.setOnLongClickListener(longClickListener );
        relativeLayout2.setOnLongClickListener(longClickListener );
        relativeLayout3.setOnLongClickListener(longClickListener );


        new GetTaData().execute();



    }




    View.OnLongClickListener longClickListener = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View v){
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder myShadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, myShadowBuilder, v, 0);
            return true;
        }
    };

    View.OnDragListener dragListener = new View.OnDragListener(){

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int dragEvent = event.getAction();

            final View view = (View) event.getLocalState();


            switch (dragEvent) {

                case DragEvent.ACTION_DRAG_STARTED:

                    if (view.getId() == R.id.relativeLayout) {
                        view.setVisibility(View.INVISIBLE);
                    }
                    if (view.getId() == R.id.relativeLayout1) {
                        view.setVisibility(View.INVISIBLE);
                    }
                    if (view.getId() == R.id.relativeLayout2) {
                        view.setVisibility(View.INVISIBLE);
                    }
                    if (view.getId() == R.id.relativeLayout3) {
                        view.setVisibility(View.INVISIBLE);
                    }

                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    fab.animate().scaleYBy(1.25f).scaleXBy(1.25f).setDuration(100).start();
                    fab_animated =  true;
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    fab.animate().scaleYBy(-1.25f).scaleXBy(-1.25f).setDuration(100).start();
                    fab_animated = false;
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    final boolean dropped = event.getResult();
                    if(fab_animated){
                        fab.animate().scaleYBy(-1.25f).scaleXBy(-1.25f).setDuration(100).start();
                        fab_animated = false;
                    }
                    if (dropped) {System.out.println("DROPPED TRUE");}
                    else {


                        if (view.getId() == R.id.relativeLayout) {
                            view.setVisibility(View.VISIBLE);

                        }
                        if (view.getId() == R.id.relativeLayout1) {
                            view.setVisibility(View.VISIBLE);

                        }
                        if (view.getId() == R.id.relativeLayout2) {
                            view.setVisibility(View.VISIBLE);

                        }
                        if (view.getId() == R.id.relativeLayout3) {
                            view.setVisibility(View.VISIBLE);
                        }
                    }

                    break;
                case DragEvent.ACTION_DROP:


                        if (view.getId() == R.id.relativeLayout) {
                            view.setVisibility(View.GONE);
                            removed.add("0");
                        }
                        if (view.getId() == R.id.relativeLayout1) {
                            view.setVisibility(View.GONE);
                            removed.add("1");
                        }
                        if (view.getId() == R.id.relativeLayout2) {
                            view.setVisibility(View.GONE);
                            removed.add("2");
                        }
                        if (view.getId() == R.id.relativeLayout3) {
                            view.setVisibility(View.GONE);
                            removed.add("3");
                        }



                        break;


                    }

                    System.out.print("DROP EVENT TRUE");

                    return true;
            }

    };

    //2 methods below for edit button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.done_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_actionbar:
                Intent intent = new Intent();
                intent.putStringArrayListExtra("list", removed);
                setResult(Activity.RESULT_OK, intent);
                finish();


                //relativeLayout.setVisibility(View.GONE);

                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }









    //------------------------------------------------------------------------------

    private class GetTaData extends AsyncTask<String, Integer, LinkedHashMap<String, List<String>>>{


        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected LinkedHashMap<String, List<String>> doInBackground(String... params){

            return response;

        }

        protected void onProgressUpdate(Integer... progress) {}


        protected void onPostExecute(LinkedHashMap<String, List<String>> response) {

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



            RunTasks(response);

        }

        private void RunTasks(LinkedHashMap<String, List<String>> response){


            new EditActivity.Subject().execute(response);
            new Subject1().execute(response);
            new Subject2().execute(response);
            new Subject3().execute(response);







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


            final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar);
            publishProgress (Math.round(Mark-1));

            ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                @Override
                public void progressToComplete() {
                    // Progress reaches the maximum callback default Max value is 100
                    Toast.makeText(EditActivity.this, "100", Toast.LENGTH_SHORT).show();
                }
            });

            return Mark;
        }

        protected void onProgressUpdate(Integer... progress) {
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar);
            ProgressBarAverage.setVisibility(View.VISIBLE);
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

            final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar1);
            publishProgress (Math.round(Mark-1));

            ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                @Override
                public void progressToComplete() {
                    // Progress reaches the maximum callback default Max value is 100
                    Toast.makeText(EditActivity.this, "100", Toast.LENGTH_SHORT).show();
                }
            });

            return Mark;
        }

        protected void onProgressUpdate(Integer... progress) {
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar1);
            ProgressBarAverage.setVisibility(View.VISIBLE);
            ProgressBarAverage.setProgress(progress[0]);
        }
        @Override
        protected void onPostExecute(Float Mark) {
            if(Mark.equals(-1f)){
                TextView EmptyCourse = findViewById(R.id.EmptyCourse1);
                final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar1);
                EmptyCourse.setText(R.string.EmptyText);
            }
            else{
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar1);
                ProgressBarAverage.setVisibility(View.VISIBLE);
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

            final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar2);
            publishProgress (Math.round(Mark-1));

            ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                @Override
                public void progressToComplete() {
                    // Progress reaches the maximum callback default Max value is 100
                    Toast.makeText(EditActivity.this, "100", Toast.LENGTH_SHORT).show();
                }
            });

            return Mark;
        }

        protected void onProgressUpdate(Integer... progress) {
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar2);
            ProgressBarAverage.setVisibility(View.VISIBLE);
            ProgressBarAverage.setProgress(progress[0]);
        }
        @Override
        protected void onPostExecute(Float Mark) {
            if(Mark.equals(-1f)){
                TextView EmptyCourse = findViewById(R.id.EmptyCourse2);
                final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar2);
                EmptyCourse.setText(R.string.EmptyText);
            }
            else{
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar2);
                ProgressBarAverage.setVisibility(View.VISIBLE);
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

            final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar3);
            publishProgress (Math.round(Mark-1));

            ProgressBarAverage.setOnProgressListener(new RingProgressBar.OnProgressListener() {
                @Override
                public void progressToComplete() {
                    // Progress reaches the maximum callback default Max value is 100
                    Toast.makeText(EditActivity.this, "100", Toast.LENGTH_SHORT).show();
                }
            });

            return Mark;
        }

        protected void onProgressUpdate(Integer... progress) {
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.SubjectBar3);
            ProgressBarAverage.setVisibility(View.VISIBLE);
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
            else{
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar3);
                ProgressBarAverage.setVisibility(View.VISIBLE);
            }


        }
    }
}


