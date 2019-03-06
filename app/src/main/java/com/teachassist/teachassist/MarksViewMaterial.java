package com.teachassist.teachassist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;


public class MarksViewMaterial extends AppCompatActivity {
    private Context mContext;
    LinearLayout linearLayout;
    Menu menu;
    JSONObject Marks;
    String username;
    String password;
    int subject_number;
    String CourseName;
    String Mark;
    ProgressDialog dialog;
    Context context = this;
    ArrayList<View> rlList = new ArrayList<>();
    int numberOfAssignments;
    int numberOfRemovedAssignments;
    Boolean trashShown = false;
    ArrayList<Integer> removedAssignmentIndexList = new ArrayList<>();
    LinkedHashMap<String, Integer> assignmentIndex = new LinkedHashMap<>();
    int original_height_of_assignment = -1;
    SwipeRefreshLayout SwipeRefresh;
    private AsyncTask getMarksTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Request window feature action bar
        //requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marks_card_view);

        //progress dialog
        dialog = ProgressDialog.show(MarksViewMaterial.this, "",
                "Loading...", true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);//back button


        // Get the application context
        mContext = getApplicationContext();

        //get intents
        Intent intent = getIntent();
        username = intent.getStringExtra("username").replaceAll("\\s+", "");
        password = intent.getStringExtra("password").replaceAll("\\s+", "");
        subject_number = intent.getIntExtra("subject", 0);
        Mark = intent.getStringExtra("subject Mark");
        Crashlytics.setUserIdentifier(username);
        Crashlytics.setString("username", username);
        Crashlytics.setString("password", password);
        Crashlytics.log(Log.DEBUG, "username", username);
        Crashlytics.log(Log.DEBUG, "password", password);

        linearLayout = findViewById(R.id.LinearLayoutMarksView);

        SwipeRefresh = findViewById(R.id.swipeRefresh);
        SwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Intent intent = getIntent();
                        Mark = intent.getStringExtra("subject Mark");
                        int length = linearLayout.getChildCount();
                        for (int i = 1; i < length; i++) {
                            linearLayout.removeViewAt(1);
                        }
                        removedAssignmentIndexList = new ArrayList<>();
                        assignmentIndex = new LinkedHashMap<>();
                        rlList = new ArrayList<>();
                        trashShown = false;
                        getMarksTask = new GetMarks().execute();
                        SwipeRefresh.setRefreshing(false);
                    }});

        //instantiate this class to cancel it later if the back button is pressed
        getMarksTask = new GetMarks().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                getMarksTask.cancel(true);
                return true;
            case R.id.action_edit:
                if(!trashShown) {
                    for (int i = 0; i < linearLayout.getChildCount(); i++) {
                        View v = linearLayout.getChildAt(i);
                        if (v instanceof RelativeLayout) {
                            try {
                                ImageButton trashButton = (ImageButton) v.findViewById(R.id.trash_can);
                                trashButton.setVisibility(View.VISIBLE);
                                trashShown = true;
                            } catch (Exception e) {
                            }
                        }
                    }
                }else{
                    for (int i = 0; i < linearLayout.getChildCount(); i++) {
                        View v = linearLayout.getChildAt(i);
                        if (v instanceof RelativeLayout) {
                            try {
                                ImageButton trashButton = (ImageButton) v.findViewById(R.id.trash_can);
                                trashButton.setVisibility(View.INVISIBLE);
                                trashShown = false;
                            } catch (Exception e) {
                            }
                        }
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //without this overide if there is no internet the activity will raise an error because you are showing the no internet dialog when it has already been destroyed
    @Override
    public void onBackPressed() {
        System.out.println("OVERRIDE");
        getMarksTask.cancel(true);
        super.onBackPressed();
    }

    //2 methods below for edit button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_button, menu);
        return super.onCreateOptionsMenu(menu);
    }





    private class GetMarks extends AsyncTask<String, Integer, JSONObject> {
        View rl;

        @Override
        protected void onPreExecute() {
            if(Mark == null){
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
                dialog.dismiss();
                return;
            }
            TextView AverageInt = findViewById(R.id.AverageInt);
            AverageInt.setText(Mark+"%");
            if(Mark.contains("NA") && !Mark.contains("NaN")){
                finish();
            }
            int Average = Math.round(Float.parseFloat(Mark.replaceAll("%", "")));
            final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.AverageBar);
            ProgressBarAverage.setProgress(Average);
        }

        @Override
        protected JSONObject doInBackground(String... temp) {
            TA ta = new TA();
            ta.GetTAData(username, password);
            List<JSONObject> returnValue = ta.newGetMarks(subject_number);
            if(returnValue == null){
                Crashlytics.log(Log.ERROR, "network request failed", "line 184 MVM");
                return null;
            }
            Marks = returnValue.get(0);
            try {
                CourseName = returnValue.get(1).getString("course");
            }catch (JSONException e){
                e.printStackTrace();
            }
            return Marks;


        }

        protected void onProgressUpdate(Integer... temp) {
            super.onProgressUpdate();
        }
        protected void onPostExecute(JSONObject marks){
            if(!isCancelled()) {
                if (marks == null) {
                    new AlertDialog.Builder(context)
                            .setTitle("Connection Error")
                            .setMessage("Something went Wrong while trying to reach TeachAssist. Please check your internet connection and try again.")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    recreate();
                                }
                            })
                            .show();
                    dialog.dismiss();
                    return;
                }
                numberOfAssignments = marks.length() - 1;
                String title;
                DecimalFormat round = new DecimalFormat(".#");

                for (int i = 0; i < numberOfAssignments; i++) {
                    Double Kmark = 0.000000001;
                    Double Tmark = 0.000000001;
                    Double Cmark = 0.000000001;
                    Double Amark = 0.000000001;
                    final Double Kweight;
                    final Double Tweight;
                    final Double Cweight;
                    final Double Aweight;
                    final Double Oweight;
                    final String feedback;
                    final String finalStringKFraction;
                    final String finalStringTFraction;
                    final String finalStringCFraction;
                    final String finalStringAFraction;
                    String stringKFraction = "";
                    String stringTFraction = "";
                    String stringCFraction = "";
                    String stringAFraction = "";
                    rl = LayoutInflater.from(mContext).inflate(R.layout.marks_view_assignment, null);
                    linearLayout.addView(rl);
                    rlList.add(rl);
                    try {
                        final JSONObject assignment = marks.getJSONObject(String.valueOf(i));
                        if (assignment.has("K") && assignment.getJSONObject("K").getString("mark").equals("no mark") ||
                                assignment.has("T") && assignment.getJSONObject("T").getString("mark").equals("no mark") ||
                                assignment.has("C") && assignment.getJSONObject("C").getString("mark").equals("no mark") ||
                                assignment.has("A") && assignment.getJSONObject("A").getString("mark").equals("no mark")) {
                            title = assignment.getString("title");
                            feedback = "";
                            Kweight = 0.0;
                            Tweight = 0.0;
                            Cweight = 0.0;
                            Aweight = 0.0;
                            Oweight = 0.0;
                        } else {

                            title = assignment.getString("title");
                            feedback = assignment.getString("feedback").replaceAll("\n", "");

                            if (assignment.has("K")) {
                                if (assignment.getJSONObject("K").getString("weight").isEmpty()) {
                                    Kweight = 0.0;
                                } else {
                                    Kweight = Double.parseDouble(assignment.getJSONObject("K").getString("weight"));
                                }
                                if (assignment.getJSONObject("K").getString("outOf").equals("0") || assignment.getJSONObject("K").getString("outOf").equals("0.0")) {
                                    Kmark = 0.0;
                                } else {
                                    if (!assignment.getJSONObject("K").isNull("mark") && !assignment.getJSONObject("K").isNull("outOf")) {
                                        Kmark = Double.parseDouble(assignment.getJSONObject("K").getString("mark")) /
                                                Double.parseDouble(assignment.getJSONObject("K").getString("outOf"));
                                        Kmark = Double.parseDouble(round.format(Kmark * 100));
                                    }
                                }
                                if (assignment.getJSONObject("K").getString("mark").isEmpty()) {
                                    stringKFraction = "0/";
                                } else {
                                    stringKFraction = assignment.getJSONObject("K").getString("mark") + "/";
                                }
                                if (assignment.getJSONObject("K").getString("outOf").isEmpty()) {
                                    stringKFraction += "0";
                                } else {
                                    stringKFraction += assignment.getJSONObject("K").getString("outOf");
                                }
                            } else {
                                Kweight = 0.0;
                            }
                            if (assignment.has("T")) {
                                if (assignment.getJSONObject("T").getString("weight").isEmpty()) {
                                    Tweight = 0.0;
                                } else {
                                    Tweight = Double.parseDouble(assignment.getJSONObject("T").getString("weight"));
                                }
                                if (assignment.getJSONObject("T").getString("outOf").equals("0") || assignment.getJSONObject("T").getString("outOf").equals("0.0")) {
                                    Tmark = 0.0;
                                } else {
                                    if (!assignment.getJSONObject("T").isNull("mark") && !assignment.getJSONObject("T").isNull("outOf")) {
                                        Tmark = Double.parseDouble(assignment.getJSONObject("T").getString("mark")) /
                                                Double.parseDouble(assignment.getJSONObject("T").getString("outOf"));
                                        Tmark = Double.parseDouble(round.format(Tmark * 100));
                                    }
                                }
                                if (assignment.getJSONObject("T").getString("mark").isEmpty()) {
                                    stringTFraction = "0/";
                                } else {
                                    stringTFraction = assignment.getJSONObject("T").getString("mark") + "/";
                                }
                                if (assignment.getJSONObject("T").getString("outOf").isEmpty()) {
                                    stringTFraction += "0";
                                } else {
                                    stringTFraction += assignment.getJSONObject("T").getString("outOf");
                                }
                            } else {
                                Tweight = 0.0;
                            }
                            if (assignment.has("C")) {
                                if (assignment.getJSONObject("C").getString("weight").isEmpty()) {
                                    Cweight = 0.0;
                                } else {
                                    Cweight = Double.parseDouble(assignment.getJSONObject("C").getString("weight"));
                                }
                                if (assignment.getJSONObject("C").getString("outOf").equals("0") || assignment.getJSONObject("C").getString("outOf").equals("0.0")) {
                                    Cmark = 0.0;
                                } else {
                                    if (!assignment.getJSONObject("C").isNull("mark") && !assignment.getJSONObject("C").isNull("outOf")) {
                                        Cmark = Double.parseDouble(assignment.getJSONObject("C").getString("mark")) /
                                                Double.parseDouble(assignment.getJSONObject("C").getString("outOf"));
                                        Cmark = Double.parseDouble(round.format(Cmark * 100));
                                    }
                                }
                                if (assignment.getJSONObject("C").getString("mark").isEmpty()) {
                                    stringCFraction = "0/";
                                } else {
                                    stringCFraction = assignment.getJSONObject("C").getString("mark") + "/";
                                }
                                if (assignment.getJSONObject("C").getString("outOf").isEmpty()) {
                                    stringCFraction += "0";
                                } else {
                                    stringCFraction += assignment.getJSONObject("C").getString("outOf");
                                }
                            } else {
                                Cweight = 0.0;
                            }
                            if (assignment.has("A")) {
                                if (assignment.getJSONObject("A").getString("weight").isEmpty()) {
                                    Aweight = 0.0;
                                } else {
                                    Aweight = Double.parseDouble(assignment.getJSONObject("A").getString("weight"));
                                }
                                if (assignment.getJSONObject("A").getString("outOf").equals("0") || assignment.getJSONObject("A").getString("outOf").equals("0.0")) {
                                    Amark = 0.0;
                                } else {
                                    if (!assignment.getJSONObject("A").isNull("mark") && !assignment.getJSONObject("A").isNull("outOf")) {
                                        Amark = Double.parseDouble(assignment.getJSONObject("A").getString("mark")) /
                                                Double.parseDouble(assignment.getJSONObject("A").getString("outOf"));
                                        Amark = Double.parseDouble(round.format(Amark * 100));
                                    }
                                }
                                if (assignment.getJSONObject("A").getString("mark").isEmpty()) {
                                    stringAFraction = "0/";
                                } else {
                                    stringAFraction = assignment.getJSONObject("A").getString("mark") + "/";
                                }
                                if (assignment.getJSONObject("A").getString("outOf").isEmpty()) {
                                    stringAFraction += "0";
                                } else {
                                    stringAFraction += assignment.getJSONObject("A").getString("outOf");
                                }
                            } else {
                                Aweight = 0.0;
                            }

                            if (assignment.has("")) {
                                if (assignment.getJSONObject("").getString("weight").isEmpty()) {
                                    Oweight = 0.0;
                                } else {
                                    Oweight = Double.parseDouble(assignment.getJSONObject("").getString("weight"));
                                }
                            } else {
                                Oweight = 0.0;
                            }
                        }

                        finalStringKFraction = stringKFraction;
                        finalStringTFraction = stringTFraction;
                        finalStringCFraction = stringCFraction;
                        finalStringAFraction = stringAFraction;

                        final TextView KWeight = new TextView(context);
                        final TextView TWeight = new TextView(context);
                        final TextView CWeight = new TextView(context);
                        final TextView AWeight = new TextView(context);
                        final TextView feedbackTextView = new TextView(context);
                        final TextView markFractionK = new TextView(context);
                        final TextView markFractionT = new TextView(context);
                        final TextView markFractionC = new TextView(context);
                        final TextView markFractionA = new TextView(context);

                        rl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RelativeLayout rlNested = v.findViewById(R.id.relativeLayout_marks_view);
                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlNested.getLayoutParams();
                                if (original_height_of_assignment == -1) {
                                    original_height_of_assignment = rlNested.getHeight();
                                }
                                if (rlNested.getHeight() == original_height_of_assignment) {
                                    params.height = (int) Math.round(rlNested.getHeight() * 2.3 + v.findViewById(R.id.AveragePercent).getHeight() + feedback.length() / 40 * 20);

                                    rlNested.setLayoutParams(params);

                                    View bar1 = v.findViewById(R.id.BarGraph1);
                                    View bar2 = v.findViewById(R.id.BarGraph2);
                                    View bar3 = v.findViewById(R.id.BarGraph3);
                                    View bar4 = v.findViewById(R.id.BarGraph4);

                                    RelativeLayout barsRL = v.findViewById(R.id.mark_bars);
                                    RelativeLayout.LayoutParams barsRLParams = (RelativeLayout.LayoutParams) barsRL.getLayoutParams();
                                    barsRLParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                    barsRLParams.height = barsRL.getHeight() * 3;
                                    barsRLParams.addRule(RelativeLayout.BELOW, R.id.AveragePercent);
                                    barsRLParams.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
                                    barsRL.setLayoutParams(barsRLParams);

                                    RelativeLayout.LayoutParams paramsKweight = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    KWeight.setId(View.generateViewId());
                                    paramsKweight.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    paramsKweight.addRule(RelativeLayout.ALIGN_START, R.id.BarGraph1);
                                    KWeight.setLayoutParams(paramsKweight);
                                    KWeight.setText("W: " + String.valueOf(Kweight));
                                    barsRL.addView(KWeight);

                                    RelativeLayout.LayoutParams paramsTweight = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    TWeight.setId(View.generateViewId());
                                    paramsTweight.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    paramsTweight.addRule(RelativeLayout.ALIGN_START, R.id.BarGraph2);
                                    TWeight.setLayoutParams(paramsTweight);
                                    TWeight.setText("W: " + String.valueOf(Tweight));
                                    barsRL.addView(TWeight);

                                    RelativeLayout.LayoutParams paramsCweight = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    CWeight.setId(View.generateViewId());
                                    paramsCweight.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    paramsCweight.addRule(RelativeLayout.ALIGN_START, R.id.BarGraph3);
                                    CWeight.setLayoutParams(paramsCweight);
                                    CWeight.setText("W: " + String.valueOf(Cweight));
                                    barsRL.addView(CWeight);

                                    RelativeLayout.LayoutParams paramsAweight = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    AWeight.setId(View.generateViewId());
                                    paramsAweight.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    paramsAweight.addRule(RelativeLayout.ALIGN_START, R.id.BarGraph4);
                                    AWeight.setLayoutParams(paramsAweight);
                                    AWeight.setText("W: " + String.valueOf(Aweight));
                                    barsRL.addView(AWeight);

                                    RelativeLayout.LayoutParams layoutParamsBar1 = (RelativeLayout.LayoutParams) bar1.getLayoutParams();
                                    layoutParamsBar1.height = (int) Math.round(bar1.getHeight() * 2.3);
                                    layoutParamsBar1.width = (int) Math.round(bar1.getWidth() * 2);
                                    layoutParamsBar1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                                    layoutParamsBar1.addRule(RelativeLayout.ABOVE, KWeight.getId());
                                    layoutParamsBar1.setMarginStart(0);
                                    bar1.setLayoutParams(layoutParamsBar1);

                                    RelativeLayout.LayoutParams layoutParamsBar2 = (RelativeLayout.LayoutParams) bar2.getLayoutParams();
                                    layoutParamsBar2.height = (int) Math.round(bar2.getHeight() * 2.3);
                                    layoutParamsBar2.width = (int) Math.round(bar2.getWidth() * 2);
                                    layoutParamsBar2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                                    layoutParamsBar2.addRule(RelativeLayout.ABOVE, TWeight.getId());
                                    layoutParamsBar2.setMarginStart(30);
                                    bar2.setLayoutParams(layoutParamsBar2);

                                    RelativeLayout.LayoutParams layoutParamsBar3 = (RelativeLayout.LayoutParams) bar3.getLayoutParams();
                                    layoutParamsBar3.height = (int) Math.round(bar3.getHeight() * 2.3);
                                    layoutParamsBar3.width = (int) Math.round(bar3.getWidth() * 2);
                                    layoutParamsBar3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                                    layoutParamsBar3.addRule(RelativeLayout.ABOVE, CWeight.getId());
                                    layoutParamsBar3.setMarginStart(30);
                                    bar3.setLayoutParams(layoutParamsBar3);

                                    RelativeLayout.LayoutParams layoutParamsBar4 = (RelativeLayout.LayoutParams) bar4.getLayoutParams();
                                    layoutParamsBar4.height = (int) Math.round(bar4.getHeight() * 2.3);
                                    layoutParamsBar4.width = (int) Math.round(bar4.getWidth() * 2);
                                    layoutParamsBar4.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                                    layoutParamsBar4.addRule(RelativeLayout.ABOVE, AWeight.getId());
                                    layoutParamsBar4.setMarginStart(30);
                                    bar4.setLayoutParams(layoutParamsBar4);

                                    TextView AveragePercent = v.findViewById(R.id.AveragePercent);
                                    RelativeLayout.LayoutParams paramsAveragePercent = (RelativeLayout.LayoutParams) AveragePercent.getLayoutParams();
                                    paramsAveragePercent.addRule(RelativeLayout.CENTER_HORIZONTAL);


                                    feedbackTextView.setId(View.generateViewId()); //without these IDs the bars float in the air for some reason
                                    RelativeLayout.LayoutParams paramsFeedback = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    paramsFeedback.addRule(RelativeLayout.BELOW, R.id.AveragePercent);
                                    paramsFeedback.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                    paramsFeedback.setMarginStart(20);
                                    feedbackTextView.setLayoutParams(paramsFeedback);
                                    feedbackTextView.setText("Feedback: " + feedback);
                                    rlNested.addView(feedbackTextView);

                                    markFractionK.setText(finalStringKFraction);
                                    markFractionT.setText(finalStringTFraction);
                                    markFractionC.setText(finalStringCFraction);
                                    markFractionA.setText(finalStringAFraction);
                                    markFractionK.setId(View.generateViewId());
                                    markFractionT.setId(View.generateViewId());
                                    markFractionC.setId(View.generateViewId());
                                    markFractionA.setId(View.generateViewId());
                                    markFractionK.setTextColor(getResources().getColor(R.color.White));
                                    markFractionT.setTextColor(getResources().getColor(R.color.White));
                                    markFractionC.setTextColor(getResources().getColor(R.color.White));
                                    markFractionA.setTextColor(getResources().getColor(R.color.White));

                                    RelativeLayout.LayoutParams markFractionKLP = new RelativeLayout.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                                    RelativeLayout.LayoutParams markFractionTLP = new RelativeLayout.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                                    RelativeLayout.LayoutParams markFractionCLP = new RelativeLayout.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                                    RelativeLayout.LayoutParams markFractionALP = new RelativeLayout.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT);

                                    markFractionKLP.addRule(RelativeLayout.BELOW, R.id.Kpercent);
                                    markFractionTLP.addRule(RelativeLayout.BELOW, R.id.Tpercent);
                                    markFractionCLP.addRule(RelativeLayout.BELOW, R.id.Cpercent);
                                    markFractionALP.addRule(RelativeLayout.BELOW, R.id.Apercent);
                                    markFractionKLP.addRule(RelativeLayout.ALIGN_START, R.id.Kpercent);
                                    markFractionTLP.addRule(RelativeLayout.ALIGN_START, R.id.Tpercent);
                                    markFractionCLP.addRule(RelativeLayout.ALIGN_START, R.id.Cpercent);
                                    markFractionALP.addRule(RelativeLayout.ALIGN_START, R.id.Apercent);
                                    markFractionK.setLayoutParams(markFractionKLP);
                                    markFractionT.setLayoutParams(markFractionTLP);
                                    markFractionC.setLayoutParams(markFractionCLP);
                                    markFractionA.setLayoutParams(markFractionALP);

                                    barsRL.addView(markFractionK);
                                    barsRL.addView(markFractionT);
                                    barsRL.addView(markFractionC);
                                    barsRL.addView(markFractionA);
                                } else if (rlNested.getHeight() != original_height_of_assignment) {
                                    params.height = original_height_of_assignment;

                                    rlNested.setLayoutParams(params);

                                    View bar1 = v.findViewById(R.id.BarGraph1);
                                    View bar2 = v.findViewById(R.id.BarGraph2);
                                    View bar3 = v.findViewById(R.id.BarGraph3);
                                    View bar4 = v.findViewById(R.id.BarGraph4);

                                    RelativeLayout barsRL = v.findViewById(R.id.mark_bars);
                                    RelativeLayout.LayoutParams barsRLParams = (RelativeLayout.LayoutParams) barsRL.getLayoutParams();
                                    barsRLParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    barsRLParams.height = barsRL.getHeight() / 2;
                                    barsRLParams.addRule(RelativeLayout.BELOW, 0);
                                    barsRLParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                                    barsRL.setLayoutParams(barsRLParams);

                                    barsRL.removeView(KWeight);
                                    barsRL.removeView(TWeight);
                                    barsRL.removeView(CWeight);
                                    barsRL.removeView(AWeight);
                                    rlNested.removeView(feedbackTextView);

                                    barsRL.removeView(markFractionK);
                                    barsRL.removeView(markFractionT);
                                    barsRL.removeView(markFractionC);
                                    barsRL.removeView(markFractionA);

                                    RelativeLayout.LayoutParams layoutParamsBar1 = (RelativeLayout.LayoutParams) bar1.getLayoutParams();
                                    layoutParamsBar1.height = (int) Math.round(bar1.getHeight() / 2.3);
                                    layoutParamsBar1.width = (int) Math.round(bar1.getWidth() / 2);
                                    layoutParamsBar1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    layoutParamsBar1.addRule(RelativeLayout.ABOVE, 0);
                                    layoutParamsBar1.setMarginStart(30);
                                    bar1.setLayoutParams(layoutParamsBar1);

                                    RelativeLayout.LayoutParams layoutParamsBar2 = (RelativeLayout.LayoutParams) bar2.getLayoutParams();
                                    layoutParamsBar2.height = (int) Math.round(bar2.getHeight() / 2.3);
                                    layoutParamsBar2.width = (int) Math.round(bar2.getWidth() / 2);
                                    layoutParamsBar2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    layoutParamsBar2.addRule(RelativeLayout.ABOVE, 0);
                                    layoutParamsBar2.setMarginStart(3);
                                    bar2.setLayoutParams(layoutParamsBar2);

                                    RelativeLayout.LayoutParams layoutParamsBar3 = (RelativeLayout.LayoutParams) bar3.getLayoutParams();
                                    layoutParamsBar3.height = (int) Math.round(bar3.getHeight() / 2.3);
                                    layoutParamsBar3.width = (int) Math.round(bar3.getWidth() / 2);
                                    layoutParamsBar3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    layoutParamsBar3.addRule(RelativeLayout.ABOVE, 0);
                                    layoutParamsBar3.setMarginStart(3);
                                    bar3.setLayoutParams(layoutParamsBar3);

                                    RelativeLayout.LayoutParams layoutParamsBar4 = (RelativeLayout.LayoutParams) bar4.getLayoutParams();
                                    layoutParamsBar4.height = (int) Math.round(bar4.getHeight() / 2.3);
                                    layoutParamsBar4.width = (int) Math.round(bar4.getWidth() / 2);
                                    layoutParamsBar4.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                    layoutParamsBar4.addRule(RelativeLayout.ABOVE, 0);
                                    layoutParamsBar4.setMarginStart(3);
                                    bar4.setLayoutParams(layoutParamsBar4);


                                    TextView AveragePercent = v.findViewById(R.id.AveragePercent);
                                    RelativeLayout.LayoutParams paramsAveragePercent = (RelativeLayout.LayoutParams) AveragePercent.getLayoutParams();
                                    paramsAveragePercent.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);


                                }


                            }
                        });

                        assignmentIndex.put(title, i);
                        final String titleOnClick = title;
                        final int index = i;
                        ImageButton trashButton = (ImageButton) rl.findViewById(R.id.trash_can);
                        trashButton.setOnClickListener(/*new onAssignmentClick(index));*/
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int toSubtract = 0;
                                        for (Integer i : removedAssignmentIndexList) {
                                            if (i < assignmentIndex.get(titleOnClick)) {
                                                toSubtract++;
                                            }
                                        }

                                        linearLayout.removeViewAt(index + 1 - toSubtract);
                                        Marks.remove(String.valueOf(index));
                                        removedAssignmentIndexList.add(index);
                                        numberOfRemovedAssignments++;

                                        String returnval = CalculateTotalAverage(Marks);
                                        TextView AverageInt = findViewById(R.id.AverageInt);
                                        AverageInt.setText(returnval + "%");
                                        int Average = Math.round(Float.parseFloat(returnval));
                                        final RingProgressBar ProgressBarAverage = (RingProgressBar) findViewById(R.id.AverageBar);
                                        ProgressBarAverage.setProgress(Average);

                                    }
                                });

                        trashButton.setVisibility(View.INVISIBLE);

                        // Setup toolbar text
                        getSupportActionBar().setTitle(CourseName);
                        Crashlytics.log(Log.DEBUG, "coursename", CourseName);
                        //set title
                        TextView Title = rl.findViewById(R.id.title);
                        Title.setText(title);

                        //set mark
                        TextView Average = rl.findViewById(R.id.AveragePercent);
                        String returnval = CalculateAverage(marks, String.valueOf(i));
                        if (returnval.equals("No Mark")) {
                            Average.setText(returnval);
                        } else {
                            Average.setText(returnval + "%");
                        }

                        //set bars
                        View bar1 = rl.findViewById(R.id.BarGraph1);
                        View bar2 = rl.findViewById(R.id.BarGraph2);
                        View bar3 = rl.findViewById(R.id.BarGraph3);
                        View bar4 = rl.findViewById(R.id.BarGraph4);
                        bar1.getLayoutParams().height = (int) Math.round(1.5 * (Kmark)) + 45;
                        bar2.getLayoutParams().height = (int) Math.round(1.5 * (Tmark)) + 45;
                        bar3.getLayoutParams().height = (int) Math.round(1.5 * (Cmark)) + 45;
                        bar4.getLayoutParams().height = (int) Math.round(1.5 * (Amark)) + 45;

                        //set percentage texts
                        TextView Kpercent = rl.findViewById(R.id.Kpercent);
                        TextView Tpercent = rl.findViewById(R.id.Tpercent);
                        TextView Cpercent = rl.findViewById(R.id.Cpercent);
                        TextView Apercent = rl.findViewById(R.id.Apercent);
                        TextView K = rl.findViewById(R.id.K);
                        TextView T = rl.findViewById(R.id.T);
                        TextView C = rl.findViewById(R.id.C);
                        TextView A = rl.findViewById(R.id.A);
                        if (Kmark == 100.0) {
                            Kpercent.setText(String.valueOf(Math.round(Kmark)));
                        } else if (Kmark == 0.0) {
                            Kpercent.setText("0.0");
                        } else if (Kmark == 0.000000001) {
                            bar1.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
                            Kpercent.setTextColor(Color.WHITE);
                            Kpercent.setText("NA");
                        } else {
                            Kpercent.setText(String.valueOf(Kmark));
                        }

                        if (Tmark == 100.0) {
                            Tpercent.setText(String.valueOf(Math.round(Tmark)));
                        } else if (Tmark == 0.0) {
                            Tpercent.setText("0.0");
                        } else if (Tmark == 0.000000001) {
                            bar2.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
                            Tpercent.setTextColor(Color.WHITE);
                            Tpercent.setText("NA");
                        } else {
                            Tpercent.setText(String.valueOf(Tmark));
                        }

                        if (Cmark == 100.0) {
                            Cpercent.setText(String.valueOf(Math.round(Cmark)));
                        } else if (Cmark == 0.0) {
                            Cpercent.setText("0.0");
                        } else if (Cmark == 0.000000001) {
                            bar3.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
                            Cpercent.setTextColor(Color.WHITE);
                            Cpercent.setText("NA");
                        } else {
                            Cpercent.setText(String.valueOf(Cmark));
                        }

                        if (Amark == 100.0) {
                            Apercent.setText(String.valueOf(Math.round(Amark)));
                        } else if (Amark == 0.0) {
                            Apercent.setText("0.0");
                        } else if (Amark == 0.000000001) {
                            bar4.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
                            Apercent.setTextColor(Color.WHITE);
                            Apercent.setText("NA");
                        } else {
                            Apercent.setText(String.valueOf(Amark));
                        }


                        //hide bars if the assignment is an exam / summative
                        if (Kpercent.getText().equals("NA") &&
                                Tpercent.getText().equals("NA") &&
                                Cpercent.getText().equals("NA") &&
                                Apercent.getText().equals("NA")) {
                            bar1.setVisibility(View.GONE);
                            bar2.setVisibility(View.GONE);
                            bar3.setVisibility(View.GONE);
                            bar4.setVisibility(View.GONE);
                            Kpercent.setVisibility(View.GONE);
                            Tpercent.setVisibility(View.GONE);
                            Cpercent.setVisibility(View.GONE);
                            Apercent.setVisibility(View.GONE);
                            T.setVisibility(View.GONE);
                            C.setVisibility(View.GONE);
                            A.setVisibility(View.GONE);

                            K.setText("Weight: " + String.valueOf(Oweight));
                            RelativeLayout.LayoutParams lpK = (RelativeLayout.LayoutParams) K.getLayoutParams();
                            lpK.addRule(RelativeLayout.CENTER_IN_PARENT);
                            K.setTextSize(20);
                            rl.setClickable(false);


                        }

                        dialog.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        Double Omark = 0.0;
        DecimalFormat round = new DecimalFormat(".#");
        JSONObject assignment = marks.getJSONObject(assingmentNumber);

        if(assignment.has("")){
            if (!assignment.getJSONObject("").getString("outOf").equals("0") || !assignment.getJSONObject("").getString("outOf").equals("0.0")) {
                if (!assignment.getJSONObject("").isNull("mark")) {
                    if(assignment.getJSONObject("").getString("mark").equals("no mark")){
                        return "No Mark";
                    }
                    Omark = Double.parseDouble(assignment.getJSONObject("").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("").getString("outOf"));
                    return round.format(Omark * 100);

                }
            }
        }

        if(assignment.has("K")) {
            if(assignment.getJSONObject("K").getString("mark").equals("no mark")){
                return "No Mark";
            }
            if (!assignment.getJSONObject("K").getString("outOf").equals("0") || !assignment.getJSONObject("K").getString("outOf").equals("0.0")) {
                if (!assignment.getJSONObject("K").isNull("mark")) {
                    Kmark = Double.parseDouble(assignment.getJSONObject("K").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("K").getString("outOf"));
                }else{
                    weightK = 0.0;
                }
            }
        }else{
                weightK = 0.0;
            }
        if(assignment.has("T")) {
            if(assignment.getJSONObject("T").getString("mark").equals("no mark")){
                return "No Mark";
            }
            if (!assignment.getJSONObject("T").getString("outOf").equals("0") || !assignment.getJSONObject("T").getString("outOf").equals("0.0")) {
                if (!assignment.getJSONObject("T").isNull("mark")) {
                    Tmark = Double.parseDouble(assignment.getJSONObject("T").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("T").getString("outOf"));
                }else{
                    weightT = 0.0;
                }
            }
        }else{
                weightT = 0.0;
            }
        if(assignment.has("C")) {
            if(assignment.getJSONObject("C").getString("mark").equals("no mark")){
                return "No Mark";
            }
            if (!assignment.getJSONObject("C").getString("outOf").equals("0") || !assignment.getJSONObject("C").getString("outOf").equals("0.0")) {
                if (!assignment.getJSONObject("C").isNull("mark")) {
                    Cmark = Double.parseDouble(assignment.getJSONObject("C").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("C").getString("outOf"));
                }else{
                    weightC = 0.0;
                }
            }
        }else{
                weightC = 0.0;
            }
        if(assignment.has("A")) {
            if(assignment.getJSONObject("A").getString("mark").equals("no mark")){
                return "No Mark";
            }
            if (!assignment.getJSONObject("A").getString("outOf").equals("0") || !assignment.getJSONObject("A").getString("outOf").equals("0.0")) {
                if (!assignment.getJSONObject("A").isNull("mark")) {
                    Amark = Double.parseDouble(assignment.getJSONObject("A").getString("mark")) /
                            Double.parseDouble(assignment.getJSONObject("A").getString("outOf"));
                }else{
                    weightA = 0.0;
                }
            }
        }else{
                weightA = 0.0;
            }

        Kmark*=weightK;
        Tmark*=weightT;
        Cmark*=weightC;
        Amark*=weightA;
        String Average = round.format((Kmark+Tmark+Cmark+Amark)/(weightK+weightT+weightC+weightA)*100);
        if(Average.equals(".0")){
            Average = "0";
        }
        if(Average.equals("100.0")){
            Average = "100";
        }
        return Average;
        }catch (JSONException e){
            e.printStackTrace();
            Crashlytics.log(Log.ERROR, "error in Calculate Average MVM", Arrays.toString(e.getStackTrace()));
            return null;
        }
    }
    public String CalculateTotalAverage(JSONObject marks) {
        DecimalFormat round = new DecimalFormat(".#");
        try {
            JSONObject weights = marks.getJSONObject("categories");
            Double weightK = weights.getDouble("K") * 10 * 0.7;
            Double weightT = weights.getDouble("T") * 10 * 0.7;
            Double weightC = weights.getDouble("C") * 10 * 0.7;
            Double weightA = weights.getDouble("A") * 10 * 0.7;
            Double weightS = 0.3*10;
            Double Kmark = 0.0;
            Double Tmark = 0.0;
            Double Cmark = 0.0;
            Double Amark = 0.0;
            Double Smark = 0.0;
            Double KweightAssignment = 0.0;
            Double TweightAssignment = 0.0;
            Double CweightAssignment = 0.0;
            Double AweightAssignment = 0.0;
            Double SweightAssignment = 0.0;
            Double KweightAssignmentTemp;
            Double TweightAssignmentTemp;
            Double CweightAssignmentTemp;
            Double AweightAssignmentTemp;
            Double SweightAssignmentTemp;
            for (int i = 0; i < marks.length()-1+numberOfRemovedAssignments; i++) {
                JSONObject assignment;
                try {
                    assignment = marks.getJSONObject(String.valueOf(i));
                }catch (JSONException e){
                        continue;}

                try {
                    if (!assignment.getJSONObject("K").isNull("mark") && !assignment.getJSONObject("K").getString("mark").equals("no mark")) {
                        Double assignmentK = Double.parseDouble(assignment.getJSONObject("K").getString("mark")) /
                                Double.parseDouble(assignment.getJSONObject("K").getString("outOf"));
                        KweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("K").getString("weight"));
                        Kmark += assignmentK * KweightAssignmentTemp;
                        KweightAssignment += KweightAssignmentTemp;
                    }
                }catch (JSONException e){}

                try {
                    if (!assignment.getJSONObject("T").isNull("mark") && !assignment.getJSONObject("T").getString("mark").equals("no mark")) {
                        Double assignmentT = Double.parseDouble(assignment.getJSONObject("T").getString("mark")) /
                                Double.parseDouble(assignment.getJSONObject("T").getString("outOf"));
                        TweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("T").getString("weight"));
                        Tmark += assignmentT * TweightAssignmentTemp;
                        TweightAssignment += TweightAssignmentTemp;
                    }
                }catch (JSONException e){}

                try {
                    if (!assignment.getJSONObject("C").isNull("mark") && !assignment.getJSONObject("C").getString("mark").equals("no mark")) {
                        Double assignmentC = Double.parseDouble(assignment.getJSONObject("C").getString("mark")) /
                                Double.parseDouble(assignment.getJSONObject("C").getString("outOf"));
                        CweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("C").getString("weight"));
                        Cmark += assignmentC * CweightAssignmentTemp;
                        CweightAssignment += CweightAssignmentTemp;
                    }
                }catch (JSONException e){}

                try {
                    if (!assignment.getJSONObject("A").isNull("mark") && !assignment.getJSONObject("A").getString("mark").equals("no mark")) {
                        Double assignmentA = Double.parseDouble(assignment.getJSONObject("A").getString("mark")) /
                                Double.parseDouble(assignment.getJSONObject("A").getString("outOf"));
                        AweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("A").getString("weight"));
                        Amark += assignmentA * AweightAssignmentTemp;
                        AweightAssignment += AweightAssignmentTemp;
                    }
                }catch (JSONException e){}

                try {
                    if (!assignment.getJSONObject("").isNull("mark")) {
                        Double assignmentS = Double.parseDouble(assignment.getJSONObject("").getString("mark")) /
                                Double.parseDouble(assignment.getJSONObject("").getString("outOf"));
                        SweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("").getString("weight"));
                        Smark += assignmentS * SweightAssignmentTemp;
                        SweightAssignment += SweightAssignmentTemp;
                    }
                }catch (JSONException e){}

            }

            if(KweightAssignment == 0.0){
                Kmark = 0.0;
                weightK = 0.0;
            }else {
                Kmark /= KweightAssignment;
            }

            if(TweightAssignment == 0.0){
                Tmark = 0.0;
                weightT = 0.0;
            }else {
                Tmark /= TweightAssignment;
            }

            if(CweightAssignment == 0.0){
                Cmark = 0.0;
                weightC = 0.0;
            }else {
                Cmark /= CweightAssignment;
            }

            if(AweightAssignment == 0.0){
                Amark = 0.0;
                weightA = 0.0;
            }else {
                Amark /= AweightAssignment;
            }
            if(SweightAssignment == 0.0){
                Smark = 0.0;
                weightS = 0.0;
            }else {
                Smark /= SweightAssignment;
            }

            Kmark *= weightK;
            Tmark *= weightT;
            Cmark *= weightC;
            Amark *= weightA;
            Smark *= weightS;
            String Average = String.valueOf(round.format((Kmark + Tmark + Cmark + Amark +Smark) / (weightK + weightT + weightC + weightA +weightS) * 100));
            return Average;

        }catch (JSONException e){
            e.printStackTrace();
            Crashlytics.log(Log.ERROR, "MarksViewMaterial Calculate total average returns null", Arrays.toString(e.getStackTrace()));
            return null;
        }
    }
}
