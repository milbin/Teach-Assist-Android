package com.teachassist.teachassist;

import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.AsyncTask;

import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
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
        Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto_mono);

        String username = "335525168";
        String password = "4a6349kc";


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
            TextView ToolbarText =  findViewById(R.id.toolbar_title);
            ToolbarText.setText("Student Report for: "+ username);




            LinkedHashMap<String, List<String>> response = ta.GetTAData(username, password);

            return response;

        }

        protected void onProgressUpdate(Integer... progress) {}


        protected void onPostExecute(LinkedHashMap<String, List<String>> response) {
            // Set Average Text
            System.out.println(response);
            TA ta = new TA();
            double average = ta.GetAverage(response);
            Float Average = (float) average;
            TextView AverageInt = findViewById(R.id.AverageInt);
            AverageInt.setText(Average.toString()+"%");



            // Set Subject Text
            Float Mark = 0f;
            int counter = 0;
            String SubjectAbrvString = "";
            String SubjectNameString = "";
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                if(counter == 0) {
                    Mark = Float.parseFloat(entry.getValue().get(0));
                    SubjectAbrvString =  entry.getValue().get(1);
                    SubjectNameString =  entry.getValue().get(2);
                }
                counter++;
            }
            TextView SubjectInt = findViewById(R.id.SubjectInt);
            SubjectInt.setText(Mark.toString()+"%");
            TextView SubjectAbrv = findViewById(R.id.SubjectAbrv);
            SubjectAbrv.setText(SubjectAbrvString);
            TextView SubjectName = findViewById(R.id.SubjectName);
            SubjectName.setText(SubjectNameString);


            //Set Subject1 Text
            Float Mark1 = 0f;
            int counter1 = 0;
            String SubjectAbrvString1 = "";
            String SubjectNameString1 = "";
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                if(counter1 == 1) {
                    Mark1 = Float.parseFloat(entry.getValue().get(0));
                    SubjectAbrvString1 =  entry.getValue().get(1);
                    SubjectNameString1 =  entry.getValue().get(2);
                }
                counter1++;
            }
            TextView SubjectInt1 = findViewById(R.id.SubjectInt1);
            SubjectInt1.setText(Mark1.toString()+"%");
            TextView SubjectAbrv1 = findViewById(R.id.SubjectAbrv1);
            SubjectAbrv1.setText(SubjectAbrvString1);
            TextView SubjectName1 = findViewById(R.id.SubjectName1);
            SubjectName1.setText(SubjectNameString1);


            //Set Subject2 Text
            Float Mark2 = 0f;
            int counter2 = 0;
            String SubjectAbrvString2 = "";
            String SubjectNameString2 = "";
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                if(counter2 == 2) {
                    Mark2 = Float.parseFloat(entry.getValue().get(0));
                    SubjectAbrvString2 =  entry.getValue().get(1);
                    SubjectNameString2 =  entry.getValue().get(2);
                }
                counter2++;
            }
            TextView SubjectInt2 = findViewById(R.id.SubjectInt2);
            SubjectInt2.setText(Mark2.toString()+"%");
            TextView SubjectAbrv2 = findViewById(R.id.SubjectAbrv2);
            SubjectAbrv2.setText(SubjectAbrvString2);
            TextView SubjectName2 = findViewById(R.id.SubjectName2);
            SubjectName2.setText(SubjectNameString2);


            //Set Subject3 Text
            Float Mark3 = 0f;
            int counter3 = 0;
            String SubjectAbrvString3 = "";
            String SubjectNameString3 = "";
            for (Map.Entry<String, List<String>> entry : response.entrySet()) {
                if(counter3 == 3) {
                    Mark3 = Float.parseFloat(entry.getValue().get(0));
                    SubjectAbrvString3 =  entry.getValue().get(1);
                    SubjectNameString3 =  entry.getValue().get(2);
                }
                counter3++;
            }
            TextView SubjectInt3 = findViewById(R.id.SubjectInt3);
            SubjectInt3.setText(Mark3.toString()+"%");
            TextView SubjectAbrv3 = findViewById(R.id.SubjectAbrv3);
            SubjectAbrv3.setText(SubjectAbrvString3);
            TextView SubjectName3 = findViewById(R.id.SubjectName3);
            SubjectName3.setText(SubjectNameString3);



            RunTasks(response);

        }

        private void RunTasks(LinkedHashMap<String, List<String>> response){

                new Average().execute(response);

                new MainActivity.Subject().execute(response);
                new Subject1().execute(response);
                new Subject2().execute(response);
                new Subject3().execute(response);

            /*
            MainActivity.Subject subject = new MainActivity.Subject();
                subject.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response);


                Subject1 subject1 = new Subject1();
                subject1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response);


                Subject2 subject2 = new Subject2();
                subject2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response);


                Subject3 subject3 = new Subject3();
                subject3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response);
                */




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
            for (Map.Entry<String, List<String>> entry : response[0].entrySet()) {
                Mark =  Float.parseFloat(entry.getValue().get(0));

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
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {


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
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {


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
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {


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
            ProgressBarAverage.setProgress(progress[0]);

        }
        @Override
        protected void onPostExecute(Float Mark) {


        }
    }


}

