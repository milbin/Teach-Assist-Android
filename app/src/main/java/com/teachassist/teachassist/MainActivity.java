package com.teachassist.teachassist;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Dimension;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.net.URLConnection;
import java.net.URL;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String username = "335525168";
        String password = "416349kc";
        new GetTaData().execute(username, password);

    }







    private class GetTaData extends AsyncTask<String, Integer, LinkedHashMap<String, List<String>>>{
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected LinkedHashMap<String, List<String>> doInBackground(String... params){
            TA ta = new TA();
            String username = params[0];
            String password = params[1];
            LinkedHashMap<String, List<String>> response = ta.GetTAData(username, password);

            return response;

        }

        protected void onProgressUpdate(Integer... progress) {}


        protected void onPostExecute(LinkedHashMap<String, List<String>> response) {
            RunTasks(response);

        }
        private void RunTasks(LinkedHashMap<String, List<String>> response){
            new Average().execute(response);

            Subject subject = new Subject();
            subject.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,  response);

            Subject1 subject1 = new Subject1();
            subject1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,  response);

            Subject2 subject2 = new Subject2();
            subject2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,  response);

            Subject3 subject3 = new Subject3();
            subject3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,  response);
        }

    }
    //---------------------------------------------------------------------------------------------------------------------------------------
    private class Average extends AsyncTask<HashMap<String, List<String>>, Integer, Float>{
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Float doInBackground(HashMap<String, List<String>>... response){
            TA ta = new TA();
            double average = ta.GetAverage(response[0]);
            float Average = (float) average;

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.AverageBar);
                for (int i = 0; i < Math.round(Average); i+=3) {
                    publishProgress (i);
                    Thread.sleep(1);


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
                            //ProgressBarAnimation anim = new ProgressBarAnimation(ProgressBarAverage, 0, Average);
                //anim.setDuration(1000);
                //ProgressBarAverage.startAnimation(anim);
                TextView AverageInt = (TextView) findViewById(R.id.AverageInt);
                AverageInt.setText(Average.toString());

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
            for (Map.Entry<String, List<String>> entry : response[0].entrySet()) {
                Mark =  Float.parseFloat(entry.getValue().get(0));

            }

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar);
                for (int i = 0; i < Math.round(Mark); i+=3) {
                    publishProgress (i);
                    Thread.sleep(1);


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
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {
            //ProgressBarAnimation anim = new ProgressBarAnimation(ProgressBarAverage, 0, Average);
            //anim.setDuration(1000);
            //ProgressBarAverage.startAnimation(anim);
            TextView AverageInt = (TextView) findViewById(R.id.SubjectInt);
            AverageInt.setText(Mark.toString());

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
                    Mark = Float.parseFloat(entry.getValue().get(0));

                }
                counter++;
            }

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar1);
                for (int i = 0; i < Math.round(Mark); i+=3) {
                    publishProgress (i);
                    Thread.sleep(1);


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
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {
            //ProgressBarAnimation anim = new ProgressBarAnimation(ProgressBarAverage, 0, Average);
            //anim.setDuration(1000);
            //ProgressBarAverage.startAnimation(anim);
            TextView AverageInt = (TextView) findViewById(R.id.SubjectInt1);
            AverageInt.setText(Mark.toString());

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
                    Mark = Float.parseFloat(entry.getValue().get(0));

                }
                counter++;
            }

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar2);
                for (int i = 0; i < Math.round(Mark); i+=3) {
                    publishProgress (i);
                    Thread.sleep(1);


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
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {
            //ProgressBarAnimation anim = new ProgressBarAnimation(ProgressBarAverage, 0, Average);
            //anim.setDuration(1000);
            //ProgressBarAverage.startAnimation(anim);
            TextView AverageInt = (TextView) findViewById(R.id.SubjectInt2);
            AverageInt.setText(Mark.toString());

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
                    Mark = Float.parseFloat(entry.getValue().get(0));
                }
                counter++;
            }

            try {
                final RingProgressBar ProgressBarAverage =  findViewById(R.id.SubjectBar3);
                for (int i = 0; i < Math.round(Mark); i+=3) {
                    publishProgress (i);
                    Thread.sleep(1);


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
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {
            //ProgressBarAnimation anim = new ProgressBarAnimation(ProgressBarAverage, 0, Average);
            //anim.setDuration(1000);
            //ProgressBarAverage.startAnimation(anim);
            TextView AverageInt = (TextView) findViewById(R.id.SubjectInt3);
            AverageInt.setText(Mark.toString());

        }
    }


}

