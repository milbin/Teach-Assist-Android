package com.teachassist.teachassist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;


public class AssignmentsFragment extends Fragment {
    LinearLayout linearLayout;
    Menu menu;
    JSONObject Marks;
    String username;
    String password;
    int subjectNumber;
    String courseCode;
    String Mark;
    ProgressDialog dialog;
    Context context;
    ArrayList<View> rlList = new ArrayList<>();
    int numberOfAssignments;
    int numberOfRemovedAssignments;
    Boolean trashShown = false;
    ArrayList<Integer> removedAssignmentIndexList = new ArrayList<>();
    LinkedHashMap<String, Integer> assignmentIndex = new LinkedHashMap<>();
    int original_height_of_assignment = -1;
    SwipeRefreshLayout SwipeRefresh;
    private AsyncTask getMarksTask;
    Typeface font;
    RelativeLayout addAssignmentButton;
    boolean isAddAssignmentButtonExpanded = false;
    TextView addAssignmentAdvancedModeButton;
    boolean isAddAssignmentAdvancedModeButtonExpanded = false;
    Button addAssignmentDoneButton;
    Button addAssignmentCancelButton;
    View fragment;
    AppCompatActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.assignments_fragment, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
        fragment = getView();
        activity = (AppCompatActivity)getActivity();

        //setup add assignment button
        addAssignmentButton = fragment.findViewById(R.id.addAssignmentButton);
        addAssignmentAdvancedModeButton = fragment.findViewById(R.id.addAssignmentAdvancedModeTV);
        addAssignmentDoneButton = fragment.findViewById(R.id.addAssignmentDoneButton);
        addAssignmentCancelButton = fragment.findViewById(R.id.addAssignmentCancelButton);


        //setup edit button
        activity.findViewById(R.id.editButton).setOnClickListener(new onEditButtonClick());

        //setup font
        font = ResourcesCompat.getFont(context, R.font.sandstone_regular);

        //progress dialog
        dialog = ProgressDialog.show(context, "",
                "Loading...", true);


        //get intents
        Intent intent = activity.getIntent();
        username = intent.getStringExtra("username").replaceAll("\\s+", "");
        password = intent.getStringExtra("password").replaceAll("\\s+", "");
        subjectNumber = intent.getIntExtra("subjectNumber", 0);
        courseCode = intent.getStringExtra("courseCode").replaceAll("\\s+", "");
        Mark = intent.getStringExtra("subject Mark");
        Crashlytics.setUserIdentifier(username);
        Crashlytics.setString("username", username);
        Crashlytics.setString("password", password);
        Crashlytics.log(Log.DEBUG, "username", username);
        Crashlytics.log(Log.DEBUG, "password", password);

        linearLayout = fragment.findViewById(R.id.LinearLayoutMarksView);

        SwipeRefresh = fragment.findViewById(R.id.swipeRefresh);
        SwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Intent intent = activity.getIntent();
                        Mark = intent.getStringExtra("subject Mark");
                        int length = linearLayout.getChildCount();
                        for (int i = 2; i < length; i++) {
                            linearLayout.removeViewAt(2);
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

    public class onEditButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(!trashShown) {
                for (int i = 0; i < linearLayout.getChildCount(); i++) {
                    View assignmentView = linearLayout.getChildAt(i);
                    if (assignmentView instanceof RelativeLayout) {
                        try {
                            ImageButton trashButton = (ImageButton) assignmentView.findViewById(R.id.trash_can);
                            trashButton.setVisibility(View.VISIBLE);
                            trashShown = true;
                        } catch (Exception e) {
                        }
                    }
                }
            }else{
                for (int i = 0; i < linearLayout.getChildCount(); i++) {
                    View assignmentView = linearLayout.getChildAt(i);
                    if (assignmentView instanceof RelativeLayout) {
                        try {
                            ImageButton trashButton = (ImageButton) assignmentView.findViewById(R.id.trash_can);
                            trashButton.setVisibility(View.INVISIBLE);
                            trashShown = false;
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((Button)activity.findViewById(R.id.statisticsButton)).setTextColor(resolveColorAttr(context, R.attr.unhighlightedTextColor));
        ((Button)activity.findViewById(R.id.assignmentsButton)).setTextColor(resolveColorAttr(context, R.attr.textColor));
    }

    //without this overide if there is no internet the activity will raise an error because you are showing the no internet dialog when it has already been destroyed
    @Override
    public void onStop(){
        getMarksTask.cancel(true);
        super.onStop();
    }
    @Override
    public void onDestroy(){
        getMarksTask.cancel(true);
        super.onDestroy();
    }

    private class GetMarks extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected void onPreExecute() {
            if(Mark == null){
                new AlertDialog.Builder(context)
                        .setTitle("Connection Error")
                        .setMessage("Something went wrong while trying to reach TeachAssist. Please check your internet connection and try again.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("MainActivity", "No internet connection");
                            }
                        })
                        .show();
                if(!isCancelled()) {
                    dialog.dismiss();
                }
                return;
            }
            TextView semesterAverageTV = fragment.findViewById(R.id.semesterAverageTV);
            semesterAverageTV.setText(Mark+"%");
            if(Mark.contains("NA") && !Mark.contains("NaN")){
                activity.finish();
            }
            int Average = Math.round(Float.parseFloat(Mark.replaceAll("%", "")));
            final RingProgressBar ProgressBarAverage = (RingProgressBar) fragment.findViewById(R.id.AverageBar);
            ProgressBarAverage.setProgress(Average);
        }

        @Override
        protected JSONObject doInBackground(String... ignore) {
            TA ta = new TA();
            ta.GetCoursesHTML(username, password);
            JSONObject returnValue = ta.GetAssignmentsHTML(subjectNumber);
            AppDatabase db = Room.databaseBuilder(activity.getApplicationContext(),
                    AppDatabase.class, username).build();
            if (returnValue == null) {
                try {
                    Marks = new JSONObject(db.coursesDao().getCourseByCourseCode(courseCode).assignments);
                } catch (Exception ignored) {
                    return null;
                }
            }else{
                Marks = returnValue;
            }
            ((CourseInfoActivity) activity).assignments = Marks;
            // Write all the individual assignments of this course as one big json string to the database
            // Android requires that the action be performed within a thread
            String jsonAssignmentString = Marks.toString();
            //it should not be possible for this call to return nothing since you need an unhidden course to click on it
            CoursesEntity coursesEntity = db.coursesDao().getCourseByCourseCode(courseCode);
            coursesEntity.assignments = jsonAssignmentString;
            db.coursesDao().updateCourse(coursesEntity);
            db.close();
            return Marks;
        }

        protected void onPostExecute(final JSONObject marks){
            if(!isCancelled()) {
                if (marks == null) {
                    new AlertDialog.Builder(context)
                            .setTitle("Connection Error")
                            .setMessage("Something went wrong while trying to reach TeachAssist. Please check your internet connection and try again.")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    activity.recreate();
                                }
                            })
                            .show();
                    dialog.dismiss();
                    return;
                }
                numberOfAssignments = marks.length() - 1;
                setupCourseBars(marks);

                //add assignments to lienar layout
                for (int i = 0; i < numberOfAssignments; i++) {
                    addAssignmentToLinearLayout(marks, i, isCancelled());
                }
                addAssignmentButton.setVisibility(View.VISIBLE);
                addAssignmentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isAddAssignmentButtonExpanded) { //button is expanded, must now collapse
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) addAssignmentButton.getLayoutParams();
                            RelativeLayout addAssignmentAllButtonsRL = fragment.findViewById(R.id.addAssignmentAllButtonsRL);
                            RelativeLayout plusIconRL = fragment.findViewById(R.id.plusIconRL);

                            params.height = (int) Math.round(addAssignmentButton.getHeight() * 2.3);
                            plusIconRL.setVisibility(View.GONE);
                            addAssignmentAllButtonsRL.setVisibility(View.VISIBLE);
                            isAddAssignmentButtonExpanded = true;
                            addAssignmentButton.setLayoutParams(params);
                        }
                    }
                });
                addAssignmentAdvancedModeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) addAssignmentButton.getLayoutParams();
                        RelativeLayout addAssignmentAdvancedModeRL = fragment.findViewById(R.id.addAssignmentAdvancedModeRL);
                        ImageView addAssignmentAdvancedModeDropdownButton = fragment.findViewById(R.id.addAssignmentAdvancedModeDropdownButton);

                        EditText addAssignmentOverallMark = fragment.findViewById(R.id.addAssignmentOverallMarkET);
                        EditText addAssignmentOverallWeight  = fragment.findViewById(R.id.addAssignmentOverallWeightET);
                        if(isAddAssignmentAdvancedModeButtonExpanded) { //currently expanded
                            addAssignmentOverallMark.setAlpha(1f);
                            addAssignmentOverallMark.setFocusable(true);
                            addAssignmentOverallMark.setFocusableInTouchMode(true);
                            addAssignmentOverallWeight.setAlpha(1f);
                            addAssignmentOverallWeight.setFocusable(true);
                            addAssignmentOverallWeight.setFocusableInTouchMode(true);
                            params.height = (int) Math.round(addAssignmentButton.getHeight() / 1.8);
                            addAssignmentAdvancedModeRL.setVisibility(View.GONE);
                            addAssignmentAdvancedModeDropdownButton.setImageDrawable(context.getTheme().getDrawable(R.drawable.arrow_up));
                            isAddAssignmentAdvancedModeButtonExpanded = false;
                        }else{//not currently expanded
                            addAssignmentOverallMark.setAlpha(0.5f);
                            addAssignmentOverallMark.setFocusable(false);
                            addAssignmentOverallMark.setFocusableInTouchMode(false);
                            addAssignmentOverallWeight.setAlpha(0.5f);
                            addAssignmentOverallWeight.setFocusable(false);
                            addAssignmentOverallWeight.setFocusableInTouchMode(false);
                            params.height = (int) Math.round(addAssignmentButton.getHeight() * 1.8);
                            addAssignmentAdvancedModeRL.setVisibility(View.VISIBLE);
                            addAssignmentAdvancedModeDropdownButton.setImageDrawable(context.getTheme().getDrawable(R.drawable.arrow_down));
                            isAddAssignmentAdvancedModeButtonExpanded = true;
                        }
                        addAssignmentButton.setLayoutParams(params);

                    }
                });
                addAssignmentDoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText addAssignmentTitle = fragment.findViewById(R.id.addAssignmentTitleET);
                        EditText addAssignmentOverallMark = fragment.findViewById(R.id.addAssignmentOverallMarkET);
                        EditText addAssignmentOverallWeight  = fragment.findViewById(R.id.addAssignmentOverallWeightET);

                        EditText addAssignmentKMark = fragment.findViewById(R.id.addAssignmentKMarkET);
                        EditText addAssignmentTMark = fragment.findViewById(R.id.addAssignmentTMarkET);
                        EditText addAssignmentCMark = fragment.findViewById(R.id.addAssignmentCMarkET);
                        EditText addAssignmentAMark = fragment.findViewById(R.id.addAssignmentAMarkET);
                        EditText addAssignmentOMark = fragment.findViewById(R.id.addAssignmentOMarkET);

                        EditText addAssignmentKWeight = fragment.findViewById(R.id.addAssignmentKWeightET);
                        EditText addAssignmentTWeight = fragment.findViewById(R.id.addAssignmentTWeightET);
                        EditText addAssignmentCWeight = fragment.findViewById(R.id.addAssignmentCWeightET);
                        EditText addAssignmentAWeight = fragment.findViewById(R.id.addAssignmentAWeightET);
                        EditText addAssignmentOWeight = fragment.findViewById(R.id.addAssignmentOWeightET);

                        //check for user entry errors
                        if(addAssignmentTitle.getText().toString().isEmpty()){
                            new AlertDialog.Builder(context)
                                    .setTitle("Please Add a Title")
                                    .setMessage("An assignment title is required for all new assignments.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                            return;
                        }

                        JSONObject assignment = new JSONObject();
                        try {
                            assignment.put("title", addAssignmentTitle.getText().toString());
                            assignment.put("feedback", "");
                            if (isAddAssignmentAdvancedModeButtonExpanded) { //advanced mode
                                if(!NumberUtils.isNumber(addAssignmentKMark.getText().toString()) &&
                                        !NumberUtils.isNumber(addAssignmentTMark.getText().toString()) &&
                                        !NumberUtils.isNumber(addAssignmentCMark.getText().toString()) &&
                                        !NumberUtils.isNumber(addAssignmentAMark.getText().toString()) &&
                                        !NumberUtils.isNumber(addAssignmentOMark.getText().toString())){
                                    new AlertDialog.Builder(context)
                                            .setTitle("Please Add a Mark")
                                            .setMessage("At least one mark is required in any category to add an assignment.")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                            .show();
                                    return;
                                }

                                //marks
                                boolean K = !NumberUtils.isNumber(addAssignmentKMark.getText().toString());
                                boolean T = !NumberUtils.isNumber(addAssignmentTMark.getText().toString());
                                boolean C = !NumberUtils.isNumber(addAssignmentCMark.getText().toString());
                                boolean A = !NumberUtils.isNumber(addAssignmentAMark.getText().toString());
                                boolean O = !NumberUtils.isNumber(addAssignmentOMark.getText().toString());
                                final Double KMark = (double) Math.round((K?-1:Double.parseDouble(addAssignmentKMark.getText().toString()))*10)/10; //if K is NAN, make it 0, otherwise make it K
                                final Double TMark = (double) Math.round((T?-1:Double.parseDouble(addAssignmentTMark.getText().toString()))*10)/10;
                                final Double CMark = (double) Math.round((C?-1:Double.parseDouble(addAssignmentCMark.getText().toString()))*10)/10;
                                final Double AMark = (double) Math.round((A?-1:Double.parseDouble(addAssignmentAMark.getText().toString()))*10)/10;
                                final Double OMark = (double) Math.round((O?-1:Double.parseDouble(addAssignmentOMark.getText().toString()))*10)/10;

                                //weights
                                boolean KW = !NumberUtils.isNumber(addAssignmentKWeight.getText().toString());
                                boolean TW = !NumberUtils.isNumber(addAssignmentTWeight.getText().toString());
                                boolean CW = !NumberUtils.isNumber(addAssignmentCWeight.getText().toString());
                                boolean AW = !NumberUtils.isNumber(addAssignmentAWeight.getText().toString());
                                boolean OW = !NumberUtils.isNumber(addAssignmentOWeight.getText().toString());
                                final Double KWeight = (double) Math.round((KW?(K?0:1):Double.parseDouble(addAssignmentKWeight.getText().toString()))*10)/10; //if K weight is a a number, use that number, otherwise if K mark is a number set K weight to 1, if not, set K weight to zero
                                final Double TWeight = (double) Math.round((TW?(T?0:1):Double.parseDouble(addAssignmentTWeight.getText().toString()))*10)/10;
                                final Double CWeight = (double) Math.round((CW?(C?0:1):Double.parseDouble(addAssignmentCWeight.getText().toString()))*10)/10;
                                final Double AWeight = (double) Math.round((AW?(A?0:1):Double.parseDouble(addAssignmentAWeight.getText().toString()))*10)/10;
                                final Double OWeight = (double) Math.round((OW?(O?0:1):Double.parseDouble(addAssignmentOWeight.getText().toString()))*10)/10;

                                //if Kmark == -1 set this key to null, otherwise create new json object and populate
                                assignment.put("K", (KMark == -1)?null:new JSONObject() {{put("category", "K"); put("mark", KMark.toString()); put("outOf", "100"); put("weight", KWeight.toString());}});
                                assignment.put("T", (TMark == -1)?null:new JSONObject() {{put("category", "T"); put("mark", TMark.toString()); put("outOf", "100"); put("weight", TWeight.toString());}});
                                assignment.put("C", (CMark == -1)?null:new JSONObject() {{put("category", "C"); put("mark", CMark.toString()); put("outOf", "100"); put("weight", CWeight.toString());}});
                                assignment.put("A", (AMark == -1)?null:new JSONObject() {{put("category", "A"); put("mark", AMark.toString()); put("outOf", "100"); put("weight", AWeight.toString());}});
                                assignment.put("", (OMark == -1)?null:new JSONObject() {{put("category", ""); put("mark", OMark.toString()); put("outOf", "100"); put("weight", OWeight.toString());}});
                            } else { //non advanced mode
                                if(!NumberUtils.isNumber(addAssignmentOverallMark.getText().toString())){
                                    new AlertDialog.Builder(context)
                                            .setTitle("Please Add a Mark")
                                            .setMessage("At least one mark is required in any category to add an assignment.")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                            .show();
                                    return;
                                }
                                if(!NumberUtils.isNumber(addAssignmentOverallWeight.getText().toString())){
                                    new AlertDialog.Builder(context)
                                            .setTitle("Please Add a Weight")
                                            .setMessage("An assignment weighting is required to calculate your course average.")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                            .show();
                                    return;
                                }
                                boolean markIsNumber = !NumberUtils.isNumber(addAssignmentOverallMark.getText().toString());
                                final Double mark = (double) Math.round((markIsNumber?0:Double.parseDouble(addAssignmentOverallMark.getText().toString()))*10)/10;
                                boolean weightIsNumber = !NumberUtils.isNumber(addAssignmentOverallWeight.getText().toString());
                                final Double weight = (double) Math.round((weightIsNumber?1:Double.parseDouble(addAssignmentOverallWeight.getText().toString()))*10)/10;
                                assignment.put("K", new JSONObject() {{put("category", "K"); put("mark", mark.toString()); put("outOf", "100"); put("weight", weight.toString());}});
                                assignment.put("T", new JSONObject() {{put("category", "T"); put("mark", mark.toString()); put("outOf", "100"); put("weight", weight.toString());}});
                                assignment.put("C", new JSONObject() {{put("category", "C"); put("mark", mark.toString()); put("outOf", "100"); put("weight", weight.toString());}});
                                assignment.put("A", new JSONObject() {{put("category", "A"); put("mark", mark.toString()); put("outOf", "100"); put("weight", weight.toString());}});
                                //assignment.put("", new JSONObject() {{put("category", ""); put("mark", mark.toString()); put("outOf", "100"); put("weight", weight.toString());}});
                            }
                        }catch (JSONException e){
                            new AlertDialog.Builder(context)
                                    .setTitle("Entry Error")
                                    .setMessage("Something went wrong while trying to add an assignment. Please check what you entered and try again.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                            return;
                        }
                        try{
                            Marks.put(String.valueOf(numberOfAssignments), assignment);
                            addAssignmentToLinearLayout(Marks, numberOfAssignments, false);
                            numberOfAssignments++;
                            TA ta = new TA();
                            String returnval = ta.CalculateCourseAverageFromAssignments(Marks, numberOfRemovedAssignments, null);
                            TextView AverageInt = fragment.findViewById(R.id.semesterAverageTV);
                            AverageInt.setText(returnval + "%");
                            int Average = Math.round(Float.parseFloat(returnval));
                            final RingProgressBar ProgressBarAverage = (RingProgressBar) fragment.findViewById(R.id.AverageBar);
                            ProgressBarAverage.setProgress(Average);
                            setupCourseBars(Marks);
                            System.out.println(assignment);

                        }catch (JSONException ignore){}
                        addAssignmentCancelButton.callOnClick();
                        clearAddAssignmentEditTextViews();

                    }
                });
                addAssignmentCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isAddAssignmentButtonExpanded){ //button is expanded, must now collapse

                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) addAssignmentButton.getLayoutParams();
                            if(isAddAssignmentAdvancedModeButtonExpanded) { //currently expanded
                                addAssignmentAdvancedModeButton.performClick();
                                clearAddAssignmentEditTextViews();
                                params.height = (int) Math.round(addAssignmentButton.getHeight() / (2.3*1.8));
                            }else{
                                params.height = (int) Math.round(addAssignmentButton.getHeight() / 2.3);
                            }
                            RelativeLayout addAssignmentAllButtonsRL = fragment.findViewById(R.id.addAssignmentAllButtonsRL);
                            RelativeLayout plusIconRL = fragment.findViewById(R.id.plusIconRL);
                            addAssignmentButton.setLayoutParams(params);
                            addAssignmentAllButtonsRL.setVisibility(View.GONE);
                            plusIconRL.setVisibility(View.VISIBLE);
                            isAddAssignmentButtonExpanded = false;

                        }
                    }
                });
            }
        }
    }
    private void clearAddAssignmentEditTextViews(){
        EditText addAssignmentTitle = fragment.findViewById(R.id.addAssignmentTitleET);
        EditText addAssignmentOverallMark = fragment.findViewById(R.id.addAssignmentOverallMarkET);
        EditText addAssignmentOverallWeight  = fragment.findViewById(R.id.addAssignmentOverallWeightET);
        EditText addAssignmentKMark = fragment.findViewById(R.id.addAssignmentKMarkET);
        EditText addAssignmentTMark = fragment.findViewById(R.id.addAssignmentTMarkET);
        EditText addAssignmentCMark = fragment.findViewById(R.id.addAssignmentCMarkET);
        EditText addAssignmentAMark = fragment.findViewById(R.id.addAssignmentAMarkET);
        EditText addAssignmentOMark = fragment.findViewById(R.id.addAssignmentOMarkET);
        EditText addAssignmentKWeight = fragment.findViewById(R.id.addAssignmentKWeightET);
        EditText addAssignmentTWeight = fragment.findViewById(R.id.addAssignmentTWeightET);
        EditText addAssignmentCWeight = fragment.findViewById(R.id.addAssignmentCWeightET);
        EditText addAssignmentAWeight = fragment.findViewById(R.id.addAssignmentAWeightET);
        EditText addAssignmentOWeight = fragment.findViewById(R.id.addAssignmentOWeightET);

        //clear all text views
        addAssignmentKMark.setText("");
        addAssignmentTMark.setText("");
        addAssignmentCMark.setText("");
        addAssignmentAMark.setText("");
        addAssignmentOMark.setText("");
        addAssignmentKWeight.setText("");
        addAssignmentTWeight.setText("");
        addAssignmentCWeight.setText("");
        addAssignmentAWeight.setText("");
        addAssignmentOWeight.setText("");
        addAssignmentTitle.setText("");
        addAssignmentOverallMark.setText("");
        addAssignmentOverallWeight.setText("");
    }

    private void addAssignmentToLinearLayout(final JSONObject marks, int i, boolean isCancelled){
        View rl;
        final DecimalFormat round = new DecimalFormat("#.#");
        String title;
        Double Kmark = 0.000000001;
        Double Tmark = 0.000000001;
        Double Cmark = 0.000000001;
        Double Amark = 0.000000001;
        Double Omark = 0.000000001;
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
        final String finalStringOFraction;
        String stringKFraction = "";
        String stringTFraction = "";
        String stringCFraction = "";
        String stringAFraction = "";
        String stringOFraction = "";
        rl = LayoutInflater.from(context).inflate(R.layout.marks_view_assignment, null);
        linearLayout.addView(rl);
        rlList.add(rl);
        try {
            final JSONObject assignment = marks.getJSONObject(String.valueOf(i));
            title = assignment.getString("title");
            feedback = assignment.getString("feedback").replaceAll("\n", "");
            if (assignment.has("K") && assignment.getJSONObject("K").getString("mark").equals("no mark")){
                Kweight = 0.0;
            }else {
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
                            Kmark = Double.parseDouble(round.format(Kmark * 100).replaceAll(",", "."));
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
            }
            if (assignment.has("T") && assignment.getJSONObject("T").getString("mark").equals("no mark")) {
                Tweight = 0.0;
            }else {

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
                            Tmark = Double.parseDouble(round.format(Tmark * 100).replaceAll(",", "."));
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
            }
            if (assignment.has("C") && assignment.getJSONObject("C").getString("mark").equals("no mark")) {
                Cweight = 0.0;
            }else {

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
                            Cmark = Double.parseDouble(round.format(Cmark * 100).replaceAll(",", "."));
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
            }
            if (assignment.has("A") && assignment.getJSONObject("A").getString("mark").equals("no mark")) {
                Aweight = 0.0;
            }else {

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
                            Amark = Double.parseDouble(round.format(Amark * 100).replaceAll(",", "."));
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
            }
            if (assignment.has("") && assignment.getJSONObject("").getString("mark").equals("no mark")) {
                Oweight = 0.0;
            }else {
                if (assignment.has("")) {
                    if (assignment.getJSONObject("").getString("weight").isEmpty()) {
                        Oweight = 0.0;
                    } else {
                        Oweight = Double.parseDouble(assignment.getJSONObject("").getString("weight"));
                    }
                    if (assignment.getJSONObject("").getString("outOf").equals("0") || assignment.getJSONObject("").getString("outOf").equals("0.0")) {
                        Omark = 0.0;
                    } else {
                        if (!assignment.getJSONObject("").isNull("mark") && !assignment.getJSONObject("").isNull("outOf")) {
                            Omark = Double.parseDouble(assignment.getJSONObject("").getString("mark")) /
                                    Double.parseDouble(assignment.getJSONObject("").getString("outOf"));
                            Omark = Double.parseDouble(round.format(Omark * 100).replaceAll(",", "."));
                        }
                    }
                    if (assignment.getJSONObject("").getString("mark").isEmpty()) {
                        stringOFraction = "0/";
                    } else {
                        stringOFraction = assignment.getJSONObject("").getString("mark") + "/";
                    }
                    if (assignment.getJSONObject("").getString("outOf").isEmpty()) {
                        stringOFraction += "0";
                    } else {
                        stringOFraction += assignment.getJSONObject("").getString("outOf");
                    }
                } else {
                    Oweight = 0.0;
                }
            }




            finalStringKFraction = stringKFraction;
            finalStringTFraction = stringTFraction;
            finalStringCFraction = stringCFraction;
            finalStringAFraction = stringAFraction;
            finalStringOFraction = stringOFraction;

            final TextView KWeight = new TextView(new ContextThemeWrapper(context,R.style.Body2));
            final TextView TWeight = new TextView(new ContextThemeWrapper(context,R.style.Body2));
            final TextView CWeight = new TextView(new ContextThemeWrapper(context,R.style.Body2));
            final TextView AWeight = new TextView(new ContextThemeWrapper(context,R.style.Body2));
            final TextView OWeight = new TextView(new ContextThemeWrapper(context,R.style.Body2));
            final TextView feedbackTextView = new TextView(new ContextThemeWrapper(context,R.style.Body2));
            final TextView markFractionK = new TextView(new ContextThemeWrapper(context,R.style.Body2));
            final TextView markFractionT = new TextView(new ContextThemeWrapper(context,R.style.Body2));
            final TextView markFractionC = new TextView(new ContextThemeWrapper(context,R.style.Body2));
            final TextView markFractionA = new TextView(new ContextThemeWrapper(context,R.style.Body2));
            final TextView markFractionO = new TextView(new ContextThemeWrapper(context,R.style.Body2));

            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RelativeLayout rlNested = v.findViewById(R.id.relativeLayout_marks_view);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rlNested.getLayoutParams();
                    if (original_height_of_assignment == -1) {
                        original_height_of_assignment = rlNested.getHeight();
                    }
                    if (rlNested.getHeight() == original_height_of_assignment) {
                        params.height = (int) Math.round(rlNested.getHeight() * 2.4 + v.findViewById(R.id.AveragePercent).getHeight() + ((feedback.length()/35) * 30));

                        rlNested.setLayoutParams(params);
                        RelativeLayout bar1 = v.findViewById(R.id.BarGraph1);
                        RelativeLayout bar2 = v.findViewById(R.id.BarGraph2);
                        RelativeLayout bar3 = v.findViewById(R.id.BarGraph3);
                        RelativeLayout bar4 = v.findViewById(R.id.BarGraph4);
                        RelativeLayout bar5 = v.findViewById(R.id.BarGraph5);

                        RelativeLayout barsRL = v.findViewById(R.id.mark_bars);
                        RelativeLayout.LayoutParams barsRLParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        barsRLParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        barsRLParams.height = barsRL.getHeight() * 3;
                        barsRLParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        barsRLParams.setMargins(0, 0, 0, dpToPx(8));
                        barsRL.setLayoutParams(barsRLParams);

                        RelativeLayout.LayoutParams paramsKweight = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        KWeight.setId(View.generateViewId());
                        paramsKweight.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        paramsKweight.addRule(RelativeLayout.ALIGN_START, R.id.BarGraph1);
                        paramsKweight.addRule(RelativeLayout.ALIGN_END, R.id.BarGraph1);
                        KWeight.setLayoutParams(paramsKweight);
                        KWeight.setText(String.valueOf(Kweight));
                        KWeight.setTextColor(resolveColorAttr(context, R.attr.textColor));
                        KWeight.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                        KWeight.setTypeface(font);
                        KWeight.setTextSize(12);
                        barsRL.addView(KWeight);

                        RelativeLayout.LayoutParams paramsTweight = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        TWeight.setId(View.generateViewId());
                        paramsTweight.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        paramsTweight.addRule(RelativeLayout.ALIGN_START, R.id.BarGraph2);
                        paramsTweight.addRule(RelativeLayout.ALIGN_END, R.id.BarGraph2);
                        TWeight.setLayoutParams(paramsTweight);
                        TWeight.setText(String.valueOf(Tweight));
                        TWeight.setTextColor(resolveColorAttr(context, R.attr.textColor));
                        TWeight.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                        TWeight.setTypeface(font);
                        TWeight.setTextSize(12);
                        barsRL.addView(TWeight);

                        RelativeLayout.LayoutParams paramsCweight = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        CWeight.setId(View.generateViewId());
                        paramsCweight.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        paramsCweight.addRule(RelativeLayout.ALIGN_START, R.id.BarGraph3);
                        paramsCweight.addRule(RelativeLayout.ALIGN_END, R.id.BarGraph3);
                        CWeight.setLayoutParams(paramsCweight);
                        CWeight.setText(String.valueOf(Cweight));
                        CWeight.setTextColor(resolveColorAttr(context, R.attr.textColor));
                        CWeight.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                        CWeight.setTypeface(font);
                        CWeight.setTextSize(12);
                        barsRL.addView(CWeight);

                        RelativeLayout.LayoutParams paramsAweight = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        AWeight.setId(View.generateViewId());
                        paramsAweight.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        paramsAweight.addRule(RelativeLayout.ALIGN_START, R.id.BarGraph4);
                        paramsAweight.addRule(RelativeLayout.ALIGN_END, R.id.BarGraph4);
                        AWeight.setLayoutParams(paramsAweight);
                        AWeight.setText(String.valueOf(Aweight));
                        AWeight.setTextColor(resolveColorAttr(context, R.attr.textColor));
                        AWeight.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                        AWeight.setTypeface(font);
                        AWeight.setTextSize(12);
                        barsRL.addView(AWeight);

                        RelativeLayout.LayoutParams paramsOweight = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        OWeight.setId(View.generateViewId());
                        paramsOweight.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        paramsOweight.addRule(RelativeLayout.ALIGN_START, R.id.BarGraph5);
                        paramsOweight.addRule(RelativeLayout.ALIGN_END, R.id.BarGraph5);
                        OWeight.setLayoutParams(paramsOweight);
                        OWeight.setText(String.valueOf(Oweight));
                        OWeight.setTextColor(resolveColorAttr(context, R.attr.textColor));
                        OWeight.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                        OWeight.setTypeface(font);
                        OWeight.setTextSize(12);
                        barsRL.addView(OWeight);

                        RelativeLayout.LayoutParams layoutParamsBar1 = (RelativeLayout.LayoutParams) bar1.getLayoutParams();
                        layoutParamsBar1.height = (int) Math.round(bar1.getHeight() * 2.3);
                        layoutParamsBar1.width = (int) Math.round(bar1.getWidth() * 1.8);
                        layoutParamsBar1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                        layoutParamsBar1.addRule(RelativeLayout.ABOVE, KWeight.getId());
                        layoutParamsBar1.setMarginStart(0);
                        bar1.setLayoutParams(layoutParamsBar1);

                        RelativeLayout.LayoutParams layoutParamsBar2 = (RelativeLayout.LayoutParams) bar2.getLayoutParams();
                        layoutParamsBar2.height = (int) Math.round(bar2.getHeight() * 2.3);
                        layoutParamsBar2.width = (int) Math.round(bar2.getWidth() * 1.8);
                        layoutParamsBar2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                        layoutParamsBar2.addRule(RelativeLayout.ABOVE, TWeight.getId());
                        layoutParamsBar2.setMarginStart(10);
                        bar2.setLayoutParams(layoutParamsBar2);

                        RelativeLayout.LayoutParams layoutParamsBar3 = (RelativeLayout.LayoutParams) bar3.getLayoutParams();
                        layoutParamsBar3.height = (int) Math.round(bar3.getHeight() * 2.3);
                        layoutParamsBar3.width = (int) Math.round(bar3.getWidth() * 1.8);
                        layoutParamsBar3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                        layoutParamsBar3.addRule(RelativeLayout.ABOVE, CWeight.getId());
                        layoutParamsBar3.setMarginStart(10);
                        bar3.setLayoutParams(layoutParamsBar3);

                        RelativeLayout.LayoutParams layoutParamsBar4 = (RelativeLayout.LayoutParams) bar4.getLayoutParams();
                        layoutParamsBar4.height = (int) Math.round(bar4.getHeight() * 2.3);
                        layoutParamsBar4.width = (int) Math.round(bar4.getWidth() * 1.8);
                        layoutParamsBar4.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                        layoutParamsBar4.addRule(RelativeLayout.ABOVE, AWeight.getId());
                        layoutParamsBar4.setMarginStart(10);
                        bar4.setLayoutParams(layoutParamsBar4);

                        RelativeLayout.LayoutParams layoutParamsBar5 = (RelativeLayout.LayoutParams) bar5.getLayoutParams();
                        layoutParamsBar5.height = (int) Math.round(bar5.getHeight() * 2.3);
                        layoutParamsBar5.width = (int) Math.round(bar5.getWidth() * 1.8);
                        layoutParamsBar5.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                        layoutParamsBar5.addRule(RelativeLayout.ABOVE, OWeight.getId());
                        layoutParamsBar5.setMarginStart(10);
                        bar5.setLayoutParams(layoutParamsBar5);

                        TextView AveragePercent = v.findViewById(R.id.AveragePercent);
                        RelativeLayout.LayoutParams paramsAveragePercent = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsAveragePercent.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        paramsAveragePercent.addRule(RelativeLayout.BELOW, v.findViewById(R.id.title).getId());
                        AveragePercent.setLayoutParams(paramsAveragePercent);


                        feedbackTextView.setId(View.generateViewId()); //without these IDs the bars float in the air for some reason
                        feedbackTextView.setText(Html.fromHtml("<u><b>Feedback:</u></b> <br>" + feedback));
                        if(feedback.isEmpty()){
                            feedbackTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            feedbackTextView.setText(Html.fromHtml("<b>No Feedback</b>"));
                        }
                        feedbackTextView.setTypeface(font);

                        feedbackTextView.setTextColor(resolveColorAttr(context, R.attr.textColor));
                        RelativeLayout.LayoutParams paramsFeedback = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsFeedback.addRule(RelativeLayout.BELOW, R.id.AveragePercent);
                        paramsFeedback.addRule(RelativeLayout.ALIGN_PARENT_END);
                        paramsFeedback.addRule(RelativeLayout.ALIGN_PARENT_START);
                        paramsFeedback.setMarginStart(dpToPx(10));
                        paramsFeedback.setMarginEnd(dpToPx(10));
                        feedbackTextView.setLayoutParams(paramsFeedback);
                        rlNested.addView(feedbackTextView);

                        markFractionK.setText(finalStringKFraction);
                        markFractionT.setText(finalStringTFraction);
                        markFractionC.setText(finalStringCFraction);
                        markFractionA.setText(finalStringAFraction);
                        markFractionO.setText(finalStringOFraction);
                        markFractionK.setTypeface(font);
                        markFractionT.setTypeface(font);
                        markFractionC.setTypeface(font);
                        markFractionA.setTypeface(font);
                        markFractionO.setTypeface(font);

                        markFractionK.setId(View.generateViewId());
                        markFractionT.setId(View.generateViewId());
                        markFractionC.setId(View.generateViewId());
                        markFractionA.setId(View.generateViewId());
                        markFractionO.setId(View.generateViewId());
                        markFractionK.setTextColor(resolveColorAttr(context, R.attr.textColor));
                        markFractionT.setTextColor(resolveColorAttr(context, R.attr.textColor));
                        markFractionC.setTextColor(resolveColorAttr(context, R.attr.textColor));
                        markFractionA.setTextColor(resolveColorAttr(context, R.attr.textColor));
                        markFractionO.setTextColor(resolveColorAttr(context, R.attr.textColor));

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
                        RelativeLayout.LayoutParams markFractionOLP = new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);

                        markFractionKLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        markFractionTLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        markFractionCLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        markFractionALP.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        markFractionOLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        markFractionKLP.addRule(RelativeLayout.BELOW, bar1.findViewById(R.id.Kpercent).getId());
                        markFractionTLP.addRule(RelativeLayout.BELOW, bar2.findViewById(R.id.Tpercent).getId());
                        markFractionCLP.addRule(RelativeLayout.BELOW, bar3.findViewById(R.id.Cpercent).getId());
                        markFractionALP.addRule(RelativeLayout.BELOW, bar4.findViewById(R.id.Apercent).getId());
                        markFractionOLP.addRule(RelativeLayout.BELOW, bar5.findViewById(R.id.Opercent).getId());
                        markFractionK.setLayoutParams(markFractionKLP);
                        markFractionT.setLayoutParams(markFractionTLP);
                        markFractionC.setLayoutParams(markFractionCLP);
                        markFractionA.setLayoutParams(markFractionALP);
                        markFractionO.setLayoutParams(markFractionOLP);

                        bar1.addView(markFractionK);
                        bar2.addView(markFractionT);
                        bar3.addView(markFractionC);
                        bar4.addView(markFractionA);
                        bar5.addView(markFractionO);
                    } else if (rlNested.getHeight() != original_height_of_assignment) {
                        params.height = original_height_of_assignment;

                        rlNested.setLayoutParams(params);

                        RelativeLayout bar1 = v.findViewById(R.id.BarGraph1);
                        RelativeLayout bar2 = v.findViewById(R.id.BarGraph2);
                        RelativeLayout bar3 = v.findViewById(R.id.BarGraph3);
                        RelativeLayout bar4 = v.findViewById(R.id.BarGraph4);
                        RelativeLayout bar5 = v.findViewById(R.id.BarGraph5);

                        RelativeLayout barsRL = v.findViewById(R.id.mark_bars);
                        barsRL.removeView(KWeight);
                        barsRL.removeView(TWeight);
                        barsRL.removeView(CWeight);
                        barsRL.removeView(AWeight);
                        barsRL.removeView(OWeight);
                        rlNested.removeView(feedbackTextView);

                        RelativeLayout.LayoutParams barsRLParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dpToPx(110));
                        barsRLParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                        barsRLParams.setMargins(0, dpToPx(25), dpToPx(20), 0);
                        barsRL.setLayoutParams(barsRLParams);

                        bar1.removeView(markFractionK);
                        bar2.removeView(markFractionT);
                        bar3.removeView(markFractionC);
                        bar4.removeView(markFractionA);
                        bar5.removeView(markFractionO);

                        RelativeLayout.LayoutParams layoutParamsBar1 = (RelativeLayout.LayoutParams) bar1.getLayoutParams();
                        layoutParamsBar1.height = (int) Math.round(bar1.getHeight() / 2.3);
                        layoutParamsBar1.width = (int) Math.round(bar1.getWidth() / 1.8);
                        layoutParamsBar1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        layoutParamsBar1.addRule(RelativeLayout.ABOVE, 0);
                        layoutParamsBar1.setMarginStart(0);
                        bar1.setLayoutParams(layoutParamsBar1);

                        RelativeLayout.LayoutParams layoutParamsBar2 = (RelativeLayout.LayoutParams) bar2.getLayoutParams();
                        layoutParamsBar2.height = (int) Math.round(bar2.getHeight() / 2.3);
                        layoutParamsBar2.width = (int) Math.round(bar2.getWidth() / 1.8);
                        layoutParamsBar2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        layoutParamsBar2.addRule(RelativeLayout.ABOVE, 0);
                        layoutParamsBar2.setMarginStart(dpToPx(2));
                        bar2.setLayoutParams(layoutParamsBar2);

                        RelativeLayout.LayoutParams layoutParamsBar3 = (RelativeLayout.LayoutParams) bar3.getLayoutParams();
                        layoutParamsBar3.height = (int) Math.round(bar3.getHeight() / 2.3);
                        layoutParamsBar3.width = (int) Math.round(bar3.getWidth() / 1.8);
                        layoutParamsBar3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        layoutParamsBar3.addRule(RelativeLayout.ABOVE, 0);
                        layoutParamsBar3.setMarginStart(dpToPx(2));
                        bar3.setLayoutParams(layoutParamsBar3);

                        RelativeLayout.LayoutParams layoutParamsBar4 = (RelativeLayout.LayoutParams) bar4.getLayoutParams();
                        layoutParamsBar4.height = (int) Math.round(bar4.getHeight() / 2.3);
                        layoutParamsBar4.width = (int) Math.round(bar4.getWidth() / 1.8);
                        layoutParamsBar4.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        layoutParamsBar4.addRule(RelativeLayout.ABOVE, 0);
                        layoutParamsBar4.setMarginStart(dpToPx(2));
                        bar4.setLayoutParams(layoutParamsBar4);

                        RelativeLayout.LayoutParams layoutParamsBar5 = (RelativeLayout.LayoutParams) bar5.getLayoutParams();
                        layoutParamsBar5.height = (int) Math.round(bar5.getHeight() / 2.3);
                        layoutParamsBar5.width = (int) Math.round(bar5.getWidth() / 1.8);
                        layoutParamsBar5.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        layoutParamsBar5.addRule(RelativeLayout.ABOVE, 0);
                        layoutParamsBar5.setMarginStart(dpToPx(2));
                        bar5.setLayoutParams(layoutParamsBar5);


                        TextView AveragePercent = v.findViewById(R.id.AveragePercent);
                        RelativeLayout.LayoutParams paramsAveragePercent = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsAveragePercent.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        paramsAveragePercent.addRule(RelativeLayout.ALIGN_PARENT_START);
                        AveragePercent.setLayoutParams(paramsAveragePercent);


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

                            linearLayout.removeViewAt(index + 2 - toSubtract);
                            Marks.remove(String.valueOf(index));
                            removedAssignmentIndexList.add(index);
                            numberOfRemovedAssignments++;
                            TA ta = new TA();
                            String returnval = ta.CalculateCourseAverageFromAssignments(Marks, numberOfRemovedAssignments, null);
                            TextView AverageInt = fragment.findViewById(R.id.semesterAverageTV);
                            AverageInt.setText(returnval + "%");
                            int Average = Math.round(Float.parseFloat(returnval));
                            final RingProgressBar ProgressBarAverage = (RingProgressBar) fragment.findViewById(R.id.AverageBar);
                            ProgressBarAverage.setProgress(Average);

                            setupCourseBars(marks);

                        }
                    });

            trashButton.setVisibility(View.INVISIBLE);

            // Setup toolbar text
            ((TextView)activity.findViewById(R.id.toolbar_title)).setText(courseCode);
            activity.getSupportActionBar().setTitle("");
            Crashlytics.log(Log.DEBUG, "coursename", courseCode);
            //set title
            TextView Title = rl.findViewById(R.id.title);
            Title.setText(title);

            //this code creates a dictionary that the CalculateAverage method uses to
            // multiply the course weighting by the assignment weightings
            HashMap weightDict = new HashMap<String, Double>();
            if(Kweight == 0.0 && Tweight == 0.0 && Cweight == 0.0 && Aweight == 0.0 && Oweight == 0.0) {
                weightDict.put("K", 1.0);
                weightDict.put("T", 1.0);
                weightDict.put("C", 1.0);
                weightDict.put("A", 1.0);
                weightDict.put("O", 1.0);
            }else {
                weightDict.put("K", Kweight);
                weightDict.put("T", Tweight);
                weightDict.put("C", Cweight);
                weightDict.put("A", Aweight);
                weightDict.put("O", Oweight);
            }

            //set mark
            TextView Average = rl.findViewById(R.id.AveragePercent);
            String returnval = CalculateAverage(marks, String.valueOf(i), weightDict);
            if(returnval == null || returnval.equals("NaN")||returnval.equals("null")){
                Average.setText("No Mark");
            }else {
                Average.setText(returnval + "%");
            }


            //set bars
            View bar1 = rl.findViewById(R.id.BarGraph1);
            View bar2 = rl.findViewById(R.id.BarGraph2);
            View bar3 = rl.findViewById(R.id.BarGraph3);
            View bar4 = rl.findViewById(R.id.BarGraph4);
            View bar5 = rl.findViewById(R.id.BarGraph5);
            bar1.getLayoutParams().height = dpToPx(Math.round(0.7 * (Kmark)) + 20);
            bar2.getLayoutParams().height = dpToPx(Math.round(0.7 * (Tmark)) + 20);
            bar3.getLayoutParams().height = dpToPx(Math.round(0.7 * (Cmark)) + 20);
            bar4.getLayoutParams().height = dpToPx(Math.round(0.7 * (Amark)) + 20);
            bar5.getLayoutParams().height = dpToPx(Math.round(0.7 * (Omark)) + 20);

            //set percentage texts
            TextView Kpercent = rl.findViewById(R.id.Kpercent);
            TextView Tpercent = rl.findViewById(R.id.Tpercent);
            TextView Cpercent = rl.findViewById(R.id.Cpercent);
            TextView Apercent = rl.findViewById(R.id.Apercent);
            TextView Opercent = rl.findViewById(R.id.Opercent);

            if (Kmark == 100.0) {
                Kpercent.setText(String.valueOf(Math.round(Kmark)));
            } else if (Kmark == 0.0) {
                Kpercent.setText("0.0");
            } else if (Kmark == 0.000000001) {
                bar1.setBackground(context.getTheme().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
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
                bar2.setBackground(context.getTheme().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
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
                bar3.setBackground(context.getTheme().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
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
                bar4.setBackground(context.getTheme().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
                Apercent.setTextColor(Color.WHITE);
                Apercent.setText("NA");
            } else {
                Apercent.setText(String.valueOf(Amark));
            }

            if (Omark == 100.0) {
                Opercent.setText(String.valueOf(Math.round(Omark)));
            } else if (Amark == 0.0) {
                Opercent.setText("0.0");
            } else if (Omark == 0.000000001) {
                bar5.setBackground(context.getTheme().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
                Opercent.setTextColor(Color.WHITE);
                Opercent.setText("NA");
            } else {
                Opercent.setText(String.valueOf(Omark));
            }

            if(!isCancelled) {
                dialog.dismiss();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String CalculateAverage(JSONObject marks, String assingmentNumber, HashMap weightDict){
        try {
            JSONObject weights = marks.getJSONObject("categories");
            Double weightK = weights.getDouble("K")*10 * 0.7 * ((Double)weightDict.get("K"));
            Double weightT = weights.getDouble("T")*10 * 0.7 * ((Double)weightDict.get("T"));
            Double weightC = weights.getDouble("C")*10 * 0.7 * ((Double)weightDict.get("C"));
            Double weightA = weights.getDouble("A")*10 * 0.7 * ((Double)weightDict.get("A"));
            Double weightO = 3.0;
            Double Kmark = 0.0;
            Double Tmark = 0.0;
            Double Cmark = 0.0;
            Double Amark = 0.0;
            Double Omark = 0.0;
            DecimalFormat round = new DecimalFormat(".#");
            JSONObject assignment = marks.getJSONObject(assingmentNumber);

            if(assignment.has("")) {
                if(assignment.getJSONObject("").getString("mark").equals("no mark")){
                    weightO = 0.0;
                }else {
                    if (!assignment.getJSONObject("").getString("outOf").equals("0") || !assignment.getJSONObject("").getString("outOf").equals("0.0")) {
                        if (!assignment.getJSONObject("").isNull("mark")) {
                            Omark = Double.parseDouble(assignment.getJSONObject("").getString("mark")) /
                                    Double.parseDouble(assignment.getJSONObject("").getString("outOf"));
                        } else {
                            weightO = 0.0;
                        }
                    }
                }
            }else{
                weightO = 0.0;
            }

            if(assignment.has("K")) {
                if(assignment.getJSONObject("K").getString("mark").equals("no mark")){
                    weightK = 0.0;
                }else {
                    if (!assignment.getJSONObject("K").getString("outOf").equals("0") || !assignment.getJSONObject("K").getString("outOf").equals("0.0")) {
                        if (!assignment.getJSONObject("K").isNull("mark")) {
                            Kmark = Double.parseDouble(assignment.getJSONObject("K").getString("mark")) /
                                    Double.parseDouble(assignment.getJSONObject("K").getString("outOf"));
                        } else {
                            weightK = 0.0;
                        }
                    }
                }
            }else{
                weightK = 0.0;
            }
            if(assignment.has("T")) {
                if(assignment.getJSONObject("T").getString("mark").equals("no mark")){
                    weightT = 0.0;
                }else {
                    if (!assignment.getJSONObject("T").getString("outOf").equals("0") || !assignment.getJSONObject("T").getString("outOf").equals("0.0")) {
                        if (!assignment.getJSONObject("T").isNull("mark")) {
                            Tmark = Double.parseDouble(assignment.getJSONObject("T").getString("mark")) /
                                    Double.parseDouble(assignment.getJSONObject("T").getString("outOf"));
                        } else {
                            weightT = 0.0;
                        }
                    }
                }
            }else{
                weightT = 0.0;
            }
            if(assignment.has("C")) {
                if(assignment.getJSONObject("C").getString("mark").equals("no mark")){
                    weightC = 0.0;
                }else {
                    if (!assignment.getJSONObject("C").getString("outOf").equals("0") || !assignment.getJSONObject("C").getString("outOf").equals("0.0")) {
                        if (!assignment.getJSONObject("C").isNull("mark")) {
                            Cmark = Double.parseDouble(assignment.getJSONObject("C").getString("mark")) /
                                    Double.parseDouble(assignment.getJSONObject("C").getString("outOf"));
                        } else {
                            weightC = 0.0;
                        }
                    }
                }
            }else{
                weightC = 0.0;
            }
            if(assignment.has("A")) {
                if(assignment.getJSONObject("A").getString("mark").equals("no mark")){
                    weightA = 0.0;
                }else {
                    if (!assignment.getJSONObject("A").getString("outOf").equals("0") || !assignment.getJSONObject("A").getString("outOf").equals("0.0")) {
                        if (!assignment.getJSONObject("A").isNull("mark")) {
                            Amark = Double.parseDouble(assignment.getJSONObject("A").getString("mark")) /
                                    Double.parseDouble(assignment.getJSONObject("A").getString("outOf"));
                        } else {
                            weightA = 0.0;
                        }
                    }
                }
            }else{
                weightA = 0.0;
            }

            Kmark*=weightK;
            Tmark*=weightT;
            Cmark*=weightC;
            Amark*=weightA;
            Omark*=weightO;
            String Average = round.format((Kmark+Tmark+Cmark+Amark+Omark)/(weightK+weightT+weightC+weightA+weightO)*100).replaceAll(",", ".");
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


    public List<String> GetWeightAndAverageByCategory(JSONObject marks) {
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
                        if(KweightAssignmentTemp != -1.0) {
                            Kmark += assignmentK * KweightAssignmentTemp;
                            KweightAssignment += KweightAssignmentTemp;
                        }
                    }
                }catch (JSONException e){}

                try {
                    if (!assignment.getJSONObject("T").isNull("mark") && !assignment.getJSONObject("T").getString("mark").equals("no mark")) {
                        Double assignmentT = Double.parseDouble(assignment.getJSONObject("T").getString("mark")) /
                                Double.parseDouble(assignment.getJSONObject("T").getString("outOf"));
                        TweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("T").getString("weight"));
                        if(TweightAssignmentTemp != -1.0) {
                            Tmark += assignmentT * TweightAssignmentTemp;
                            TweightAssignment += TweightAssignmentTemp;
                        }
                    }
                }catch (JSONException e){}

                try {
                    if (!assignment.getJSONObject("C").isNull("mark") && !assignment.getJSONObject("C").getString("mark").equals("no mark")) {
                        Double assignmentC = Double.parseDouble(assignment.getJSONObject("C").getString("mark")) /
                                Double.parseDouble(assignment.getJSONObject("C").getString("outOf"));
                        CweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("C").getString("weight"));
                        if(CweightAssignmentTemp != -1.0) {
                            Cmark += assignmentC * CweightAssignmentTemp;
                            CweightAssignment += CweightAssignmentTemp;
                        }
                    }
                }catch (JSONException e){}

                try {
                    if (!assignment.getJSONObject("A").isNull("mark") && !assignment.getJSONObject("A").getString("mark").equals("no mark")) {
                        Double assignmentA = Double.parseDouble(assignment.getJSONObject("A").getString("mark")) /
                                Double.parseDouble(assignment.getJSONObject("A").getString("outOf"));
                        AweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("A").getString("weight"));
                        if(AweightAssignmentTemp != -1.0) {
                            Amark += assignmentA * AweightAssignmentTemp;
                            AweightAssignment += AweightAssignmentTemp;
                        }
                    }
                }catch (JSONException e){}

                try {
                    if (!assignment.getJSONObject("").isNull("mark") && !assignment.getJSONObject("").getString("mark").equals("no mark")) {
                        Double assignmentS = Double.parseDouble(assignment.getJSONObject("").getString("mark")) /
                                Double.parseDouble(assignment.getJSONObject("").getString("outOf"));
                        SweightAssignmentTemp = Double.parseDouble(assignment.getJSONObject("").getString("weight"));
                        if(SweightAssignmentTemp != -1.0) {
                            Smark += assignmentS * SweightAssignmentTemp;
                            SweightAssignment += SweightAssignmentTemp;
                        }
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

            Kmark *= 100;
            Tmark *= 100;
            Cmark *= 100;
            Amark *= 100;
            Smark *= 100;

            weightK *= 10;
            weightT *= 10;
            weightC *= 10;
            weightA *= 10;
            weightS *= 10;
            return Arrays.asList(String.valueOf(Kmark), String.valueOf(Tmark), String.valueOf(Cmark), String.valueOf(Amark),String.valueOf(Smark), String.valueOf(weightK), String.valueOf(weightT), String.valueOf(weightC), String.valueOf(weightA), String.valueOf(weightS));


        }catch (JSONException e){
            e.printStackTrace();
            Crashlytics.log(Log.ERROR, "MarksViewMaterial Calculate total average returns null", Arrays.toString(e.getStackTrace()));
            return null;
        }
    }
    private void setupCourseBars(JSONObject marks){
        final DecimalFormat round = new DecimalFormat("#.#");
        //setup course bars
        RelativeLayout barAverageRL = fragment.findViewById(R.id.mark_bars);

        TextView weightKAverage = fragment.findViewById(R.id.weightKAverage);
        TextView weightTAverage = fragment.findViewById(R.id.weightTAverage);
        TextView weightCAverage = fragment.findViewById(R.id.weightCAverage);
        TextView weightAAverage = fragment.findViewById(R.id.weightAAverage);
        TextView weightOAverage = fragment.findViewById(R.id.weightOAverage);

        TextView KpercentAverage = fragment.findViewById(R.id.KpercentAverage);
        TextView TpercentAverage = fragment.findViewById(R.id.TpercentAverage);
        TextView CpercentAverage = fragment.findViewById(R.id.CpercentAverage);
        TextView ApercentAverage = fragment.findViewById(R.id.ApercentAverage);
        TextView OpercentAverage = fragment.findViewById(R.id.OpercentAverage);

        View BarAverage1 = fragment.findViewById(R.id.BarAverage1);
        View BarAverage2 = fragment.findViewById(R.id.BarAverage2);
        View BarAverage3 = fragment.findViewById(R.id.BarAverage3);
        View BarAverage4 = fragment.findViewById(R.id.BarAverage4);
        View BarAverage5 = fragment.findViewById(R.id.BarAverage5);
        List<String> list = GetWeightAndAverageByCategory(marks);


        RelativeLayout.LayoutParams layoutParamsBar1 = (RelativeLayout.LayoutParams) BarAverage1.getLayoutParams();
        layoutParamsBar1.height = dpToPx(Math.round(Double.parseDouble(list.get(0)) * 0.7 + 40));
        BarAverage1.setLayoutParams(layoutParamsBar1);

        RelativeLayout.LayoutParams layoutParamsBar2 = (RelativeLayout.LayoutParams) BarAverage2.getLayoutParams();
        layoutParamsBar2.height = dpToPx(Math.round(Double.parseDouble(list.get(1)) * 0.7 + 40));
        BarAverage2.setLayoutParams(layoutParamsBar2);

        RelativeLayout.LayoutParams layoutParamsBar3 = (RelativeLayout.LayoutParams) BarAverage3.getLayoutParams();
        layoutParamsBar3.height = dpToPx(Math.round(Double.parseDouble(list.get(2)) * 0.7 + 40));
        BarAverage3.setLayoutParams(layoutParamsBar3);

        RelativeLayout.LayoutParams layoutParamsBar4 = (RelativeLayout.LayoutParams) BarAverage4.getLayoutParams();
        layoutParamsBar4.height = dpToPx(Math.round(Double.parseDouble(list.get(3)) * 0.7 + 40));
        BarAverage4.setLayoutParams(layoutParamsBar4);

        RelativeLayout.LayoutParams layoutParamsBar5 = (RelativeLayout.LayoutParams) BarAverage5.getLayoutParams();
        layoutParamsBar5.height = dpToPx(Math.round(Double.parseDouble(list.get(4)) * 0.7 + 40));
        BarAverage5.setLayoutParams(layoutParamsBar5);

        if(round.format(Double.parseDouble(list.get(0))).replaceAll(",", ".").equals("0")){
            KpercentAverage.setText("NA");
            BarAverage1.setBackground(context.getTheme().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
            KpercentAverage.setTextColor(context.getTheme().getResources().getColor(R.color.textColor));
            ((TextView)fragment.findViewById(R.id.K)).setTextColor(context.getTheme().getResources().getColor(R.color.textColor));
        }else {
            KpercentAverage.setText(round.format(Double.parseDouble(list.get(0))).replaceAll(",", "."));
            BarAverage1.setBackground(context.getTheme().getDrawable(R.drawable.rounded_rectangle_bar_graph));
            KpercentAverage.setTextColor(resolveColorAttr(context, R.attr.textColor));
            ((TextView)fragment.findViewById(R.id.K)).setTextColor(resolveColorAttr(context, R.attr.textColor));
        }

        if(round.format(Double.parseDouble(list.get(1))).replaceAll(",", ".").equals("0")){
            TpercentAverage.setText("NA");
            BarAverage2.setBackground(context.getTheme().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
            TpercentAverage.setTextColor(context.getTheme().getResources().getColor(R.color.textColor));
            ((TextView)fragment.findViewById(R.id.T)).setTextColor(context.getTheme().getResources().getColor(R.color.textColor));
        }else {
            TpercentAverage.setText(round.format(Double.parseDouble(list.get(1))).replaceAll(",", "."));
            BarAverage2.setBackground(context.getTheme().getDrawable(R.drawable.rounded_rectangle_bar_graph));
            TpercentAverage.setTextColor(resolveColorAttr(context, R.attr.textColor));
            ((TextView)fragment.findViewById(R.id.T)).setTextColor(resolveColorAttr(context, R.attr.textColor));
        }

        if(round.format(Double.parseDouble(list.get(2))).replaceAll(",", ".").equals("0")){
            CpercentAverage.setText("NA");
            BarAverage3.setBackground(context.getTheme().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
            CpercentAverage.setTextColor(context.getTheme().getResources().getColor(R.color.textColor));
            ((TextView)fragment.findViewById(R.id.C)).setTextColor(context.getTheme().getResources().getColor(R.color.textColor));
        }else {
            CpercentAverage.setText(round.format(Double.parseDouble(list.get(2))).replaceAll(",", "."));
            BarAverage3.setBackground(context.getTheme().getDrawable(R.drawable.rounded_rectangle_bar_graph));
            CpercentAverage.setTextColor(resolveColorAttr(context, R.attr.textColor));
            ((TextView)fragment.findViewById(R.id.C)).setTextColor(resolveColorAttr(context, R.attr.textColor));
        }

        if(round.format(Double.parseDouble(list.get(3))).replaceAll(",", ".").equals("0")){
            ApercentAverage.setText("NA");
            BarAverage4.setBackground(context.getTheme().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
            ApercentAverage.setTextColor(context.getTheme().getResources().getColor(R.color.textColor));
            ((TextView)fragment.findViewById(R.id.A)).setTextColor(context.getTheme().getResources().getColor(R.color.textColor));
        }else {
            ApercentAverage.setText(round.format(Double.parseDouble(list.get(3))).replaceAll(",", "."));
            BarAverage4.setBackground(context.getTheme().getDrawable(R.drawable.rounded_rectangle_bar_graph));
            ApercentAverage.setTextColor(resolveColorAttr(context, R.attr.textColor));
            ((TextView)fragment.findViewById(R.id.A)).setTextColor(resolveColorAttr(context, R.attr.textColor));
        }
        if(round.format(Double.parseDouble(list.get(4))).replaceAll(",", ".").equals("0")){
            OpercentAverage.setText("NA");
            BarAverage5.setBackground(context.getTheme().getDrawable(R.drawable.rounded_rectangle_bar_graph_pink));
            OpercentAverage.setTextColor(context.getTheme().getResources().getColor(R.color.textColor));
            ((TextView)fragment.findViewById(R.id.O)).setTextColor(context.getTheme().getResources().getColor(R.color.textColor));
        }else {
            OpercentAverage.setText(round.format(Double.parseDouble(list.get(4))).replaceAll(",", "."));
            BarAverage5.setBackground(context.getTheme().getDrawable(R.drawable.rounded_rectangle_bar_graph));
            OpercentAverage.setTextColor(resolveColorAttr(context, R.attr.textColor));
            ((TextView)fragment.findViewById(R.id.O)).setTextColor(resolveColorAttr(context, R.attr.textColor));
        }

        weightKAverage.setText(round.format(Double.parseDouble(list.get(5))).replaceAll(",", "."));
        weightTAverage.setText(round.format(Double.parseDouble(list.get(6))).replaceAll(",", "."));
        weightCAverage.setText(round.format(Double.parseDouble(list.get(7))).replaceAll(",", "."));
        weightAAverage.setText(round.format(Double.parseDouble(list.get(8))).replaceAll(",", "."));
        weightOAverage.setText(round.format(Double.parseDouble(list.get(9))).replaceAll(",", "."));

        RelativeLayout.LayoutParams barAverageRLParams = (RelativeLayout.LayoutParams) barAverageRL.getLayoutParams();
        barAverageRLParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        barAverageRL.setLayoutParams(barAverageRLParams);
    }

    private int dpToPx(double dp) {
        float fdp =(float) dp;
        return Math.round(fdp * context.getResources().getDisplayMetrics().density);
    }
    @ColorInt
    public static int resolveColorAttr(Context context, @AttrRes int colorAttr) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(colorAttr, typedValue, true);
        TypedArray arr =context.obtainStyledAttributes(typedValue.data, new int[]{
                colorAttr});
        return arr.getColor(0, -1);
    }
}
