package com.teachassist.teachassist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.charts.LineChart;

import org.decimal4j.util.DoubleRounder;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class MarksViewMaterial extends AppCompatActivity {
    private Context mContext;
    LinearLayout linearLayout;
    JSONObject Marks;
    String username;
    String password;
    int subject_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Request window feature action bar
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marks_card_view);

        // Get the application context
        mContext = getApplicationContext();

        //get intents
        Intent intent = getIntent();
        username = intent.getStringExtra("username").replaceAll("\\s+", "");
        password = intent.getStringExtra("password").replaceAll("\\s+", "");
        subject_number = intent.getIntExtra("subject", 0);
        Crashlytics.setUserIdentifier(username);
        Crashlytics.setString("username", username);
        Crashlytics.setString("password", password);
        Crashlytics.log(Log.DEBUG, "username", username);
        Crashlytics.log(Log.DEBUG, "password", password);
        new GetMarks().execute();
    }

        private class GetMarks extends AsyncTask<String, Integer, JSONObject> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(String... temp) {
                TA ta = new TA();
                ta.GetTADataNotifications(username, password);
                Marks = ta.newGetMarks(subject_number);
                return Marks;


            }

            protected void onProgressUpdate(Integer... temp) {
                super.onProgressUpdate();
            }
            protected void onPostExecute(JSONObject marks) {
                int length = marks.length();
                String title;
                String feedback;
                Double Kweight = 0.0;
                Double Kmark = 0.0;
                Double Tweight;
                Double Tmark = 0.0;
                Double Cweight;
                Double Cmark = 0.0;
                Double Aweight;
                Double Amark = 0.0;
                DecimalFormat round = new DecimalFormat(".#");

                for(int i = 0; i <length-1; i++){
                    Kweight = 0.0;
                    Kmark = 0.0;
                    Tweight = 0.0;
                    Tmark = 0.0;
                    Cweight = 0.0;
                    Cmark = 0.0;
                    Aweight = 0.0;
                    Amark = 0.0;
                    try {
                        JSONObject assignment = marks.getJSONObject(String.valueOf(i));
                        title = assignment.getString("title");
                        feedback = assignment.getString("feedback");
                        if(assignment.has("K")) {
                            if (assignment.getJSONObject("K").getString("weight").isEmpty()) {
                                Kweight = 0.0;
                            } else {
                                Kweight = Double.parseDouble(assignment.getJSONObject("K").getString("weight"));
                            }
                            if (assignment.getJSONObject("K").getString("outOf").equals("0") || assignment.getJSONObject("K").getString("outOf").equals("0.0")) {
                                Kweight = 0.0;
                                Kmark = 0.0;
                            }else {
                                Kmark = Double.parseDouble(assignment.getJSONObject("K").getString("mark")) /
                                        Double.parseDouble(assignment.getJSONObject("K").getString("outOf"));
                                Kmark = Double.parseDouble(round.format(Kmark));
                            }
                        }
                        if(assignment.has("T")) {
                            if (assignment.getJSONObject("T").getString("weight").isEmpty()) {
                                Tweight = 0.0;
                            } else {
                                Tweight = Double.parseDouble(assignment.getJSONObject("T").getString("weight"));
                            }
                            if (assignment.getJSONObject("T").getString("outOf").equals("0") || assignment.getJSONObject("T").getString("outOf").equals("0.0")) {
                                Tweight = 0.0;
                                Tmark = 0.0;
                            }else {
                                Tmark = Double.parseDouble(assignment.getJSONObject("T").getString("mark")) /
                                        Double.parseDouble(assignment.getJSONObject("T").getString("outOf"));
                                Tmark = Double.parseDouble(round.format(Tmark));
                            }
                        }
                        if(assignment.has("C")) {
                            if (assignment.getJSONObject("C").getString("weight").isEmpty()) {
                                Cweight = 0.0;
                            } else {
                                Cweight = Double.parseDouble(assignment.getJSONObject("C").getString("weight"));
                            }
                            if (assignment.getJSONObject("C").getString("outOf").equals("0") || assignment.getJSONObject("C").getString("outOf").equals("0.0")) {
                                Cweight = 0.0;
                                Cmark = 0.0;
                            }else {
                                Cmark = Double.parseDouble(assignment.getJSONObject("C").getString("mark")) /
                                        Double.parseDouble(assignment.getJSONObject("C").getString("outOf"));
                                Cmark = Double.parseDouble(round.format(Cmark));
                            }
                        }
                        if(assignment.has("A")) {
                            if (assignment.getJSONObject("A").getString("weight").isEmpty()) {
                                Aweight = 0.0;
                            } else {
                                Aweight = Double.parseDouble(assignment.getJSONObject("A").getString("weight"));
                            }
                            if (assignment.getJSONObject("A").getString("outOf").equals("0") || assignment.getJSONObject("A").getString("outOf").equals("0.0")) {
                                Aweight = 0.0;
                                Amark = 0.0;
                            }else {
                                Amark = Double.parseDouble(assignment.getJSONObject("A").getString("mark")) /
                                        Double.parseDouble(assignment.getJSONObject("A").getString("outOf"));
                                Amark = Double.parseDouble(round.format(Amark));
                            }
                        }



                        LinearLayout linearLayout = findViewById(R.id.LinearLayoutMarksView);
                        View rl = LayoutInflater.from(mContext).inflate(R.layout.marks_view_assignment, null);
                        linearLayout.addView(rl);

                        //set title
                        TextView Title = rl.findViewById(R.id.title);
                        Title.setText(title);

                        //set mark
                        System.out.println(i);
                        TextView Average = rl.findViewById(R.id.AveragePercent);
                        String returnval = CalculateAverage(marks, String.valueOf(i));
                        Average.setText(returnval+"%");

                        //set bars
                        View bar1 = rl.findViewById(R.id.BarGraph1);
                        View bar2 = rl.findViewById(R.id.BarGraph2);
                        View bar3 = rl.findViewById(R.id.BarGraph3);
                        View bar4 = rl.findViewById(R.id.BarGraph4);
                        bar1.getLayoutParams().height = (int)Math.round(1.5*(Kmark*100));
                        bar2.getLayoutParams().height = (int)Math.round(1.5*(Tmark*100));
                        bar3.getLayoutParams().height = (int)Math.round(1.5*(Cmark*100));
                        bar4.getLayoutParams().height = (int)Math.round(1.5*(Amark*100));

                        //set percentage texts
                        TextView Kpercent = rl.findViewById(R.id.Kpercent);
                        TextView Tpercent = rl.findViewById(R.id.Tpercent);
                        TextView Cpercent = rl.findViewById(R.id.Cpercent);
                        TextView Apercent = rl.findViewById(R.id.Apercent);
                        TextView K = rl.findViewById(R.id.K);
                        TextView T = rl.findViewById(R.id.T);
                        TextView C = rl.findViewById(R.id.C);
                        TextView A = rl.findViewById(R.id.A);
                        if(Kmark == 1.0){
                            Kpercent.setText(String.valueOf(Math.round(Kmark*100)));
                        }else if(Kmark == 0.0){
                            K.setText("");
                        }else {
                            Kpercent.setText(String.valueOf(Kmark * 100));
                        }

                        if(Tmark == 1.0){
                            Tpercent.setText(String.valueOf(Math.round(Tmark*100)));
                        }else if(Tmark == 0.0){
                            T.setText("");
                        }else {
                            Tpercent.setText(String.valueOf(Tmark * 100));
                        }

                        if(Cmark == 1.0){
                            Cpercent.setText(String.valueOf(Math.round(Cmark*100)));
                        }else if(Cmark == 0.0){
                            C.setText("");
                        }else {
                            Cpercent.setText(String.valueOf(Cmark * 100));
                        }

                        if(Amark == 1.0){
                            Apercent.setText(String.valueOf(Math.round(Amark*100)));
                        }else if(Amark == 0.0){
                            A.setText("");
                        }else {
                            Apercent.setText(String.valueOf(Amark * 100));
                        }

/*
                        // Creating a new RelativeLayout
                        RelativeLayout rl = new RelativeLayout(mContext);

                        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT);
                        rlp.setMargins(0, 10, 0, 4);
                        rlp.setMarginStart(10);
                        rlp.setMarginEnd(10);
                        rl.setLayoutParams(rlp);
                        rl.setBackgroundColor(R.drawable.button_bg_rectangle_solid_marks_view);
                        rl.setMinimumHeight(R.dimen.Row_Height);
                        rl.setGravity(Gravity.TOP);
                        rl.setElevation(3);


                        TextView TVtitle = new TextView(mContext);
                        TVtitle.setText(title);
                        TVtitle.setPadding(10, 10, 18, 0);
                        TVtitle.setTextColor(getResources().getColor(R.color.DarkGray));
                        TVtitle.setTextSize(16);

                        */



                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        }

        private String CalculateAverage(JSONObject marks, String assingmentNumber){
        try {
            JSONObject weights = marks.getJSONObject("categories");
            Double weightK = weights.getDouble("K")*10;
            Double weightT = weights.getDouble("T")*10;
            Double weightC = weights.getDouble("C")*10;
            Double weightA = weights.getDouble("A")*10;
            Double Kmark = 0.0;
            Double Tmark = 0.0;
            Double Cmark = 0.0;
            Double Amark = 0.0;
            DecimalFormat round = new DecimalFormat(".#");
            JSONObject assignment = marks.getJSONObject(assingmentNumber);

            if(assignment.has("K")) {
                if (!assignment.getJSONObject("K").getString("outOf").equals("0") || !assignment.getJSONObject("K").getString("outOf").equals("0.0")) {
                    Kmark = Double.parseDouble(assignment.getJSONObject("K").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("K").getString("outOf"));
                }
            }else{
                    weightK = 0.0;
                }
            if(assignment.has("T")) {
                if (!assignment.getJSONObject("T").getString("outOf").equals("0") || !assignment.getJSONObject("T").getString("outOf").equals("0.0")) {
                    Tmark = Double.parseDouble(assignment.getJSONObject("T").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("T").getString("outOf"));
                }
            }else{
                    weightT = 0.0;
                }
            if(assignment.has("C")) {
                if (!assignment.getJSONObject("C").getString("outOf").equals("0") || !assignment.getJSONObject("C").getString("outOf").equals("0.0")) {
                    Cmark = Double.parseDouble(assignment.getJSONObject("C").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("C").getString("outOf"));
                }
            }else{
                    weightC = 0.0;
                }
            if(assignment.has("A")) {
                if (!assignment.getJSONObject("A").getString("outOf").equals("0") || !assignment.getJSONObject("A").getString("outOf").equals("0.0")) {
                    Amark = Double.parseDouble(assignment.getJSONObject("A").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("A").getString("outOf"));
                }
            }else{
                    weightA = 0.0;
                }

            Kmark*=weightK;
            Tmark*=weightT;
            Cmark*=weightC;
            Amark*=weightA;
            String Average = round.format((Kmark+Tmark+Cmark+Amark)/(weightK+weightT+weightC+weightA)*100);
            return Average;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}
